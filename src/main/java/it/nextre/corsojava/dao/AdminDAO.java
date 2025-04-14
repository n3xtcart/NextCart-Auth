package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Admin;

public class AdminDAO extends Dao<Admin> {

    @Override
    public void update(Long id, Admin item) {
        Admin admin = database.get(id);
        if (item.getNome() != null) admin.setNome(item.getNome());
        if (item.getCognome() != null) admin.setCognome(item.getCognome());
        if (item.getEmail() != null) admin.setEmail(item.getEmail());
        if (item.getRole() != null) admin.setRole(item.getRole());
        if (item.getPassword() != null) admin.setPassword(item.getPassword());
    }
}
