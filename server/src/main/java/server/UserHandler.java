package server;

import model.*;
import spark.*;
import com.google.gson.Gson;

import dataaccess.DataAccessException;

public abstract class UserHandler {
    public static RegisterResult register(Request req, Response res) throws DataAccessException, Exception {
        var registerRequest = getBody(req, RegisterRequest.class);
        return service.UserService.register(registerRequest);
    }

    public static LoginResult login(Request req, Response res) throws DataAccessException {
        var loginRequest = getBody(req, LoginRequest.class);
        return service.UserService.login(loginRequest);
    }

    public static BaseResult logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        service.UserService.logout(logoutRequest);
        return new BaseResult();
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}