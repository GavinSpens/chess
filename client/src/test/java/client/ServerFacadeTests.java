package client;

import model.AuthData;
import model.LoginRequest;
import model.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    UserData registerRequest = new UserData("player1", "password", "p1@email.com");
    UserData registerRequest2 = new UserData("player2", "password2", "p2@email.com");
    AuthData authData;
    LoginRequest loginRequest = new LoginRequest("player1", "password");

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
}
