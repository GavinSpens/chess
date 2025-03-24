package model;

// import chess.ChessGame.TeamColor;

import java.util.Objects;

public class JoinGameReq {
    private final String playerColor;
    private final int gameID;

    public JoinGameReq(JoinGameRequest joinGameRequest) {
        gameID = joinGameRequest.getGameID();
        playerColor = joinGameRequest.getPlayerColor();
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JoinGameReq that = (JoinGameReq) o;
        return gameID == that.gameID && Objects.equals(playerColor, that.playerColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerColor, gameID);
    }

    @Override
    public String toString() {
        return "JoinGameReq{" +
                "playerColor='" + playerColor + '\'' +
                ", gameID=" + gameID +
                '}';
    }
}
