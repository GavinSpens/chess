package server;

import model.*;
import spark.*;
import com.google.gson.Gson;

import dataaccess.DataAccessException;

public abstract class UserHandler {
    public static String register(Request req, Response res) throws DataAccessException, Exception {
        var registerRequest = getBody(req, RegisterRequest.class);
        RegisterResult result = service.UserService.register(registerRequest);
        return new Gson().toJson(result);
    }

    public static String login(Request req, Response res) throws DataAccessException {
        var loginRequest = getBody(req, LoginRequest.class);
        LoginResult result = service.UserService.login(loginRequest);
        return new Gson().toJson(result);
    }

    public static String logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        service.UserService.logout(logoutRequest);
        return "{}";
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}