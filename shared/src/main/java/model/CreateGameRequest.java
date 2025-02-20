package model;

import java.util.Objects;

public class CreateGameRequest {
    private final String gameName;
    private final String authToken;

    public CreateGameRequest(String gameName, String authToken) {
        this.gameName = gameName;
        this.authToken = authToken;
    }

    public String getGameName() {
        return gameName;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateGameRequest that = (CreateGameRequest) o;
        return Objects.equals(gameName, that.gameName) && Objects.equals(authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameName, authToken);
    }

    @Override
    public String toString() {
        return "CreateGameRequest{" +
                "gameName='" + gameName + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
