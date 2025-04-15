package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Token;

public class TokenAdminDAO extends Dao<Token> {

    @Override
    public void update(Long id, Token item) {
        Token tokenAdmin = database.get(id);
        if (item.getValue() != null)
            tokenAdmin.setValue(item.getValue());
        if (item.getUser() != null)
            tokenAdmin.setUser(item.getUser());
    }
}
