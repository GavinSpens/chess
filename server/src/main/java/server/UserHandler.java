package server;

import model.*;
import spark.*;
import com.google.gson.Gson;

import dataaccess.DataAccessException;

public abstract class UserHandler {
    public static String register(Request req, Response res) throws DataAccessException, Exception {
        var userData = getBody(req, UserData.class);
        RegisterRequest request = new RegisterRequest(userData.getUsername(), userData.getPassword(), userData.getEmail());
        var result = service.UserService.register(request);
        return new Gson().toJson(result);
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}