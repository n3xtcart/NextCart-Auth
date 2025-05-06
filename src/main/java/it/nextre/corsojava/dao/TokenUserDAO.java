package it.nextre.corsojava.dao;

import java.util.List;

import it.nextre.corsojava.entity.Token;

public class TokenUserDAO extends Dao<Token> {
	private static TokenUserDAO instance = new TokenUserDAO();


	public static TokenUserDAO getIstance() {
		return instance;
	}

	private TokenUserDAO() {

		super();
	}

	@Override
	public void update(Long id, Token item) {
		Token tokenUser = database.get(id);
		if(item.getValue()!=null) tokenUser.setValue(item.getValue());
		if(item.getUser()!=null) tokenUser.setUser(item.getUser());
	}
	
	public List<Token> getTokenByIdUser(Long id) {
		return database.values().stream().filter(token -> token.getUser().getId().equals(id)).toList();
	}
	
	public Token getTokenByValue(String value) {
		return database.values().stream().filter(token -> token.getValue().equals(value)).findFirst().orElse(null);
	}

}
