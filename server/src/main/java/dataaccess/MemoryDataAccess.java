package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryDataAccess implements DataAccess {
    private final ArrayList<UserData> users;
    private final ArrayList<GameData> games;
    private final ArrayList<AuthData> auths;

    public MemoryDataAccess() {
        users = new ArrayList<>();
        games = new ArrayList<>();
        auths = new ArrayList<>();
    }

    @Override
    public UserData getUser(String username) {
        for (UserData user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData auth : auths) {
            if (Objects.equals(auth.getAuthToken(), authToken)) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public GameData getGame(int gameId) {
        for (GameData game : games) {
            if (game.gameID() == gameId) {
                return game;
            }
        }
        return null;
    }

    @Override
    public GameData[] getGames() {
        GameData[] gameData = new GameData[games.size()];
        for (int i = 0; i < games.size(); i++) {
            gameData[i] = games.get(i);
        }
        return gameData;
    }

    @Override
    public int countGames() {
        return games.size();
    }

    @Override
    public void createUser(UserData user) {
        users.add(user);
    }

    @Override
    public void createAuth(AuthData auth) {
        auths.add(auth);
    }

    @Override
    public void createGame(GameData game) {
        games.add(game);
    }

    @Override
    public void updateGame(GameData game) {
        games.remove(game.gameID() - 1);
        games.add(game);
    }

    @Override
    public void deleteAuth(String authToken) {
        AuthData delete = null;
        for (AuthData auth : auths) {
            if (Objects.equals(auth.getAuthToken(), authToken)) {
                delete = auth;
            }
        }
        auths.remove(delete);
    }

    @Override
    public void deleteAll() {
        users.clear();
        auths.clear();
        games.clear();
    }
}
