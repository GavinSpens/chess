package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import com.google.gson.Gson;

public class SQLDataAccess implements DataAccess {
    private static SQLDataAccess instance;

    public static SQLDataAccess getInstance() {
        return instance;
    }

    private final String[] createStatements = {
            """
                    CREATE TABLE IF NOT EXISTS users (
                        id INT NOT NULL AUTO_INCREMENT,
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id),
                        UNIQUE (username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """,
            """
                    CREATE TABLE IF NOT EXISTS auths (
                        id INT NOT NULL AUTO_INCREMENT,
                        authToken VARCHAR(255) NOT NULL,
                        username VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id),
                        FOREIGN KEY (username) REFERENCES users(username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """,
            """
                    CREATE TABLE IF NOT EXISTS games (
                        id INT NOT NULL AUTO_INCREMENT,
                        json TEXT NOT NULL,
                        PRIMARY KEY (id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """
    };

    public SQLDataAccess() throws DataAccessException {
        instance = this;
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create tables: " + ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUserData(rs);
                    } else {
                        throw new Exception("");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("No user with username " + username);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuthData(rs);
                    } else {
                        throw new Exception("");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: access denied");
        }
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    } else {
                        throw new Exception("");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("cannot get game with id " + gameId);
        }
    }

    @Override
    public GameData[] getGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    var games = new GameData[countGames()];
                    var i = 0;
                    while (rs.next()) {
                        games[i++] = readGameData(rs);
                    }
                    return games;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Cannot retrieve games");
        }
    }

    @Override
    public int countGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    var i = 0;
                    while (rs.next()) {
                        i++;
                    }
                    return i;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Cannot retrieve games");
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statementString = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statementString, user.getUsername(), user.getPassword(), user.getEmail());
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String statementString = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        executeUpdate(statementString, auth.getAuthToken(), auth.getUsername());
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        String statementString = "INSERT INTO games (json) VALUES (?)";
        executeUpdate(statementString, game);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String statementString = "UPDATE games SET json=? WHERE id=?";
        executeUpdate(statementString, game, game.getId());
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statementString = "DELETE FROM auths WHERE authToken=?";
        executeUpdate(statementString, authToken);
    }

    @Override
    public void deleteAll() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement("DELETE FROM users")) {
                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement("DELETE FROM auths")) {
                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement("DELETE FROM games")) {
                ps.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: cannot delete all data");
        }
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var json = rs.getString("json");
        var gameData = new Gson().fromJson(json, GameData.class);
        return gameData.setId(id);
    }

    private UserData readUserData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try {
            var conn = DatabaseManager.getConnection();
            var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                switch (param) {
                    case String p -> ps.setString(i + 1, p);
                    case Integer p -> ps.setInt(i + 1, p);
                    case GameData p -> {
                        String json = new Gson().toJson(p);
                        ps.setString(i + 1, json);
                    }
                    case null -> ps.setNull(i + 1, NULL);
                    default -> {
                    }
                }
            }
            ps.executeUpdate();

            var rs = ps.getGeneratedKeys();
            if (rs.next()) {
                rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
