package model;

import java.util.Objects;

public class CreateGameResult extends BaseResult {
    private final Integer gameID;

    public CreateGameResult(Integer gameID) {
        this.gameID = gameID;
    }

    public CreateGameResult() {
        gameID = null;
    }

    public Integer getGameID() {
        return gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateGameResult that = (CreateGameResult) o;
        return Objects.equals(gameID, that.gameID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameID);
    }

    @Override
    public String toString() {
        return "CreateGameResult{" +
                "gameID='" + gameID + '\'' +
                '}';
    }
}
