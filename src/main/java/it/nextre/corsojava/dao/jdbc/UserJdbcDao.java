package it.nextre.corsojava.dao.jdbc;

import java.util.Optional;

import it.nextre.corsojava.entity.User;

public class UserJdbcDao extends JdbcDao<User> {
	private static UserJdbcDao instance = new UserJdbcDao();

	public static UserJdbcDao getInstance() {
		return instance;
	}

	private UserJdbcDao() {
		super(User.class,"user");
	}

	public Optional<User> findByEmailPassword(String email, String password) {
		String[] colStrings={"email","password"};
		String[] val={email,password};
	return Optional.of(findBy_(colStrings, val));
	}

	

	public User getByEmail(String email) {
		String[] colStrings={"email"};
		String[] val={email};
	return findBy_(colStrings, val);
	}

	

}
