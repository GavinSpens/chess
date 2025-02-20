package server;

import spark.*;

public class Server {

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.externalLocation(
                "C:\\Users\\gavin\\OneDrive\\Desktop\\CS240\\chess\\server\\src\\main\\resources\\web");

        // Register your endpoints and handle exceptions here.
        Spark.get("/user", (req, res) -> {
            return UserHandler.register(req, res);
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
