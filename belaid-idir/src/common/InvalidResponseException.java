package common;

/*
 * Exception thrown by the response parser when the string to be parsed
 * is syntactically incorrect.
 */
public class InvalidResponseException extends Exception {

    /*
     * When this exception is thrown, that means that the server
     * response was invalid.
     */
    @Override public String getMessage() {
        return "Invalid server response";
    }
}
