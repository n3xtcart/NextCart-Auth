package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Admin;

public class AdminDAO extends Dao<Admin> {

    @Override
    public void update(Long id, Admin item) {
        Admin toModify = database.get(id);
        if (item.getNome() != null) toModify.setNome(item.getNome());
        if (item.getCognome() != null) toModify.setCognome(item.getCognome());
        if (item.getEmail() != null) toModify.setEmail(item.getEmail());
        if (item.getRole() != null) toModify.setRole(item.getRole());
        if (item.getPassword() != null) toModify.setPassword(item.getPassword());
    }
}
