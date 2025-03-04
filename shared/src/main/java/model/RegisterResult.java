package model;

import java.util.Objects;

public class RegisterResult extends BaseResult {
    private final String authToken;
    private final String username;

    public RegisterResult(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public RegisterResult() {
        authToken = null;
        username = null;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterResult that = (RegisterResult) o;
        return Objects.equals(authToken, that.authToken) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }

    @Override
    public String toString() {
        return "RegisterResult{" +
                "authToken='" + authToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
