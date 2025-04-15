package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    private UserData hashPassword(UserData userData) {
        String clearTextPassword = userData.getPassword();
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        return new UserData(userData.getUsername(), hashedPassword, userData.getEmail());
    }

    private boolean verifyPassword(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        UserData userData = dataAccess.getUser(username);
    
        if (userData == null) {
            return false;
        }
        String hashedPassword = userData.getPassword();

        return BCrypt.checkpw(password, hashedPassword);
    }

    public RegisterResult register(UserData request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();

        if (username == null || password == null || email == null) {
            throw new Exception("Error: Bad Request");
        }

        UserData userData = null;
        try {
            userData = dataAccess.getUser(username);
        } catch (DataAccessException e){
            // Ignore
        }
        if (userData != null) {
            throw new DataAccessException("Error: already taken");
        }

        userData = new UserData(username, password, email);
        userData = hashPassword(userData);

        dataAccess.createUser(userData);

        String authToken = createAuthToken();
        AuthData auth = new AuthData(authToken, username);
        dataAccess.createAuth(auth);

        return new RegisterResult(authToken, username);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.getUsername();

        if (!verifyPassword(loginRequest)) {
            throw new DataAccessException("Error: Unauthorized");
        }

        String authToken = createAuthToken();
        dataAccess.createAuth(new AuthData(authToken, username));

        return new LoginResult(username, authToken);
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(logoutRequest.getAuthToken());
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        dataAccess.deleteAuth(logoutRequest.getAuthToken());
    }

    private String createAuthToken() {
        return UUID.randomUUID().toString();
    }
}
