package websocket.messages;

import com.google.gson.Gson;
import model.GameData;

public class LoadGame extends ServerMessage {
    public GameData game;
    public LoadGame(ServerMessageType type, GameData gameData) {
        super(type);
        this.game = gameData;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
