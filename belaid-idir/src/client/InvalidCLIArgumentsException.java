package client;

class InvalidCLIArgumentsException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid arguments";
    }
}
