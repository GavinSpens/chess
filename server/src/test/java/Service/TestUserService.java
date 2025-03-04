package Service;

import dataaccess.DataAccessException;
import model.*;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.AfterEach;
import service.GameService;
import service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import spark.utils.Assert;

public class TestUserService {
    private final String username = "username";
    private final String password = "password";
    private final String email = "email";
    private String authToken;

    private final RegisterRequest registerRequest = new RegisterRequest(username, password, email);
    private final LoginRequest loginRequest = new LoginRequest(username, password);

    private LogoutRequest createLogoutRequest() {
        return new LogoutRequest(authToken);
    }

    @AfterEach
    public void tearDown() {
        GameService.clear();
    }

    @Test
    @DisplayName("Register")
    public void testRegister() {
        RegisterResult actual;
        try {
            actual = UserService.register(registerRequest);
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
            UserService.register(registerRequest);
        } catch (Exception e) {
            Assertions.fail("Threw DataAccessException on first register call" + e);
            return;
        }
        try {
            UserService.register(registerRequest);
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
            UserService.register(registerRequest);
            actual = UserService.login(loginRequest);
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
            UserService.login(loginRequest);
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
            result = UserService.register(registerRequest);
        } catch (Exception e) {
            Assertions.fail();
            return;
        }
        authToken = result.getAuthToken();
        LogoutRequest logoutRequest = createLogoutRequest();
        try {
            UserService.logout(logoutRequest);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("logout fail")
    public void testLogoutFail() {
        LogoutRequest logoutRequest = createLogoutRequest();
        try {
            UserService.logout(logoutRequest);
        } catch (DataAccessException e) {
            return;
        }
        Assertions.fail();
    }
}
