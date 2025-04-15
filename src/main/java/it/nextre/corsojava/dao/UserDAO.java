package it.nextre.corsojava.dao;


import it.nextre.corsojava.entity.User;

public class UserDAO  extends Dao<User> {
	
	public UserDAO() {
		super();
	}

	@Override
	public void update(Long id, User item) {
		User user = database.get(id);
		user.setNome(item.getNome());
		user.setEmail(item.getEmail());
		user.setPassword(item.getPassword());
		user.setCognome(item.getCognome());	
		user.setGroup(item.getGroup());
		
		
	}



	
	

		
		

    
}
