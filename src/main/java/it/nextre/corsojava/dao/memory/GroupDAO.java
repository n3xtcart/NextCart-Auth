package it.nextre.corsojava.dao.memory;



import it.nextre.corsojava.entity.Group;

public class GroupDAO extends Dao<Group> {
	private static GroupDAO instance=new GroupDAO();
	
	
	public static GroupDAO getIstance() {
		return instance;
	}
	private GroupDAO() {
		
		super();
	}

	@Override
	public void update(Long id, Group item) {
		Group group = database.get(id);
		if(item.getRoles()!=null)group.setRole(item.getRoles());
		
	}
	}


