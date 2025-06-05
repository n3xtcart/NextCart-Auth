package it.nextre.corsojava.dao.jdbc;

import it.nextre.corsojava.entity.Group;

public class GroupJdbcDao extends JdbcDao<Group> {
	private static GroupJdbcDao instance = new GroupJdbcDao();

	public static GroupJdbcDao getInstance() {
		return instance;
	}

	private GroupJdbcDao() {
		super(Group.class, "groupT");
	}
	
	
	

}
