package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Role;

public class RoleDAO extends Dao<Role> {

	@Override
	public void update(Long id, Role item) {
		Role role = database.get(id);
		if(item.isAdmin()!=null)role.setAdmin(item.isAdmin());
		if(item.getPriority()!=null) role.setPriority(item.getPriority());
		if(item.getDescrizione()!=null)role.setDescrizione(item.getDescrizione());
		
	}


}
