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
                        gameName VARCHAR(255) NOT NULL,
                        gameState JSON,
                        PRIMARY KEY (id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """
    };

    public SQLDataAccess() throws DataAccessException {
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
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(NULL, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUserData(rs);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(NULL, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuthData(rs);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GameData getGame(int gameId) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GameData[] getGames() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    var games = new GameData[100];
                    var i = 0;
                    while (rs.next()) {
                        games[i++] = readGameData(rs);
                    }
                    return games;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createUser(UserData user) {
        String statementString = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statementString, user.getUsername(), user.getPassword(), user.getEmail());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createAuth(AuthData auth) {
        String statementString = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try {
            executeUpdate(statementString, auth.getAuthToken(), auth.getUsername());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createGame(GameData game) {
        String statementString = "INSERT INTO games (json) VALUES (?)";
        try {
            executeUpdate(statementString, game);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateGame(GameData game) {
        String statementString = "UPDATE games SET json=? WHERE id=?";
        try {
            executeUpdate(statementString, game, game.getId());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        String statementString = "DELETE FROM auths WHERE authToken=?";
        try {
            executeUpdate(statementString, authToken);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        try (var conn = DatabaseManager.getConnection()) {
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
            e.printStackTrace();
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p)
                        ps.setString(i + 1, p);
                    else if (param instanceof Integer p)
                        ps.setInt(i + 1, p);
                    else if (param instanceof GameData p)
                        ps.setString(i + 1, p.toString());
                    else if (param == null)
                        ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
