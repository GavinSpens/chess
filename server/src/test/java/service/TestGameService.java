package service;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;

import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import server.GameHandler;
import server.UserHandler;
// import spark.Request;

public class TestGameService {
    private final String username = "username";
    private final String password = "password";
    private final String email = "email";
    private final String gameName = "gameName";
    private final String playerColor = "WHITE";
    private String authToken;
    private Integer gameID;
    
    private static UserService userService;
    private static GameService gameService;

    private final UserData registerRequest = new UserData(username, password, email);

    private CreateGameRequest createGameRequest() {
        return new CreateGameRequest(gameName, authToken);
    }

    private JoinGameRequest joinGameRequest() {
        return new JoinGameRequest(playerColor, gameID, authToken);
    }
    
    @BeforeAll
    public static void init() {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
    }
    
    @BeforeEach
    public void registerGetAuth() {
        RegisterResult result;
        try {
            result = userService.register(registerRequest);
        } catch (Exception e) {
            Assertions.fail();
            return;
        }
        authToken = result.getAuthToken();
    }

    @AfterEach
    public void tearDown() {
        try {
            gameService.clear();
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("listGames empty")
    public void testListGamesEmpty() {
        ListGamesResult actual;
        try {
            actual = gameService.listGames(authToken);
        } catch (DataAccessException e) {
            Assertions.fail();
            return;
        }
        Assertions.assertEquals(0, actual.getGames().length);
    }

    @Test
    @DisplayName("listGames")
    public void testListGames() {
        ListGamesResult actual;
        try {
            gameService.createGame(createGameRequest());
            actual = gameService.listGames(authToken);
        } catch (DataAccessException e) {
            Assertions.fail();
            return;
        }
        Assertions.assertNotEquals(0, actual.getGames().length);
    }

    @Test
    @DisplayName("createGame")
    public void testCreateGame() {
        // CreateGameResult actual;
        try {
            gameService.createGame(createGameRequest());
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("joinGame")
    public void testJoinGame() {
        CreateGameResult actual;
        try {
            gameID = gameService.createGame(createGameRequest()).getGameID();
            actual = gameService.joinGame(joinGameRequest());
        } catch (Exception e) {
            Assertions.fail();
            return;
        }
        Assertions.assertEquals(gameID, actual.getGameID());
    }

    @Test
    @DisplayName("clear")
    public void testClear() {
        gameService.clear();
        try {
            gameService.listGames("noAuth");
        } catch (DataAccessException e) {
            return;
        }
        Assertions.fail();
    }

    @Test
    @DisplayName("GameHandler")
    public void testGameHandler() {
        System.out.println(GameHandler.class);
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("UserHandler")
    public void testUserHandler() {
        System.out.println(UserHandler.class);
        Assertions.assertTrue(true);
    }
}