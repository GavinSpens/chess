package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ResponseException extends Exception {

    public ResponseException(int ignored, String message) {
        super(message);
    }

    public static ResponseException fromJson(InputStream stream, int status) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
//        var status = ((Double)map.get("status")).intValue();
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }
}