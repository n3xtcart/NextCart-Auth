package it.nextre.corsojava.dao.jdbc;

import io.agroal.api.AgroalDataSource;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
@ApplicationScoped
public class GroupJdbcDao extends JdbcDao<Group> {

	@Inject
	public GroupJdbcDao(AgroalDataSource dataSource) {
		super(Group.class, "groupT", dataSource);
		System.out.println("inject GroupJdbcDao with dataSource: " + dataSource);
	}
	
	
 public GroupJdbcDao() {
		super(Group.class, "groupT", null);
	}
	

	
	
	

}
