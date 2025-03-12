package service;

import dataaccess.*;
import model.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final boolean useInMemoryDatabase = true;
    private final DataAccess dataAccess;
    private final DataAccessInMemory dataAccessInMemory;

    public UserService() {
        dataAccess = new DataAccess();
        dataAccessInMemory = new DataAccessInMemory();
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();

        if (username == null || password == null || email == null) {
            throw new Exception("Error: Bad Request");
        }

        UserData userData = null;
        if (useInMemoryDatabase) {
            userData = dataAccessInMemory.getUser(username);
        } else {
            userData = dataAccess.getUser(username);
        }
        if (userData != null) {
            throw new DataAccessException("Error: already taken");
        }

        userData = new UserData(username, password, email);
        if (useInMemoryDatabase) {
            dataAccessInMemory.createUser(userData);
        } else {
            dataAccess.createUser(userData);
        }

        String authToken = createAuthToken();
        AuthData auth = new AuthData(authToken, username);
        if (useInMemoryDatabase) {
            dataAccessInMemory.createAuth(auth);
        } else {
            dataAccess.createAuth(auth);
        }

        return new RegisterResult(authToken, username);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.getUsername();

        UserData user = null;
        if (useInMemoryDatabase) {
            user = dataAccessInMemory.getUser(username);
        } else {
            user = dataAccess.getUser(username);
        }
        if (user == null || !Objects.equals(user.getPassword(), loginRequest.getPassword())) {
            throw new DataAccessException("Error: Unauthorized");
        }

        String authToken = createAuthToken();
        if (useInMemoryDatabase) {
            dataAccessInMemory.createAuth(new AuthData(authToken, username));
        } else {
            dataAccess.createAuth(new AuthData(authToken, username));
        }

        return new LoginResult(username, authToken);
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = null;
        if (useInMemoryDatabase) {
            authData = dataAccessInMemory.getAuth(logoutRequest.getAuthToken());
        } else {
            authData = dataAccess.getAuth(logoutRequest.getAuthToken());
        }
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        if (useInMemoryDatabase) {
            dataAccessInMemory.deleteAuth(logoutRequest.getAuthToken());
        } else {
            dataAccess.deleteAuth(logoutRequest.getAuthToken());
        }
    }

    private String createAuthToken() {
        return UUID.randomUUID().toString();
    }
}
