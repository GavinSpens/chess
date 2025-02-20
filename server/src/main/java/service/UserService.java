package service;

import dataaccess.*;
import model.*;

import java.util.Objects;
import java.util.UUID;

public abstract class UserService {
    public static RegisterResult register(RegisterRequest request) throws DataAccessException {
        String username = request.getUsername();
        
        UserData userData = dataAccess.getUser(username);
        if (userData != null) {
            throw new DataAccessException("Error: already taken");
        }

        userData = new UserData(username, request.getPassword(), request.getEmail());
        dataAccess.createUser(userData);

        String authToken = createAuthToken();
        AuthData auth = new AuthData(authToken, username);
        dataAccess.createAuth(auth);

        return new RegisterResult(authToken, username);
    }

    public static LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.getUsername();

        UserData user = dataAccess.getUser(username);
        if (user == null || !Objects.equals(user.getPassword(), loginRequest.getPassword())) {
            throw new DataAccessException("Error: Unauthorized");
        }

        String authToken = createAuthToken();
        dataAccess.createAuth(new AuthData(authToken, username));

        return new LoginResult(username, authToken);
    }

    public static void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(logoutRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }
        dataAccess.deleteAuth(logoutRequest.getAuthToken());
    }

    private static String createAuthToken() {
        return UUID.randomUUID().toString();
    }
}
