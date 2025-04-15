package it.nextre.corsojava.dao;



import it.nextre.corsojava.entity.Group;

public class GroupDAO extends Dao<Group> {
	
	public GroupDAO() {
		
		super();
	}

	@Override
	public void update(Long id, Group item) {
		Group group = database.get(id);
		if(item.getRole()!=null)group.setRole(item.getRole());
		
	}}
