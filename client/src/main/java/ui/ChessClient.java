package ui;

import java.util.Arrays;

import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;
import model.*;
import serverfacade.ServerFacade;

public class ChessClient {
    private UserData userData = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.SIGNED_OUT;
    private GameData[] games = null;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register"    -> register(params);
                case "login"       -> login(params);
                case "logout"      -> logout(params);
                case "listgames"   -> listGames(params);
                case "creategame"  -> createGame(params);
                case "join"        -> joinGame(params);
                case "observegame" -> observeGame(params);
                case "quit"        -> "quit";
                default            -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            UserData input = new UserData(params[0], params[1], params[2]);
            RegisterResult registerResult = server.register(input);

            userData = input;
            state = State.SIGNED_IN;
            authToken = registerResult.getAuthToken();

            return String.format("You signed in as %s.", userData.getUsername());
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

            return String.format("You signed in as %s.", userData.getUsername());
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

            return String.format("""
                            Created Game '%s'
                            Use listGames to get id
                            Use join <GAME_ID> [WHITE|BLACK] to join""",
                    params[0]
            );
        }
        throw new ResponseException(400, "FAILED\nExpected: <GAME_NAME>");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            assertSignedIn();
            var id = Integer.parseInt(params[0]);
            try {
                GameData game = games[id - 1];
                id = game.gameID();

                // join game
                var input = new JoinGameRequest(params[1], id, authToken);
                server.joinGame(input);
                state = State.IN_GAME;

                return EscapeSequences.ERASE_SCREEN + gameString(game, params[1]);
            } catch (IndexOutOfBoundsException e) {
                return listGames("");
            }
        }
        throw new ResponseException(400, "FAILED\nExpected: <GAME_ID> [WHITE|BLACK]");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length == 1) {
            assertSignedIn();
            var id = Integer.parseInt(params[0]);
            GameData game = games[id - 1];
            state = State.OBSERVING;

            return EscapeSequences.ERASE_SCREEN + gameString(game, "WHITE");
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

    private String gameString(GameData gameData, String playerColor) throws ResponseException {
        var gameBoard = gameData.game().getBoard().board;
        StringBuilder output = new StringBuilder("\n");
        if (playerColor.equalsIgnoreCase("WHITE")) {
            output.append("    a  b  c  d  e  f  g  h   \n");
            for (int i = 0; i < 8; i++) {
                output.append(EscapeSequences.RESET_BG_COLOR);
                output.append(String.format(" %d ", 8 - i));
                for (int j = 0; j < 8; j++) {
                    output.append(swapColor(i, j));
                    output.append(chessPiece(gameBoard[7 - i][j]));
                }
                output.append(EscapeSequences.RESET_BG_COLOR);
                output.append(String.format(" %d \n", 8 - i));
            }
            output.append("    a  b  c  d  e  f  g  h   \n");
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            output.append("    h  g  f  e  d  c  b  a   \n");
            for (int i = 0; i < 8; i++) {
                output.append(EscapeSequences.RESET_BG_COLOR);
                output.append(String.format(" %d ", i + 1));
                for (int j = 0; j < 8; j++) {
                    output.append(swapColor(i, j));
                    output.append(chessPiece(gameBoard[i][7 - j]));
                }
                output.append(EscapeSequences.RESET_BG_COLOR);
                output.append(String.format(" %d \n", i + 1));
            }
            output.append("    h  g  f  e  d  c  b  a   \n");
        } else {
            throw new ResponseException(400, "FAILED\nExpected: <GAME_ID> [WHITE|BLACK]");
        }
        return output.toString();
    }

    private String swapColor(int i, int j) {
        if ((i + j) % 2 == 0) {
            return EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        } else {
            return EscapeSequences.SET_BG_COLOR_DARK_GREY;
        }
    }

    private String chessPiece(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        String setTextColor;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            setTextColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
        } else {
            setTextColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
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