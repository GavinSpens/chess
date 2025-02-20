package dataaccess;

import java.util.ArrayList;
import java.util.Objects;

import model.*;

public abstract class dataAccess {
    public static ArrayList<UserData> users = new ArrayList<>();
    public static ArrayList<GameData> games = new ArrayList<>();
    public static ArrayList<AuthData> auths = new ArrayList<>();
    
    public static UserData getUser(String username) {
        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static AuthData getAuth(String authToken) {
        if (!auths.isEmpty()) {
            for (AuthData auth : auths) {
                if (Objects.equals(auth.getAuthToken(), authToken)) {
                    return auth;
                }
            }
        }
        return null;
    }

    public static GameData getGame(int gameId) {
        if (!games.isEmpty()) {
            for (GameData game : games) {
                if (game.getGameID() == gameId) {
                    return game;
                }
            }
        }
        return null;
    }

    public static void createUser(UserData user) {
        users.add(user);
    }

    public static void createAuth(AuthData auth) {
        auths.add(auth);
    }

    public static void createGame(GameData game) {
        games.add(game);
    }

    public static void updateGame(GameData game) {
        int gameID = game.getGameID();
        deleteGame(gameID);
        games.add(game);
    }

    public static void deleteAuth(String authToken) {
        for (AuthData auth : auths) {
            if (Objects.equals(auth.getAuthToken(), authToken)) {
                auths.remove(auth);
                return;
            }
        }
    }

    public static void deleteGame(int gameId) {
        for (GameData game : games) {
            if (game.getGameID() == gameId) {
                games.remove(game);
                return;
            }
        }
    }

    public static void deleteAll() {
        users.clear();
        auths.clear();
        games.clear();
    }
}
