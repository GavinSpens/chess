package service;

import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;

public class TestUserService {
    private final String username = "username";
    private final String password = "password";
    private final String email = "email";
    private String authToken;

    private static UserService userService;
    private static GameService gameService;

    private final RegisterRequest registerRequest = new RegisterRequest(username, password, email);
    private final LoginRequest loginRequest = new LoginRequest(username, password);

    private LogoutRequest createLogoutRequest() {
        return new LogoutRequest(authToken);
    }

    @BeforeAll
    public static void init() {
        userService = new UserService();
        gameService = new GameService();
    }
    
    @AfterEach
    public void tearDown() {
        gameService.clear();
    }

    @Test
    @DisplayName("Register")
    public void testRegister() {
        RegisterResult actual;
        try {
            actual = userService.register(registerRequest);
        } catch (Exception e) {
            Assertions.fail("Threw DataAccessException " + e);
            return;
        }
        Assertions.assertEquals(username, actual.getUsername());
        Assertions.assertNotNull(actual.getAuthToken());
    }

    @Test
    @DisplayName("Register fail")
    public void testRegisterFail() {
        try {
            userService.register(registerRequest);
        } catch (Exception e) {
            Assertions.fail("Threw DataAccessException on first register call" + e);
            return;
        }
        try {
            userService.register(registerRequest);
        } catch (Exception e) {
            return;
        }
        Assertions.fail("didn't throw exception");
    }

    @Test
    @DisplayName("login")
    public void testLogin() {
        LoginResult actual;
        try {
            userService.register(registerRequest);
            actual = userService.login(loginRequest);
        } catch (Exception e) {
            Assertions.fail();
            return;
        }
        Assertions.assertEquals(username, actual.getUsername());
        Assertions.assertNotNull(actual.getAuthToken());
    }

    @Test
    @DisplayName("login fail")
    public void testLoginFail() {
        try {
            userService.login(loginRequest);
        } catch (DataAccessException e) {
            return;
        }
        Assertions.fail();
    }

    @Test
    @DisplayName("logout")
    public void testLogout() {
        RegisterResult result;
        try {
            result = userService.register(registerRequest);
        } catch (Exception e) {
            Assertions.fail();
            return;
        }
        authToken = result.getAuthToken();
        LogoutRequest logoutRequest = createLogoutRequest();
        try {
            userService.logout(logoutRequest);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("logout fail")
    public void testLogoutFail() {
        LogoutRequest logoutRequest = createLogoutRequest();
        try {
            userService.logout(logoutRequest);
        } catch (DataAccessException e) {
            return;
        }
        Assertions.fail();
    }
}
