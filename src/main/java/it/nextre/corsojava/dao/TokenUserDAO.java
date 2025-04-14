package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Token;

public class TokenUserDAO extends Dao<Token> {
	
	public TokenUserDAO() {
		super();
		
	}

	@Override
	public void update(Long id, Token item) {
		Token tokenUser = database.get(id);
		tokenUser.setToken(item.getToken());
		tokenUser.setUser(item.getUser());
	}

}
