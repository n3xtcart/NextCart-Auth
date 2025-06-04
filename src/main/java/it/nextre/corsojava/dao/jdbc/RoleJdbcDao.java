package it.nextre.corsojava.dao.jdbc;

import it.nextre.corsojava.entity.Role;

public class RoleJdbcDao extends JdbcDao<Role> {
	private static RoleJdbcDao instance = new RoleJdbcDao();

	public static RoleJdbcDao getInstance() {
		return instance;
	}

	private RoleJdbcDao() {
		super(Role.class, "role");
	}



}
