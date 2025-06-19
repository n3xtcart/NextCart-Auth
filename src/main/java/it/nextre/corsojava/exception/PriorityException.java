package it.nextre.corsojava.exception;

public class PriorityException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PriorityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	
	}

	public PriorityException(String message, Throwable cause) {
		super(message, cause);
	}

	public PriorityException() {
		super();
	}

	public PriorityException(String message) {
		super(message);
	}

	public PriorityException(Throwable cause) {
		super(cause);
	}
	
	
	

}
