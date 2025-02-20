package server;

import model.*;
import spark.*;
import com.google.gson.Gson;

public abstract class UserHandler {
    public static String register(Request req, Response res) {
        var userData = getBody(req, UserData.class);
        
        return null;
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}