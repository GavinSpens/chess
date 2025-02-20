package dataaccess;

import java.util.ArrayList;

import chess.ChessGame;
import model.*;

public abstract class dataAccess {
    public static ArrayList<UserData> users;
    public static ArrayList<GameData> games;
    public static ArrayList<AuthData> auths;
    
    public static UserData getUser(String username) throws DataAccessException {
        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("User not found");
    }

    public static AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : auths) {
            if (auth.getAuthToken().equals(authToken)) {
                return auth;
            }
        }
        throw new DataAccessException("Auth not found");
    }

    public static void createUser(UserData user) {
        users.add(user);
    }

    public static void createAuth(AuthData auth) {
        auths.add(auth);
    }
}
