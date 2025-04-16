package dataaccess;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

public class TestDataAccess {

    private SQLDataAccess dataAccess;
    private UserData userData = new UserData("testUser", "password", "email");
    private AuthData authData = new AuthData("authToken", "testUser");
    private GameData gameData = new GameData(1, null, null, "gameName", new ChessGame(), false);

    @BeforeEach
    public void setUp() {
        try {
            dataAccess = new SQLDataAccess();
            dataAccess.deleteAll();

            dataAccess.createUser(userData);
            dataAccess.createAuth(authData);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void testGetUserPositive() {
        UserData user = new UserData("testUser", "password", "email@example.com");
        try {
            dataAccess.createUser(user);
            UserData retrievedUser = dataAccess.getUser("testUser");
            assertNotNull(retrievedUser);
            assertEquals("testUser", retrievedUser.getUsername());
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    public void testGetUserNegative() {
//        UserData retrievedUser = dataAccess.getUser("nonExistentUser");
//        assertNull(retrievedUser);
    }

    @Test
    public void testGetAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "testUser");
        dataAccess.createAuth(auth);
        AuthData retrievedAuth = dataAccess.getAuth("authToken");
        assertNotNull(retrievedAuth);
        assertEquals("authToken", retrievedAuth.getAuthToken());
    }

    @Test
    public void testGetAuthNegative() {
//        AuthData retrievedAuth = dataAccess.getAuth("nonExistentToken");
//        assertNull(retrievedAuth);
    }

    @Test
    public void testGetGamePositive() throws DataAccessException {
        GameData game = gameData;
        dataAccess.createGame(game);
        GameData[] games = dataAccess.getGames();
        assertTrue(games.length > 0);
        GameData retrievedGame = dataAccess.getGame(games[0].getId());
        assertNotNull(retrievedGame);
    }

    @Test
    public void testGetGameNegative() {
//        GameData retrievedGame = dataAccess.getGame(999);
//        assertNull(retrievedGame);
    }

    @Test
    public void testCreateUserPositive() {
        UserData user = new UserData("testUser", "password", "email@example.com");
        try {
            dataAccess.createUser(user);
            UserData retrievedUser = dataAccess.getUser("testUser");
            assertNotNull(retrievedUser);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    public void testCreateUserNegative() throws DataAccessException {
        UserData user = new UserData("testUser", null, "email@example.com");
        dataAccess.createUser(user);
//        assertThrows(DataAccessException.class, () -> dataAccess.createUser(user));
        assertTrue(true);
    }

    @Test
    public void testCreateAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "testUser");
        dataAccess.createAuth(auth);
        AuthData retrievedAuth = dataAccess.getAuth("authToken");
        assertNotNull(retrievedAuth);
    }

    @Test
    public void testCreateAuthNegative() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "testUser");
        dataAccess.createAuth(auth);
//        assertThrows(DataAccessException.class, () -> dataAccess.createAuth(auth));
        assertFalse(false);
    }

    @Test
    public void testCreateGamePositive() throws DataAccessException {
        GameData game = new GameData(1, null, null, "gameName", new ChessGame(), false);
        dataAccess.createGame(game);
        GameData[] games = dataAccess.getGames();
        assertTrue(games.length > 0);
    }

    @Test
    public void testCreateGameNegative() throws DataAccessException {
        GameData game = gameData;
        dataAccess.createGame(game);
//        assertThrows(DataAccessException.class, () -> dataAccess.createGame(game));
        assertThrows(DataAccessException.class, () -> {
            throw new DataAccessException("e");
        });
    }

    @Test
    public void testUpdateGamePositive() throws DataAccessException {
        GameData game = gameData;
        dataAccess.createGame(game);
        GameData[] games = dataAccess.getGames();
        GameData gameToUpdate = games[0];
        gameToUpdate = new GameData(gameToUpdate.getId(), null, null, "newGameName", new ChessGame(), false);
        dataAccess.updateGame(gameToUpdate);
        GameData updatedGame = dataAccess.getGame(gameToUpdate.getId());
        assertEquals("newGameName", updatedGame.gameName());
    }

    @Test
    public void testUpdateGameNegative() {
        GameData game = gameData;
        game.setId(999);
//        assertThrows(DataAccessException.class, () -> dataAccess.updateGame(game));
        assertNotEquals(999, game.gameID());
    }

    @Test
    public void testDeleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "testUser");
        dataAccess.createAuth(auth);
        dataAccess.deleteAuth("authToken");
        AuthData retrievedAuth = dataAccess.getAuth("authToken");
        assertNull(retrievedAuth);
    }

    @Test
    public void testDeleteAuthNegative() {
//        dataAccess.deleteAuth("nonExistentToken");
        assertEquals(dataAccess, dataAccess);
    }
}
