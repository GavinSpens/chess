package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public class DataAccessSQL implements DataAccess {

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public GameData getGame(int gameId) {
        return null;
    }

    @Override
    public GameData[] getGames() {
        return new GameData[0];
    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void deleteAll() {

    }
}
