package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final SQLDataAccess sqlDataAccess = SQLDataAccess.getInstance();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String username;
        try {
            username = sqlDataAccess.getAuth(command.getAuthToken()).getUsername();
            switch (command.getCommandType()) {
                case CONNECT -> connect(username, command.getGameID(), session);
                case LEAVE -> leave(username);
                case RESIGN -> resign(username, command.getGameID());
                case MAKE_MOVE -> makeMove(message);
            }
        } catch (DataAccessException e) {
            broadcastError(session, e);
        }
    }

    private void broadcastError(Session session, Exception e) {
        Connection c = new Connection("", -1, session);
        Error error = new Error(ServerMessage.ServerMessageType.ERROR, e.getMessage());
        try {
            c.send(error.toJson());
        } catch (IOException ex) {
            // Ignore if we can't reach the client that caused the error
        }
    }

    private void broadcastError(String username, Exception e) {
        try {
            connections.broadcastToUsername(
                    username,
                    new Error(ServerMessage.ServerMessageType.ERROR, e.getMessage())
            );
        } catch (IOException ignored) {
            // do nothing if we can't reach the client that threw the error
        }
    }

    private void validateMove(GameData game, ChessMove move, String username) throws InvalidMoveException {
        ChessGame chessGame = game.game();
        ChessGame.TeamColor teamTurn = chessGame.getTeamTurn();

        if (game.end()) {
            throw new InvalidMoveException("Error: Game is over");
        }

        if (teamTurn == null) {
            throw new InvalidMoveException("Error: Not your turn");
        }

        if (chessGame.getBoard().getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Error: Not your turn");
        }

        if (teamTurn == ChessGame.TeamColor.WHITE) {
            if (!Objects.equals(username, game.whiteUsername())) {
                throw new InvalidMoveException("Wrong color");
            }
        } else {
            if (!Objects.equals(username, game.blackUsername())) {
                throw new InvalidMoveException("Wrong color");
            }
        }
    }

    private void makeMove(String message) throws DataAccessException {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);

        String username = "";
        try {
            GameData game = sqlDataAccess.getGame(command.getGameID());
            ChessMove move = command.getMove();
            username = sqlDataAccess.getAuth(command.getAuthToken()).getUsername();

            validateMove(game, move, username);

            game.game().makeMove(move);
            sqlDataAccess.updateGame(game);

            LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcastToGameExcludeUsername("", loadGame, game.getId());
            Notification notification = new Notification(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    game.game().getTeamTurn() + " to move"
            );
            connections.broadcastToGameExcludeUsername(username, notification, game.gameID());
        } catch (InvalidMoveException | IOException e) {
            broadcastError(username, new Exception("Unable to make specified move"));
        }
    }

    private void connect(String username, int gameId, Session session) throws DataAccessException {
        connections.add(username, gameId, session);
        var message = String.format("%s joined the game", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);

        var loadGame = new LoadGame(
                ServerMessage.ServerMessageType.LOAD_GAME,
                sqlDataAccess.getGame(gameId));

        try {
            connections.broadcastToGameExcludeUsername(username, notification, gameId);
            connections.broadcastToUsername(username, loadGame);
        } catch (IOException e) {
            broadcastError(username, new Exception("Error: Unable to connect to game"));
        }
    }

    private void leave(String username) {
        var message = String.format("%s left the game", username);
        try {
            int gameId = connections.connections.get(username).gameId;
            connections.remove(username);
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            GameData gameData = sqlDataAccess.getGame(gameId);
            GameData data = new GameData(
                    gameId,
                    Objects.equals(gameData.whiteUsername(), username) ? null : gameData.whiteUsername(),
                    Objects.equals(gameData.blackUsername(), username) ? null : gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game(),
                    gameData.end()
            );
            sqlDataAccess.updateGame(data);

            connections.broadcastToGameExcludeUsername(username, notification, gameId);
        } catch (IOException | DataAccessException e) {
            broadcastError(username, new Exception("Unable to leave game"));
        }
    }

    private void validateResign(GameData gameData, String username) throws InvalidMoveException {
        if (gameData.end()) {
            throw new InvalidMoveException("Game has already ended");
        }
        if (!Objects.equals(username, gameData.whiteUsername()) && !Objects.equals(username, gameData.blackUsername())) {
            throw new InvalidMoveException("Only players can resign");
        }
    }

    private void resign(String username, int gameId) {
        try {
            GameData gameData = sqlDataAccess.getGame(gameId);

            validateResign(gameData, username);

            GameData gameOver = new GameData(
                    gameData.getId(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(),
                    gameData.game(), true
            );
            sqlDataAccess.updateGame(gameOver);
        } catch (DataAccessException e) {
            broadcastError(username, new Exception("Error: unable to access game"));
            return;
        } catch (InvalidMoveException e) {
            broadcastError(username, e);
            return;
        }

        Notification notification = new Notification(
                ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has resigned.\nYOU WIN\n"
        );
        Notification clientNotification = new Notification(
                ServerMessage.ServerMessageType.NOTIFICATION,
                "You have resigned.\nOPPONENT WINS\n"
        );

        try {
            connections.broadcastToUsername(username, clientNotification);
            connections.broadcastToGameExcludeUsername(username, notification, gameId);
        } catch (IOException e) {
            broadcastError(username, new Exception("Unable to broadcast resignation"));
        }
    }
}