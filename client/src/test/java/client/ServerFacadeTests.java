package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverFacade.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    UserData registerRequest = new UserData("player1", "password", "p1@email.com");
    UserData registerRequest2 = new UserData("player2", "password2", "p2@email.com");
    AuthData authData;
    LoginRequest loginRequest = new LoginRequest("player1", "password");
    CreateGameRequest createGameRequest;
    JoinGameRequest joinGameRequest;
    String auth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void reset() throws Exception {
        facade.clear();
        RegisterResult registerResult = facade.register(registerRequest);
        authData = new AuthData(registerResult.getAuthToken(), registerResult.getUsername());
        auth = authData.getAuthToken();
        createGameRequest = new CreateGameRequest("game1", auth);
        CreateGameResult result = facade.createGame(createGameRequest);
        joinGameRequest = new JoinGameRequest("WHITE", result.getGameID(), auth);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        var registerResult = facade.register(registerRequest2);
        assertTrue(registerResult.getAuthToken().length() > 10);
    }

    @Test
    void badRegister() {
        assertThrows(Exception.class, () -> facade.register(registerRequest));
    }

    @Test
    void login() throws Exception {
        var loginResult = facade.login(loginRequest);
        assertTrue(loginResult.getAuthToken().length() > 10);
    }

    @Test
    void badLogin() {
        var badLoginRequest = new LoginRequest("invalid", "invalid");
        assertThrows(Exception.class, () -> facade.login(badLoginRequest));
    }

    @Test
    void logout() {
        assertDoesNotThrow(() -> facade.logout(authData.getAuthToken()));
    }

    @Test
    void badLogout() {
        assertThrows(Exception.class, () -> facade.logout("invalid"));
    }

    @Test
    void listGames() {
        assertDoesNotThrow(() -> facade.listGames(authData.getAuthToken()));
    }

    @Test
    void badListGames() {
        assertThrows(Exception.class, () -> facade.listGames("invalid"));
    }

    @Test
    void createGame() {
        assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest("game2", auth)));
    }

    @Test
    void badCreateGame() {
        assertThrows(Exception.class, () -> facade.createGame(new CreateGameRequest("invalid", "invalid")));
    }

    @Test
    void joinGame() {
        assertDoesNotThrow(() -> facade.joinGame(joinGameRequest));
    }

    @Test
    void badJoinGame() {
        assertThrows(Exception.class, () -> facade.joinGame(new JoinGameRequest("BLACK", null, "invalid")));
    }

    @Test
    void clear() {
        assertDoesNotThrow(() -> facade.clear());
    }
}
