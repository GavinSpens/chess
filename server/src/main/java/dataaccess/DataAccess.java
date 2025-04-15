package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {

    UserData getUser(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    GameData getGame(int gameId) throws DataAccessException;

    GameData[] getGames() throws DataAccessException;

    int countGames() throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void deleteAll() throws DataAccessException;
}