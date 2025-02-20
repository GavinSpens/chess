package model;

public class AuthData {
    private final String authToken;
    private final String username;

    AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    String getAuthToken() {
        return authToken;
    }

    String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthData authData = (AuthData) o;
        return authToken.equals(authData.authToken) && username.equals(authData.username);
    }

    @Override
    public int hashCode() {
        return authToken.hashCode() + username.hashCode();
    }

    @Override
    public String toString() {
        return "AuthData{" +
                "authToken='" + authToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}