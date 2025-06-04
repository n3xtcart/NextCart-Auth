package it.nextre.corsojava.dao.jdbc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.exception.JdbcDaoException;

public class GroupJdbcDao extends JdbcDao<Group> {
	private static GroupJdbcDao instance = new GroupJdbcDao();

	public static GroupJdbcDao getInstance() {
		return instance;
	}

	private GroupJdbcDao() {
		super(Group.class, "groupT");
	}
	
	
	public void addRoleToGroup(Long groupId, Long roleId) throws SQLException {
		String query = "INSERT INTO group_role_mapping (groupId, roleId) VALUES (?, ?)";
		try (var connection = getConnection();
			 var ps = connection.prepareStatement(query)) {
			ps.setLong(1, groupId);
			ps.setLong(2, roleId);
			ps.executeUpdate();
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException("File not found "+ e.getMessage(), e);
		} catch (SQLException e) {
			throw new JdbcDaoException("Sql exception "+e.getMessage(), e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO Exception", e);
		} 
	}

}
