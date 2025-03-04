package model;

public class BaseResult {
    private String message;

    public BaseResult() {}

    public BaseResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
