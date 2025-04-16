package it.nextre.corsojava.exception;

public class UserMissingException extends RuntimeException {
    public UserMissingException(String message) {
        super(message);
    }
}
