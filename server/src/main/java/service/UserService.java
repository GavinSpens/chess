package service;

import dataaccess.*;
import model.*;

import java.util.Objects;
import java.util.UUID;

public abstract class UserService {
    public static RegisterResult register(RegisterRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();

        if (username == null || password == null || email == null) {
            throw new Exception("Error: Bad Request");
        }
        
        UserData userData = DataAccess.getUser(username);
        if (userData != null) {
            throw new DataAccessException("Error: already taken");
        }

        userData = new UserData(username, password, email);
        DataAccess.createUser(userData);

        String authToken = createAuthToken();
        AuthData auth = new AuthData(authToken, username);
        DataAccess.createAuth(auth);

        return new RegisterResult(authToken, username);
    }

    public static LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.getUsername();

        UserData user = DataAccess.getUser(username);
        if (user == null || !Objects.equals(user.getPassword(), loginRequest.getPassword())) {
            throw new DataAccessException("Error: Unauthorized");
        }

        String authToken = createAuthToken();
        DataAccess.createAuth(new AuthData(authToken, username));

        return new LoginResult(username, authToken);
    }

    public static void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = DataAccess.getAuth(logoutRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        DataAccess.deleteAuth(logoutRequest.getAuthToken());
    }

    private static String createAuthToken() {
        return UUID.randomUUID().toString();
    }
}
