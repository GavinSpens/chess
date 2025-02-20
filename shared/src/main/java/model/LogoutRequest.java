package model;

import java.util.Objects;

public class LogoutRequest {
    private final String authToken;

    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LogoutRequest that = (LogoutRequest) o;
        return Objects.equals(authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authToken);
    }

    @Override
    public String toString() {
        return "LogoutRequest{" +
                "authToken='" + authToken + '\'' +
                '}';
    }
}
