package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Role;

public class RoleDAO extends Dao<Role> {

    @Override
    public void update(Long id, Role item) {
        Role toModify = database.get(id);
        if (item.getRole() != null) toModify.setRole(item.getRole());
        if (item.getPriority() != null) toModify.setPriority(item.getPriority());
    }
}
