package it.nextre.corsojava.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;

public class RoleJdbcDao extends JdbcDao<Role> {
	private static RoleJdbcDao instance = new RoleJdbcDao();

	public static RoleJdbcDao getInstance() {
		return instance;
	}

	private RoleJdbcDao() {
		super(Role.class, "role");
	}

	public Set<Role> getByGroupId(Long id) {
			String query="SELECT descrizione,id,userId,groupId,"
					+ "admin,priority"
					+ " FROM role where GroupId=? ";
			PreparedStatement ps=null;
			ResultSet rs=null;
			try (Connection connection=getConnection()){
				ps=connection.prepareStatement(query);
				int i=1;
				ps.setLong(i, id);
				rs=ps.executeQuery();
				Set<Role> roles = new HashSet<>();
				while(rs.next()) {
					Role role = new Role();
					role.setId(rs.getLong("id"));
					role.setDescrizione(rs.getString("descrizione"));
					role.setAdmin(rs.getBoolean("admin"));
					role.setPriority(rs.getLong("priority"));
					User user = new User();
					user.setId( rs.getLong("userId"));
					Group group = new Group();
					group.setId(rs.getLong("groupId"));
					role.setGroup(group);
					roles.add(role);
				}
				return roles;
				
				
			} catch (Exception e) {
				// TODO: handle exception
			}return null;
			
		}

}
