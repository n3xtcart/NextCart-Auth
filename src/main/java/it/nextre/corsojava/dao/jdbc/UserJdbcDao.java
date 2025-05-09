package it.nextre.corsojava.dao.jdbc;

import it.nextre.corsojava.entity.User;

public class UserJdbcDao extends JdbcDao<User> {
	private static UserJdbcDao instance = new UserJdbcDao();

	public static UserJdbcDao getInstance() {
		return instance;
	}

	private UserJdbcDao() {
		super(User.class,"user");
	}

	

}
