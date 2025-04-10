package websocket.messages;

import com.google.gson.Gson;

public class Error extends ServerMessage {
    public String message;
    public Error(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
