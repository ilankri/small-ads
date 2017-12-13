package client;

/*
 * Exception thrown when the user tries to communicate with the server
 * after signing out.
 */
class NoServerConnectionException extends Exception {
    @Override
    public String getMessage() {
        return "No connection with the server";
    }
}
