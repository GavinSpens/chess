package websocket.messages;

import com.google.gson.Gson;

public class Error extends ServerMessage {
    public String errorMessage;
    public Error(ServerMessageType type, String message) {
        super(type);
        this.errorMessage = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
