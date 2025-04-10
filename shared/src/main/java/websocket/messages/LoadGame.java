package websocket.messages;

import com.google.gson.Gson;
import model.GameData;

public class LoadGame extends ServerMessage {
    public GameData gameData;
    public LoadGame(ServerMessageType type, GameData gameData) {
        super(type);
        this.gameData = gameData;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
