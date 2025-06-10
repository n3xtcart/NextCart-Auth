package it.nextre.corsojava.dao.jdbc;

import io.agroal.api.AgroalDataSource;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RoleJdbcDao extends JdbcDao<Role> {

	@Inject
	public  RoleJdbcDao(AgroalDataSource dataSource) {
		super(Role.class, "role",dataSource);
	}
	public RoleJdbcDao() {
		super(Role.class, "role", null);
	}
	



}
