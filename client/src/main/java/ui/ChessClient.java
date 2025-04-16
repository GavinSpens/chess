package ui;

import java.util.*;

import chess.*;
import exception.ResponseException;
import model.*;
import serverfacade.ServerFacade;
import websocket.WebSocketFacade;
import websocket.NotificationHandler;

public class ChessClient {
    private UserData userData = null;
    private String authToken = null;
    private final ServerFacade server;
    public State state = State.SIGNED_OUT;
    private GameData[] games = null;

    public GameData currentGame = null;
    public String playerColor = "";

    private WebSocketFacade ws = null;
    private final NotificationHandler repl;
    private final String url = "http://localhost:8080";


    public ChessClient(String serverUrl, NotificationHandler repl) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "register", "r" -> register(params);
            case "login", "l" -> login(params);
            case "logout" -> logout(params);
            case "listgames", "list" -> listGames(params);
            case "creategame", "create", "c" -> createGame(params);
            case "joingame", "join", "j" -> joinGame(params);
            case "observegame", "o", "observe" -> observeGame(params);
            case "redraw" -> redraw(params);
            case "highlight" -> highlightLegalMoves(params);
            case "leave" -> leave(params);
            case "makemove", "m", "move" -> makeMove(params);
            case "resign" -> resign(params);
            case "quit", "q" -> "quit";
            default -> help();
            };
    }

    private String leave(String... ignored) throws ResponseException {
        ws.leaveGame(authToken, currentGame.gameID());
        ws = null;

        state = State.SIGNED_IN;
        currentGame = null;
        playerColor = "";
        return "Left Game";
    }

    private ChessPiece.PieceType getPromoPieceTypeFromString(String param) {
        Map<String, ChessPiece.PieceType> mappings = Map.of(
                "knight", ChessPiece.PieceType.KNIGHT,
                "bishop", ChessPiece.PieceType.BISHOP,
                "rook", ChessPiece.PieceType.ROOK,
                "queen", ChessPiece.PieceType.QUEEN
        );
        return mappings.get(param);
    }
    
    private ChessMove getMoveFromStr(String... params) throws ResponseException {
        ChessPosition fromPos = getPosFromStr(params[0]);
        ChessPosition toPos = getPosFromStr(params[1]);
        if (params.length == 3) {
            ChessPiece.PieceType promo = getPromoPieceTypeFromString(params[2]);
            return new ChessMove(fromPos, toPos, promo);
        }
        return new ChessMove(fromPos, toPos, null);
    }

    private String makeMove(String... params) throws ResponseException {
        if (params.length != 2 && params.length != 3) {
            throw new ResponseException(400, "FAILED\nExpected: <FROM_POS> <TO_POS> <PAWN_PROMOTION_PIECE_TYPE?>");
        }
//        if (state != State.IN_GAME_MY_TURN) {
        if (!currentGame.game().getTeamTurn().toString().equalsIgnoreCase(playerColor)) {
            throw new ResponseException(400, "Not your turn");
        }
        try {
            ChessMove move = getMoveFromStr(params);

            if (currentGame.end()) {
                return EscapeSequences.SET_TEXT_COLOR_RED
                        + "Game is over.";
            }

            currentGame.game().makeMove(move);
            state = State.IN_GAME_NOT_MY_TURN;

            ws.makeMove(authToken, currentGame.getId(), move);

//            return gameString(currentGame, playerColor, null);
            return "";
        } catch (ResponseException ignored) {
            throw new ResponseException(400, "FAILED\nExpected: <FROM_POS> <TO_POS> <PAWN_PROMOTION_PIECE_TYPE?>");
        } catch (InvalidMoveException e) {
            throw new ResponseException(400, "FAILED\nIllegal Move");
        }
    }

    private String resign(String... ignored) throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print(
                EscapeSequences.SET_TEXT_COLOR_RED
                        + "Are you sure you want to resign? (y/n)\n"
                        + EscapeSequences.RESET_TEXT_COLOR
                        + ">>> "
        );
        switch (scanner.nextLine()) {
            case "y", "Y", "yes", "Yes", "YES" -> {
                // continue
            }
            case "n", "N", "no", "No", "NO" -> {
                return "Resignation cancelled";
            }
            default -> {
                System.out.print(
                        EscapeSequences.SET_TEXT_COLOR_RED
                        + "Please enter y or n\n"
                );
                return resign("");
            }
        }

        state = State.IN_GAME_NOT_MY_TURN;
        ws.resign(authToken, currentGame.getId());
        return "Resigned";
    }

    private ChessPosition getPosFromStr(String pos) throws ResponseException {
        if (pos.length() != 2) {
            throw new ResponseException(400, "FAILED\nExpected: <POS>\nEXAMPLES: A1 or h8\n");
        }
        String col = String.valueOf(pos.charAt(0));
        String row = String.valueOf(pos.charAt(1));

        try {
            Map<String, Integer> mappings = Map.of(
                    "a", 1,
                    "b", 2,
                    "c", 3,
                    "d", 4,
                    "e", 5,
                    "f", 6,
                    "g", 7,
                    "h", 8
            );
            int intRow = Integer.parseInt(row);
            int intCol;
            if (mappings.containsKey(col)) {
                intCol = mappings.get(col);
            } else {
                intCol = Integer.parseInt(col);
            }
            if (!(0 < intCol && intCol <= 8 && 0 < intRow && intRow <= 8)) {
                throw new Exception("");
            }
            return new ChessPosition(intRow, intCol);
        } catch (Exception e) {
            throw new ResponseException(400, "FAILED\nExpected: <POS>\nEXAMPLES: A1 or h8\n");
        }
    }

    private String highlightLegalMoves(String... params) throws ResponseException {
        assertInGame();
        if (params.length == 1) {
            ChessPosition selectedPiece = getPosFromStr(params[0]);
            if (currentGame.game().getBoard().getPiece(selectedPiece) == null) {
                throw new ResponseException(400, "No piece at given position");
            }
            return gameString(currentGame, playerColor, selectedPiece);
        }
        throw new ResponseException(400, "FAILED\nExpected: <POS>\nEXAMPLES: A1 or h8\n");
    }

    private void assertInGame() throws ResponseException {
        if (state != State.IN_GAME_NOT_MY_TURN
                && state != State.IN_GAME_MY_TURN
                && state != State.OBSERVING) {
            throw new ResponseException(400, "Join or observe a game first");
        }
    }

    private String redraw(String... ignored) throws ResponseException {
        assertInGame();
        return gameString(currentGame, playerColor, null);
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            UserData input = new UserData(params[0], params[1], params[2]);
            RegisterResult registerResult = server.register(input);

            userData = input;
            state = State.SIGNED_IN;
            authToken = registerResult.getAuthToken();

            return String.format("You signed in as %s.\n\nCurrent Games:\n%s", userData.getUsername(), listGames());
        }
        throw new ResponseException(400, "FAILED\nExpected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            LoginRequest input = new LoginRequest(params[0], params[1]);
            LoginResult loginResult = server.login(input);

            userData = new UserData(input);
            state = State.SIGNED_IN;
            authToken = loginResult.getAuthToken();

            return String.format("You signed in as %s.\n\nCurrent Games:\n%s", userData.getUsername(), listGames());
        }
        throw new ResponseException(400, "FAILED\nExpected: <USERNAME> <PASSWORD>");
    }

    public String logout(String... ignored) throws ResponseException {
        assertSignedIn();
        server.logout(authToken);
        return "Logged out successfully";
    }

    public String listGames(String... ignored) throws ResponseException {
        assertSignedIn();
        var result = server.listGames(authToken);

        games = result.getGames();
        StringBuilder output = new StringBuilder();
        output.append("GameId, GameName, WhitePlayer, BlackPlayer\n");
        for (int i = 0; i < games.length; i++) {
            var game = games[i];
            output.append(String.format(
                    "%d, %s, %s, %s\n",
                    i + 1,
                    game.gameName(),
                    game.whiteUsername(),
                    game.blackUsername()
            ));
        }
        return output.toString();
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            assertSignedIn();
            var input = new CreateGameRequest(params[0], authToken);
            server.createGame(input);

            return String.format("Created Game '%s'.",
                    params[0]
            );
        }
        throw new ResponseException(400, "FAILED\nExpected: <GAME_NAME>");
    }

    public String joinGame(String... params) throws ResponseException {
        String ignored = listGames();
        if (params.length == 2) {
            assertSignedIn();
            var id = Integer.parseInt(params[0]);
            try {
                GameData game = games[id - 1];
                id = game.gameID();

                // join game
                var input = new JoinGameRequest(params[1], id, authToken);
                server.joinGame(input);
                state = State.IN_GAME_NOT_MY_TURN;
                currentGame = game;
                playerColor = params[1];
                if (Objects.equals(playerColor, "white")) {
                    state = State.IN_GAME_MY_TURN;
                }
                ws = new WebSocketFacade(url, repl);
                ws.connectToGame(authToken, game.getId());


                return "Joined game as " + playerColor;

            } catch (IndexOutOfBoundsException e) {
                throw new ResponseException(400, listGames(""));
            }
        }
        throw new ResponseException(400, "FAILED\nExpected: <GAME_ID> [WHITE|BLACK]");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length == 1) {
            assertSignedIn();
            listGames("");

            var id = Integer.parseInt(params[0]);
            GameData game = games[id - 1];
            state = State.OBSERVING;
            currentGame = game;
            playerColor = "WHITE";
            ws = new WebSocketFacade(url, repl);
            ws.connectToGame(authToken, game.getId());

            return "Observing game: " + game.gameName();
        }
        throw new ResponseException(400, "FAILED\nExpected: <GAME_ID");
    }

    public String help() {
        if (state == State.SIGNED_OUT) {
            return """
                    - help
                    - quit
                    - login <USERNAME> <PASSWORD>
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    """;
        }
        if (state == State.IN_GAME_NOT_MY_TURN || state == State.IN_GAME_MY_TURN) {
            return """
                    - help
                    - redraw
                    - leave
                    - makeMove <FROM_POS> <TO_POS> <PAWN_PROMOTION_PIECE_TYPE>?
                    - resign
                    - highlight <POS>
                    """;
        }
        return """
                - help
                - logout
                - quit
                - createGame <GAME_NAME>
                - listGames
                - join <GAME_ID> [WHITE|BLACK]
                - observeGame <GAME_ID>
                """;
    }

    public String gameString(GameData gameData, String playerColor, ChessPosition selectedPiecePos) throws ResponseException {
        ArrayList<ChessMove> pieceMoves = new ArrayList<>();
        if (selectedPiecePos != null) {
            pieceMoves = (ArrayList<ChessMove>) gameData.game().validMoves(selectedPiecePos);
        }

        var gameBoard = gameData.game().getBoard().board;
        StringBuilder output = new StringBuilder("\n");
        if (playerColor.equalsIgnoreCase("WHITE")) {
            output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE);
            output.append("    a  b  c  d  e  f  g  h   \n");
            for (int i = 0; i < 8; i++) {
                output.append(String.format(" %d ", 8 - i));
                for (int j = 0; j < 8; j++) {
                    output.append(swapColor(8 - i, j + 1, pieceMoves));
                    if (Objects.equals(selectedPiecePos, new ChessPosition(8 - i, j + 1))) {
                        output.append(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }
                    output.append(chessPiece(gameBoard[7 - i][j]));
                }
                output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE);
                output.append(String.format(" %d \n", 8 - i));
            }
            output.append("    a  b  c  d  e  f  g  h   \n");
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE);
            output.append("    h  g  f  e  d  c  b  a   \n");
            for (int i = 0; i < 8; i++) {
                output.append(String.format(" %d ", i + 1));
                for (int j = 0; j < 8; j++) {
                    output.append(swapColor(i + 1, 8 - j, pieceMoves));
                    if (Objects.equals(selectedPiecePos, new ChessPosition(i + 1, 8 - j))) {
                        output.append(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }
                    output.append(chessPiece(gameBoard[i][7 - j]));
                }
                output.append(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE);
                output.append(String.format(" %d \n", i + 1));
            }
            output.append("    h  g  f  e  d  c  b  a   \n");
            output.append(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        } else {
            throw new ResponseException(400, "FAILED\nExpected: <GAME_ID> [WHITE|BLACK]");
        }
        return output.toString();
    }

    private String swapColor(int i, int j, ArrayList<ChessMove> pieceMoves) {
        if (pieceMoves.isEmpty()) {
            if ((i + j) % 2 != 0) {
                return EscapeSequences.SET_BG_COLOR_WHITE;
            } else {
                return EscapeSequences.SET_BG_COLOR_BLACK;
            }
        } else {
            var thisPos = new ChessPosition(i, j);
            if (pieceMoves.stream().map(ChessMove::getEndPosition).toList().contains(thisPos)) {
                if ((i + j) % 2 != 0) {
                    return EscapeSequences.SET_BG_COLOR_GREEN;
                } else {
                    return EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                }
            } else {
                return swapColor(i, j, new ArrayList<>());
            }
        }
    }

    private String chessPiece(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        String setTextColor;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            setTextColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
        } else {
            setTextColor = EscapeSequences.SET_TEXT_COLOR_RED;
        }
        return setTextColor + switch (piece.getPieceType()) {
            case KING   -> " K ";
            case QUEEN  -> " Q ";
            case ROOK   -> " R ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case PAWN   -> " P ";
        } + EscapeSequences.RESET_TEXT_COLOR;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNED_OUT) {
            throw new ResponseException(400, "Please log in");
        }
    }
}