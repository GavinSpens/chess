package Service;
import dataaccess.DataAccessException;
import model.*;
import service.*;

public class TestGameService {
    private boolean callListGames(String authToken, GameData[] expected) {
        GameData[] actual;
        try {
            actual = service.GameService.listGames(authToken);
        } catch (DataAccessException e) {
            actual = null;
        }
        return actual == expected;
    }

    private boolean callCreateGame(CreateGameRequest createGameRequest, CreateGameResult expected) {
        CreateGameResult actual;
        try {
            actual = service.GameService.createGame(createGameRequest);
        } catch (DataAccessException e) {
            actual = null;
        }
        return actual == expected;
    }

    private boolean callJoinGame(JoinGameRequest joinGameRequest, CreateGameResult expected) {
        CreateGameResult actual;
        try {
            actual = service.GameService.joinGame(joinGameRequest);
        } catch (Exception e) {
            actual = null;
        }
        return actual == expected;
    }

    private boolean callClear() {
        service.GameService.clear();
        return true;
    }
}