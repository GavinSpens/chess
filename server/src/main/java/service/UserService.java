package service;

import dataaccess.*;
import model.*;
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
        AuthData auth = new AuthData(username, authToken);
        dataAccess.createAuth(auth);

        return new RegisterResult(authToken, username);
    }

    private static String createAuthToken() {
        return UUID.randomUUID().toString();
    }
}
