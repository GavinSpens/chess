package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public class DataAccess {

    public DataAccess() {

    }

    public UserData getUser(String username) {
        return null;
    }

    public AuthData getAuth(String authToken) {
        return null;
    }

    public GameData getGame(int gameId) {
        return null;
    }

    public GameData[] getGames() {
        return null;
    }

    public void createUser(UserData user) {
    }

    public void createAuth(AuthData auth) {
    }

    public void createGame(GameData game) {
    }

    public void updateGame(GameData game) {
    }

    public void deleteAuth(String authToken) {
    }

    public void deleteAll() {
    }
}