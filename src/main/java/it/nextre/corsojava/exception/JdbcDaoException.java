package it.nextre.corsojava.exception;

public class JdbcDaoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JdbcDaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public JdbcDaoException(Throwable cause) {
		super(cause);
	}

}
