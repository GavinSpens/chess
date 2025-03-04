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
            if (game.gameID() == gameId) {
                return game;
            }
        }
        return null;
    }

    public static GameData[] getGames() {
        GameData[] gameData = new GameData[games.size()];
        for (int i = 0; i < games.size(); i++) {
            gameData[i] = games.get(i);
        }
        return gameData;
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
        games.remove(game.gameID() - 1);
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

    public static void deleteAll() {
        users.clear();
        auths.clear();
        games.clear();
    }
}
