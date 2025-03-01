package Service;

import dataaccess.DataAccessException;
import model.*;
import service.UserService;

public class TestUserService {
    public static void main(String[] args) {

    }

    private boolean callRegister(RegisterRequest registerRequest, RegisterResult expected) {
        RegisterResult actual;
        try {
            actual = UserService.register(registerRequest);
        } catch (DataAccessException e) {
            actual = null;
        }
        return actual == expected;
    }

    private boolean callLogin(LoginRequest loginRequest, LoginResult expected) {
        LoginResult actual;
        try {
            actual = UserService.login(loginRequest);
        } catch (DataAccessException e) {
            actual = null;
        }
        return actual == expected;
    }

    private boolean callLogout(LogoutRequest logoutRequest, boolean throwsError) {
        try {
            UserService.logout(logoutRequest);
        } catch (DataAccessException e) {
            return throwsError;
        }
        return !throwsError;
    }
}
