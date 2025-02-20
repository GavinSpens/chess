package model;

public class RegisterRequest {
    private final String username;
    private final String password;
    private final String email;

    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterRequest that = (RegisterRequest) o;
        return username.equals(that.username) && password.equals(that.password) && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return username.hashCode() + password.hashCode() + email.hashCode();
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
