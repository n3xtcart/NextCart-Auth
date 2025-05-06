package it.nextre.corsojava.dao.jdbc;

import org.junit.jupiter.api.Test;

import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.User;

public class jdbcDaoTest {
	
	@Test
	public void test() {
		UserJdbcDao dao = UserJdbcDao.getInstance();
		User user = new User();
		user.setId(1L);
		user.setCognome("scarfone");
		user.setNome("salvatore");
		user.setEmail("bo");
		user.setPassword("lalalal");
		user.setGroup(new Group());
		dao.add(user);
	}

}
