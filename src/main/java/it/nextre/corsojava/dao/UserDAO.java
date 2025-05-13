package it.nextre.corsojava.dao;


import java.util.Optional;

import it.nextre.corsojava.entity.User;

public class UserDAO extends Dao<User> {
	private static UserDAO instance = new UserDAO();


	public static UserDAO getInstance() {
		return instance;
	}

	private UserDAO() {

		super();
	}

    @Override
    public void update(Long id, User item) {
        User user = database.get(id);
        if (item.getNome() != null) user.setNome(item.getNome());
        if (item.getEmail() != null) user.setEmail(item.getEmail());
        if (item.getPassword() != null) user.setPassword(item.getPassword());
        if (item.getCognome() != null) user.setCognome(item.getCognome());
        if (item.getGroup() != null) user.setGroup(item.getGroup());
        if(item.getActive()!=null)user.setActive(item.getActive());


    }


	//TODO gestire active di user

    public User getByEmail(String email) {
        Optional<User> u = database.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
        return u.orElse(null);
 
    }

	public Optional<User> findByEmailPassword(String email, String password) {
		Optional<User> u = database.values().stream()
				.filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password)).findFirst();
		return u;
	}

}
