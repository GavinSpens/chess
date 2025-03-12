package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {

    public UserData getUser(String username);

    public AuthData getAuth(String authToken);

    public GameData getGame(int gameId);

    public GameData[] getGames();

    public void createUser(UserData user);

    public void createAuth(AuthData auth);

    public void createGame(GameData game);

    public void updateGame(GameData game);

    public void deleteAuth(String authToken);

    public void deleteAll();
}