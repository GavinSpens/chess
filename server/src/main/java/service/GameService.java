package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public class GameService {
    private final boolean useInMemoryDatabase = true;
    private final DataAccess dataAccess;

    public GameService() {
        if (useInMemoryDatabase) {
            dataAccess = new DataAccessInMemory();
        } else {
            
        }
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

        int gameID = dataAccess.getGames().length + 1;
        String gameName = createGameRequest.getGameName();

        GameData gameData = new GameData(
                gameID, null, null, gameName, new ChessGame());
        dataAccess.createGame(gameData);
        return new CreateGameResult(gameID);
    }

    public CreateGameResult joinGame(JoinGameRequest joinGameRequest) throws Exception {
        AuthData authData = dataAccess.getAuth(joinGameRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }

        GameData game = dataAccess.getGame(joinGameRequest.getGameID());
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
        dataAccess.updateGame(gameData);
        return new CreateGameResult(game.gameID());
    }

    public void clear() {
        dataAccess.deleteAll();
    }
}
