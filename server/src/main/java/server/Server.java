package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import model.*;
import server.websocket.WebSocketHandler;
import spark.*;
import com.google.gson.Gson;

public class Server {
    private final boolean useInMemoryDatabase = false;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        DataAccess dataAccess;
        if (useInMemoryDatabase) {
            dataAccess = new MemoryDataAccess();
        } else {
            try {
                dataAccess = new SQLDataAccess();
            } catch (DataAccessException e) {
                throw new RuntimeException(
                        "Unable to access db with credentials in db.properties"
                );
            }
        }
        userHandler = new UserHandler(dataAccess);
        gameHandler = new GameHandler(dataAccess);
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            RegisterResult result = new RegisterResult();
            try {
                result = userHandler.register(req, res);
            } catch (DataAccessException e) {
                res.status(403);
                result.setMessage(e.getMessage());
            } catch (Exception e) {
                res.status(400);
                result.setMessage(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.post("/session", (req, res) -> {
            LoginResult result = new LoginResult();
            try {
                result = userHandler.login(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                result.setMessage(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.delete("/session", (req, res) -> {
            BaseResult result = new BaseResult();
            try {
                result = userHandler.logout(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                result = new BaseResult(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.get("/game", (req, res) -> {
            ListGamesResult result = new ListGamesResult();
            try {
                result = gameHandler.listGames(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                result.setMessage(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.post("/game", (req, res) -> {
            CreateGameResult result = new CreateGameResult();
            try {
                result = gameHandler.createGame(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                result.setMessage(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.put("/game", (req, res) -> {
            CreateGameResult result = new CreateGameResult();
            try {
                result = gameHandler.joinGame(req, res);
            } catch (RuntimeException e) {
                res.status(403);
                result.setMessage(e.getMessage());
            } catch (DataAccessException e) {
                res.status(401);
                result.setMessage(e.getMessage());
            } catch (Exception e) {
                res.status(400);
                result.setMessage(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.delete("/db", (req, res) -> {
            BaseResult result = new BaseResult();
            gameHandler.clear();
            return new Gson().toJson(result);
        });

        // home page
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        // This line initializes the server and can be removed once you have a
        // functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
