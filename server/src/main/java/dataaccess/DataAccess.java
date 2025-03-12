package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {

    UserData getUser(String username);

    AuthData getAuth(String authToken);

    GameData getGame(int gameId);

    GameData[] getGames();

    void createUser(UserData user);

    void createAuth(AuthData auth);

    void createGame(GameData game);

    void updateGame(GameData game);

    void deleteAuth(String authToken);

    void deleteAll();
}