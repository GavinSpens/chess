package server;

import model.*;
import service.GameService;
import spark.*;
import com.google.gson.Gson;

import dataaccess.DataAccessException;

import javax.xml.crypto.Data;

public abstract class GameHandler {
    public static String listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        return new Gson().toJson(GameService.listGames(authToken));
    }

    public static String createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        myString gameName = getBody(req, myString.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName.getMystring(), authToken);
        CreateGameResult result = GameService.createGame(createGameRequest);
        return new Gson().toJson(result);
    }

    public static String joinGame(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");
        JoinGameReq joinReq = getBody(req, JoinGameReq.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(joinReq, authToken);
        CreateGameResult gameID = GameService.joinGame(joinGameRequest);
        return new Gson().toJson(gameID);
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}
