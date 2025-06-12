package it.nextre.corsojava.exception;

public class ControllerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ControllerException(String message) {
		super(message);
	}

	public ControllerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ControllerException(Throwable cause) {
		super(cause);
	}

}
