package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public abstract class GameService {
    private static final boolean useInMemoryDatabase = true;

    public static ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData;
        if (useInMemoryDatabase) {
            authData = DataAccess_InMemory.getAuth(authToken);
        } else {

        }
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        GameData[] gameData;
        if (useInMemoryDatabase) {
            gameData = DataAccess_InMemory.getGames();
        } else {

        }
        return new ListGamesResult(gameData);
    }

    public static CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {

        AuthData authData;
        if (useInMemoryDatabase) {
            authData = DataAccess_InMemory.getAuth(createGameRequest.getAuthToken());
        } else {

        }
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }

        int gameID = 0;
        if (useInMemoryDatabase) {
            gameID = DataAccess_InMemory.getGames().length + 1;
        } else {

        }
        String gameName = createGameRequest.getGameName();

        GameData gameData = new GameData(
                gameID, null, null, gameName, new ChessGame()
        );
        if (useInMemoryDatabase) {
            DataAccess_InMemory.createGame(gameData);
        } else {

        }
        return new CreateGameResult(gameID);
    }

    public static CreateGameResult joinGame(JoinGameRequest joinGameRequest) throws Exception {
        AuthData authData;
        if (useInMemoryDatabase) {
            authData = DataAccess_InMemory.getAuth(joinGameRequest.getAuthToken());
        } else {

        }
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }

        GameData game = null;
        if (useInMemoryDatabase) {
            game = DataAccess_InMemory.getGame(joinGameRequest.getGameID());
        } else {

        }
        if (game == null) {
            throw new Exception("Error: No game with given ID");
        }

        String myUsername = authData.getUsername();
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (joinGameRequest.getPlayerColor() == null) {
            throw new Exception("Error: Bad Request");
        }

        if (joinGameRequest.getPlayerColor().equals("WHITE")) {
            if (whiteUsername != null) {
                throw new RuntimeException("Error: Already Taken");
            }
            whiteUsername = myUsername;
        } else if (joinGameRequest.getPlayerColor().equals("BLACK")) {
            if (blackUsername != null) {
                throw new RuntimeException("Error: Already Taken");
            }
            blackUsername = myUsername;
        } else {
            throw new Exception("Error: Bad Request");
        }

        GameData gameData = new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
        if (useInMemoryDatabase) {
            DataAccess_InMemory.updateGame(gameData);
        } else {

        }
        return new CreateGameResult(game.gameID());
    }

    public static void clear() {
        if (useInMemoryDatabase) {
            DataAccess_InMemory.deleteAll();
        } else {

        }
    }
}
