package it.nextre.corsojava.dao.jdbc;

import org.junit.jupiter.api.Test;

import it.nextre.corsojava.entity.User;

public class jdbcDaoTest {
	
	@Test
	public void test() {
		UserJdbcDao dao = UserJdbcDao.getInstance();
		User user = new User();
		user.setId(1L);
		user.setCognome("cognome");
		user.setNome("nome");
		user.setEmail("email");
		user.setPassword("password");
		user.setGroup(null);
		dao.add(user);
	}

}
