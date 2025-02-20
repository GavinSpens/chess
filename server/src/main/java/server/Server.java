package server;

import dataaccess.DataAccessException;
import service.GameService;
import spark.*;

public class Server {

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.externalLocation(
                "C:\\Users\\gavin\\OneDrive\\Desktop\\CS240\\chess\\server\\src\\main\\resources\\web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            try {
                return UserHandler.register(req, res);
            } catch (DataAccessException e) {
                res.status(403);
                return e.getMessage();
            } catch (Exception e) {
                res.status(400);
                return "Error: bad request";
            }
        });

        Spark.post("/session", (req, res) -> {
            try {
                return UserHandler.login(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                return e.getMessage();
            }
        });

        Spark.delete("/session", (req, res) -> {
            try {
                return UserHandler.logout(req, res);
            } catch (DataAccessException e) {
                return e.getMessage();
            }
        });

        Spark.get("/game", (req, res) -> {
            try {
                return GameHandler.listGames(req, res);
            } catch (DataAccessException e) {
                return e.getMessage();
            }
        });

        Spark.post("/game", (req, res) -> {
            try {
                return GameHandler.createGame(req, res);
            } catch (DataAccessException e) {
                return e.getMessage();
            }
        });

        Spark.put("/game", (req, res) -> {
            try {
                return GameHandler.joinGame(req, res);
            } catch (RuntimeException e) {
                res.status(403);
                return e.getMessage();
            } catch (DataAccessException e) {
                res.status(401);
                return e.getMessage();
            } catch (Exception e) {
                res.status(400);
                return e.getMessage();
            }
        });

        Spark.delete("/db", (req, res) -> {
            GameService.clear();
            return "{}";
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
