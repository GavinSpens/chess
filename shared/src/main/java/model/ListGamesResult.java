package model;

public class ListGamesResult extends BaseResult {
    private GameData[] gameData;

    public ListGamesResult(GameData[] gameData) {
        this.gameData = gameData;
    }

    public ListGamesResult() {
        gameData = null;
    }

    public GameData[] getGameData() {
        return gameData;
    }
}
