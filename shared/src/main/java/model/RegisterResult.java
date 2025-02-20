package model;

public class RegisterResult {
    private final String authToken;
    private final String username;

    public RegisterResult(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
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
        return authToken.equals(that.authToken) && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return authToken.hashCode() + username.hashCode();
    }

    @Override
    public String toString() {
        return "RegisterResult{" +
                "authToken='" + authToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
