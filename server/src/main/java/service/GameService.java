package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public abstract class GameService {
    public static GameData[] listGames(String authToken) throws DataAccessException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }
        return dataAccess.getGames();
    }

    public static CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(createGameRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        int gameID = dataAccess.getGames().length;
        String gameName = createGameRequest.getGameName();

        GameData gameData = new GameData(
                gameID, null, null, gameName, new ChessGame()
        );
        dataAccess.createGame(gameData);
        return new CreateGameResult(gameID);
    }

    public static CreateGameResult joinGame(JoinGameRequest joinGameRequest) throws Exception {
        AuthData authData = dataAccess.getAuth(joinGameRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        GameData game = dataAccess.getGame(joinGameRequest.getGameID());
        if (game == null) {
            throw new Exception("No game with given ID");
        }

        String myUsername = authData.getUsername();
        String whiteUsername = game.getWhiteUsername();
        String blackUsername = game.getBlackUsername();

        if (joinGameRequest.getPlayerColor().equals("WHITE")) {
            if (whiteUsername != null) {
                throw new RuntimeException("Already Taken");
            }
            whiteUsername = myUsername;
        } else if (joinGameRequest.getPlayerColor().equals("BLACK")) {
            if (blackUsername != null) {
                throw new RuntimeException("Already Taken");
            }
            blackUsername = myUsername;
        } else {
            throw new Exception("Bad Request");
        }

        dataAccess.updateGame(new GameData(game.getGameID(), whiteUsername, blackUsername, game.getGameName(), game.getGame()));
        return new CreateGameResult(game.getGameID());
    }

    public static void clear() {
        dataAccess.deleteAll();
    }
}
