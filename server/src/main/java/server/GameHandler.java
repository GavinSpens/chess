package server;

import dataaccess.DataAccess;
import model.*;
import service.GameService;
import spark.*;
import com.google.gson.Gson;

import dataaccess.DataAccessException;

public class GameHandler {
    private final GameService gameService;
    
    public GameHandler(DataAccess dataAccess) {
        gameService = new GameService(dataAccess);
    }
    
    public ListGamesResult listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        return gameService.listGames(authToken);
    }

    public CreateGameResult createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        GameNameClass gameName = getBody(req, GameNameClass.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName.getGameName(), authToken);
        return gameService.createGame(createGameRequest);
    }

    public CreateGameResult joinGame(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");
        JoinGameReq joinReq = getBody(req, JoinGameReq.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(joinReq, authToken);
        return gameService.joinGame(joinGameRequest);
    }

    public void clear() {
        gameService.clear();
    }

    private <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}