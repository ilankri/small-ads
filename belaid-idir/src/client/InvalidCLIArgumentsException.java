package client;
//EXCEPTION ARGUMENT INVALIDE//
class InvalidCLIArgumentsException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid arguments";
    }
}
