package it.nextre.corsojava.exception;

public class PasswordMatchingException extends RuntimeException {
    public PasswordMatchingException(String message) {
        super(message);
    }
}
