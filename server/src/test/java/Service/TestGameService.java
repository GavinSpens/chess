package Service;
import dataaccess.DataAccessException;
import model.*;

import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

public class TestGameService {
    private final String username = "username";
    private final String password = "password";
    private final String email = "email";
    private final String gameName = "gameName";
    private final String playerColor = "WHITE";
    private String authToken;
    private Integer gameID;
    
    private final RegisterRequest registerRequest = new RegisterRequest(username, password, email);

    private CreateGameRequest createGameRequest() {
        return new CreateGameRequest(gameName, authToken);
    }

    private JoinGameRequest joinGameRequest() {
        return new JoinGameRequest(playerColor, gameID, authToken);
    }
    
    @BeforeEach
    public void registerGetAuth() {
        RegisterResult result;
        try {
            result = UserService.register(registerRequest);
        } catch (Exception e) {
            Assertions.fail();
            return;
        }
        authToken = result.getAuthToken();
    }

    @AfterEach
    public void tearDown() {
        try {
            GameService.clear();
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("listGames empty")
    public void testListGamesEmpty() {
        ListGamesResult actual;
        try {
            actual = GameService.listGames(authToken);
        } catch (DataAccessException e) {
            Assertions.fail();
            return;
        }
        Assertions.assertEquals(0, actual.getGameData().length);
    }

    @Test
    @DisplayName("listGames")
    public void testListGames() {
        ListGamesResult actual;
        try {
            GameService.createGame(createGameRequest());
            actual = GameService.listGames(authToken);
        } catch (DataAccessException e) {
            Assertions.fail();
            return;
        }
        Assertions.assertNotEquals(0, actual.getGameData().length);
    }

    @Test
    @DisplayName("createGame")
    public void testCreateGame() {
        CreateGameResult actual;
        try {
            GameService.createGame(createGameRequest());
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("joinGame")
    public void testJoinGame() {
        CreateGameResult actual;
        try {
            gameID = GameService.createGame(createGameRequest()).getGameID();
            actual = GameService.joinGame(joinGameRequest());
        } catch (Exception e) {
            Assertions.fail();
            return;
        }
        Assertions.assertEquals(gameID, actual.getGameID());
    }

    @Test
    @DisplayName("clear")
    public void testClear() {
        GameService.clear();
    }
}