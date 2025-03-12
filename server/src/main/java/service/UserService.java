package service;

import dataaccess.*;
import model.*;

import java.util.Objects;
import java.util.UUID;

public abstract class UserService {
    private static final boolean useInMemoryDatabase = true;

    public static RegisterResult register(RegisterRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();

        if (username == null || password == null || email == null) {
            throw new Exception("Error: Bad Request");
        }

        UserData userData = null;
        if (useInMemoryDatabase) {
            userData = DataAccess_InMemory.getUser(username);
        } else {

        }
        if (userData != null) {
            throw new DataAccessException("Error: already taken");
        }

        userData = new UserData(username, password, email);
        if (useInMemoryDatabase) {
            DataAccess_InMemory.createUser(userData);
        } else {

        }

        String authToken = createAuthToken();
        AuthData auth = new AuthData(authToken, username);
        if (useInMemoryDatabase) {
            DataAccess_InMemory.createAuth(auth);
        } else {

        }

        return new RegisterResult(authToken, username);
    }

    public static LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.getUsername();

        UserData user = null;
        if (useInMemoryDatabase) {
            user = DataAccess_InMemory.getUser(username);
        } else {

        }
        if (user == null || !Objects.equals(user.getPassword(), loginRequest.getPassword())) {
            throw new DataAccessException("Error: Unauthorized");
        }

        String authToken = createAuthToken();
        if (useInMemoryDatabase) {
            DataAccess_InMemory.createAuth(new AuthData(authToken, username));
        } else {

        }

        return new LoginResult(username, authToken);
    }

    public static void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = null;
        if (useInMemoryDatabase) {
            authData = DataAccess_InMemory.getAuth(logoutRequest.getAuthToken());
        } else {

        }
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        if (useInMemoryDatabase) {
            DataAccess_InMemory.deleteAuth(logoutRequest.getAuthToken());
        } else {

        }
    }

    private static String createAuthToken() {
        return UUID.randomUUID().toString();
    }
}
