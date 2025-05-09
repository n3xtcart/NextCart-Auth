package it.nextre.corsojava.dao.jdbc;

import it.nextre.corsojava.entity.Token;

public class TokenJdbcDao extends JdbcDao<Token> {
	private static TokenJdbcDao instance = new TokenJdbcDao();

	public static TokenJdbcDao getInstance() {
		return instance;
	}

	private TokenJdbcDao() {
		super(Token.class, "token");
	}

}
