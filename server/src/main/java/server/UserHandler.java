package server;

import dataaccess.DataAccess;
import model.*;
import service.UserService;
import spark.*;
import com.google.gson.Gson;

import dataaccess.DataAccessException;

public class UserHandler {
    private final UserService userService;
    
    public UserHandler(DataAccess dataAccess) {
        userService = new UserService(dataAccess);
    }
    
    public RegisterResult register(Request req, Response res) throws DataAccessException, Exception {
        var registerRequest = getBody(req, UserData.class);
        return userService.register(registerRequest);
    }

    public LoginResult login(Request req, Response res) throws DataAccessException {
        var loginRequest = getBody(req, LoginRequest.class);
        return userService.login(loginRequest);
    }

    public BaseResult logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        userService.logout(logoutRequest);
        return new BaseResult();
    }

    private <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}