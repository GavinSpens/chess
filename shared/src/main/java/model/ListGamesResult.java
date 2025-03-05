package model;

public class ListGamesResult extends BaseResult {
    private GameData[] games;

    public ListGamesResult(GameData[] gameData) {
        this.games = gameData;
    }

    public ListGamesResult() {
        games = null;
    }

    public GameData[] getGames() {
        return games;
    }
}
