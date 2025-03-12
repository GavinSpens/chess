package dataaccess;

import java.util.ArrayList;
import java.util.Objects;

import model.*;

public class DataAccessInMemory implements DataAccess {
    public ArrayList<UserData> users;
    public ArrayList<GameData> games;
    public ArrayList<AuthData> auths;

    public DataAccessInMemory() {
        users = new ArrayList<>();
        games = new ArrayList<>();
        auths = new ArrayList<>();
    }
    
    public UserData getUser(String username) {
        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public AuthData getAuth(String authToken) {
        for (AuthData auth : auths) {
            if (Objects.equals(auth.getAuthToken(), authToken)) {
                return auth;
            }
        }
        return null;
    }

    public GameData getGame(int gameId) {
        for (GameData game : games) {
            if (game.gameID() == gameId) {
                return game;
            }
        }
        return null;
    }

    public GameData[] getGames() {
        GameData[] gameData = new GameData[games.size()];
        for (int i = 0; i < games.size(); i++) {
            gameData[i] = games.get(i);
        }
        return gameData;
    }

    public void createUser(UserData user) {
        users.add(user);
    }

    public void createAuth(AuthData auth) {
        auths.add(auth);
    }

    public void createGame(GameData game) {
        games.add(game);
    }

    public void updateGame(GameData game) {
        games.remove(game.gameID() - 1);
        games.add(game);
    }

    public void deleteAuth(String authToken) {
        AuthData delete = null;
        for (AuthData auth : auths) {
            if (Objects.equals(auth.getAuthToken(), authToken)) {
                delete = auth;
            }
        }
        auths.remove(delete);
    }

    public void deleteAll() {
        users.clear();
        auths.clear();
        games.clear();
    }
}
