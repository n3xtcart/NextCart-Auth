package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Token;

public class TokenAdminDAO extends Dao<Token> {

    @Override
    public void update(Long id, Token item) {
        Token tokenAdmin = database.get(id);
        tokenAdmin.setToken(item.getToken());
        tokenAdmin.setUser(item.getUser());
    }
}
