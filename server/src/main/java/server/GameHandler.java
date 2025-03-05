package server;

import model.*;
import service.GameService;
import spark.*;
import com.google.gson.Gson;

import dataaccess.DataAccessException;

public abstract class GameHandler {
    public static ListGamesResult listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        return GameService.listGames(authToken);
    }

    public static CreateGameResult createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        myString gameName = getBody(req, myString.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName.getGameName(), authToken);
        return GameService.createGame(createGameRequest);
    }

    public static CreateGameResult joinGame(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");
        JoinGameReq joinReq = getBody(req, JoinGameReq.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(joinReq, authToken);
        return GameService.joinGame(joinGameRequest);
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}