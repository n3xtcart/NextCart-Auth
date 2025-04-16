package it.nextre.corsojava.exception;

public class RoleMissingException extends RuntimeException {
    public RoleMissingException(String message) {
        super(message);
    }
}
