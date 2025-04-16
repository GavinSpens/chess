package model;

import chess.ChessGame;

import java.util.Objects;

public record GameData(
        Integer gameID, String whiteUsername,
        String blackUsername, String gameName, ChessGame game,
        boolean end) {

    public GameData setId(Integer gameID) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game, end);
    }

    public Integer getId() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameData gameData = (GameData) o;
        return Objects.equals(gameID, gameData.gameID)
                && whiteUsername.equals(gameData.whiteUsername)
                && blackUsername.equals(gameData.blackUsername)
                && gameName.equals(gameData.gameName)
                && game.equals(gameData.game);
    }

    @Override
    public String toString() {
        return "GameData{" +
                "gameID=" + gameID +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                ", game=" + game +
                '}';
    }
}
