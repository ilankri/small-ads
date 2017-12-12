package common;

public class InvalidResponseException extends Exception {
    @Override public String getMessage() {
        return "Invalid server response";
    }
}
