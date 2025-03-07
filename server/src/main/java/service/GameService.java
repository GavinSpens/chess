package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public abstract class GameService {
    public static ListGamesResult listGames(String authToken) throws DataAccessException {
        if (DataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        return new ListGamesResult(DataAccess.getGames());
    }

    public static CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        AuthData authData = DataAccess.getAuth(createGameRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }

        int gameID = DataAccess.getGames().length + 1;
        String gameName = createGameRequest.getGameName();

        GameData gameData = new GameData(
                gameID, null, null, gameName, new ChessGame()
        );
        DataAccess.createGame(gameData);
        return new CreateGameResult(gameID);
    }

    public static CreateGameResult joinGame(JoinGameRequest joinGameRequest) throws Exception {
        AuthData authData = DataAccess.getAuth(joinGameRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }

        GameData game = DataAccess.getGame(joinGameRequest.getGameID());
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

        DataAccess.updateGame(new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game()));
        return new CreateGameResult(game.gameID());
    }

    public static void clear() {
        DataAccess.deleteAll();
    }
}
