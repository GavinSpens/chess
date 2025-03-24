package client;

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
        facade.register(registerRequest);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register(registerRequest2);
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void badRegister() throws Exception {
        assertThrows(Exception.class, () -> facade.register(registerRequest));
    }
}
