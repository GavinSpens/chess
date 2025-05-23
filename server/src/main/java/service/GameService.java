package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Objects;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        GameData[] gameData = dataAccess.getGames();
        return new ListGamesResult(gameData);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {

        AuthData authData = dataAccess.getAuth(createGameRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }

        int gameID = dataAccess.countGames() + 1;
        String gameName = createGameRequest.getGameName();

        GameData gameData = new GameData(
                gameID, null, null, gameName, new ChessGame(), false);
        dataAccess.createGame(gameData);
        gameID = dataAccess.getGames()[gameID - 1].getId();
        return new CreateGameResult(gameID);
    }

    public CreateGameResult joinGame(JoinGameRequest joinGameRequest) throws Exception {
        AuthData authData = dataAccess.getAuth(joinGameRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }

        GameData game;
        try {
            game = dataAccess.getGame(joinGameRequest.getGameID());
        } catch (DataAccessException e) {
            throw new Exception("Error: No game with given ID");
        }

        String myUsername = authData.getUsername();
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();
        String playerColor = joinGameRequest.getPlayerColor();

        if (playerColor == null) {
            throw new Exception("Error: Bad Request");
        }

        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (whiteUsername != null && !Objects.equals(whiteUsername, myUsername)) {
                throw new RuntimeException("Error: Already Taken");
            }
            whiteUsername = myUsername;
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (blackUsername != null && !Objects.equals(whiteUsername, myUsername)) {
                throw new RuntimeException("Error: Already Taken");
            }
            blackUsername = myUsername;
        } else {
            throw new Exception("Error: Bad Request");
        }

        GameData gameData = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game(), false);
        dataAccess.updateGame(gameData);
        return new CreateGameResult(game.gameID());
    }

    public void clear() {
        try {
            dataAccess.deleteAll();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
