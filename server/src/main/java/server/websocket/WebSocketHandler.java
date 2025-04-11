package server.websocket;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.SQLDataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final SQLDataAccess sqlDataAccess = SQLDataAccess.getInstance();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String username = sqlDataAccess.getAuth(command.getAuthToken()).getUsername();
        switch (command.getCommandType()) {
            case CONNECT -> connect(username, command.getGameID(), session);
            case LEAVE -> leave(username);
            case RESIGN -> resign(username, command.getGameID());
            case MAKE_MOVE -> makeMove(message);
        }
    }

    private void makeMove(String message) {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);

        GameData game = sqlDataAccess.getGame(command.getGameID());
        ChessMove move = command.getMove();
        try {
            game.game().makeMove(move);
            sqlDataAccess.updateGame(game);

            LoadGame notification = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcast("", notification);
        } catch (InvalidMoveException | IOException e) {
            e.printStackTrace();
        }
    }

    private void connect(String username, int gameId, Session session) {
        connections.add(username, gameId, session);
        var message = String.format("%s joined the game", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);

        var loadGame = new LoadGame(
                ServerMessage.ServerMessageType.LOAD_GAME,
                sqlDataAccess.getGame(gameId));

        try {
            connections.broadcast(username, notification);
            connections.broadcastToUsername(username, loadGame);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void leave(String username) {
        var message = String.format("%s left the game", username);
        connections.remove(username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            connections.broadcast(username, notification);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resign(String username, int gameId) {
        GameData game = sqlDataAccess.getGame(gameId);

        Notification notification = new Notification(
                ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has resigned.\nYOU WIN\n"
        );
        LoadGame loadGame = new LoadGame(
                ServerMessage.ServerMessageType.LOAD_GAME,
                game
        );

        try {
            connections.broadcast("", loadGame);
            connections.broadcast(username, notification);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}