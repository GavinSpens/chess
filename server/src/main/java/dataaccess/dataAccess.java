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
        for (AuthData auth : auths) {
            if (Objects.equals(auth.getAuthToken(), authToken)) {
                return auth;
            }
        }
        return null;
    }

    public static GameData getGame(int gameId) {
        for (GameData game : games) {
            if (game.getGameID() == gameId) {
                return game;
            }
        }
        return null;
    }

    public static GameData[] getGames() {
        return games.toArray(new GameData[0]);
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
        games.remove(game.getGameID());
        games.add(game);
    }

    public static void deleteAuth(String authToken) {
        AuthData delete = null;
        for (AuthData auth : auths) {
            if (Objects.equals(auth.getAuthToken(), authToken)) {
                delete = auth;
            }
        }
        auths.remove(delete);
    }

    public static void deleteGame(int gameId) {
        GameData delete = null;
        for (GameData game : games) {
            if (game.getGameID() == gameId) {
                delete = game;
            }
        }
        games.remove(delete);
    }

    public static void deleteAll() {
        users.clear();
        auths.clear();
        games.clear();
    }
}
