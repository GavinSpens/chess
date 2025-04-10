package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.SQLDataAccess;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), command.getGameID(), session);
            case LEAVE -> leave(command.getUsername());
            case RESIGN -> resign(command.getUsername(), command.getGameID(), session);
            case MAKE_MOVE -> makeMove(command, session);
        }
    }

    private void makeMove(UserGameCommand command, Session session) {
        SQLDataAccess sqlDataAccess = SQLDataAccess.getInstance();
        if (command.getClass() == MakeMoveCommand.class) {
            GameData game = sqlDataAccess.getGame(command.getGameID());
            ChessMove move = ((MakeMoveCommand) command).getMove();
            try {
                game.game().makeMove(move);
                sqlDataAccess.updateGame(game);

                Notification notification = new Notification(ServerMessage.ServerMessageType.LOAD_GAME, "");
                connections.broadcast("", notification);
            } catch (InvalidMoveException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connect(String username, int gameId, Session session) {
        connections.add(username, gameId, session);
        var message = String.format("%s joined the game", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            connections.broadcast(username, notification);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void leave(String username) {
        var message = String.format("%s left the game", username);
        try {
            connections.remove(username);
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(username, notification);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resign(String username, int gameId, Session session) {
        SQLDataAccess sqlDataAccess = SQLDataAccess.getInstance();
        GameData game = sqlDataAccess.getGame(gameId);
        game.game()
    }

//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}