package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(serverMessage, message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame(String authToken, int gameId) throws ResponseException {
        try {
            var command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ignored) {
            throw new ResponseException(500, "Error: bad connection to server\n");
        }
    }

    public void leaveGame(String authToken, int gameId) throws ResponseException {
        try {
            var command = new LeaveCommand(UserGameCommand.CommandType.LEAVE, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        } catch (IOException ignored) {
            throw new ResponseException(500, "Error: bad connection to server\n");
        }
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws ResponseException {
        var command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameId, move);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ignored) {
            throw new ResponseException(500, "Error: bad connection to server\n");
        }
    }

    public void resign(String authToken, int gameId) throws ResponseException {
        var command = new ResignCommand(UserGameCommand.CommandType.RESIGN, authToken, gameId);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ignored) {
            throw new ResponseException(500, "Error: bad connection to server\n");
        }
    }
}