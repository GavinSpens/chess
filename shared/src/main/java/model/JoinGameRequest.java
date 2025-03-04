package model;

import java.util.Objects;

public class JoinGameRequest {
    private final String playerColor;
    private final Integer gameID;
    private final String authToken;

    public JoinGameRequest(String playerColor, Integer gameID, String authToken) {
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.authToken = authToken;
    }

    public JoinGameRequest(JoinGameReq req, String authToken) {
        this.gameID = req.getGameID();
        this.playerColor = req.getPlayerColor();
        this.authToken = authToken;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JoinGameRequest that = (JoinGameRequest) o;
        return Objects.equals(gameID, that.gameID)
                && Objects.equals(playerColor, that.playerColor)
                && Objects.equals(authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerColor, gameID, authToken);
    }

    @Override
    public String toString() {
        return "JoinGameReq{" +
                "playerColor='" + playerColor + '\'' +
                ", gameID=" + gameID +
                ", authToken='"+ authToken + '\'' +
                '}';
    }
}
