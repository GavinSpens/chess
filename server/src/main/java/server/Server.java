package server;

import dataaccess.DataAccessException;
import model.*;
import service.GameService;
import spark.*;
import com.google.gson.Gson;

public class Server {

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.externalLocation(
                "C:\\Users\\gavin\\OneDrive\\Desktop\\CS240\\chess\\server\\src\\main\\resources\\web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            RegisterResult result = new RegisterResult();
            try {
                result = UserHandler.register(req, res);
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
                result = UserHandler.login(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                result.setMessage(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.delete("/session", (req, res) -> {
            BaseResult result = new BaseResult();
            try {
                result = UserHandler.logout(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                result = new BaseResult(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.get("/game", (req, res) -> {
            ListGamesResult result = new ListGamesResult();
            try {
                result = GameHandler.listGames(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                result.setMessage(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.post("/game", (req, res) -> {
            CreateGameResult result = new CreateGameResult();
            try {
                result = GameHandler.createGame(req, res);
            } catch (DataAccessException e) {
                res.status(401);
                result.setMessage(e.getMessage());
            }
            return new Gson().toJson(result);
        });

        Spark.put("/game", (req, res) -> {
            CreateGameResult result = new CreateGameResult();
            try {
                result = GameHandler.joinGame(req, res);
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
            GameService.clear();
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
