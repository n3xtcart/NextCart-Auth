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
		Group group = new Group();
		group.setId(1L);
		user.setGroup(group);
		dao.add(user);
		User byId = dao.getById(3L);
		System.out.println(byId);
	}
	
	
//	@Test
//	public void testUpdate() {
//
//		Method[] methods = PreparedStatement.class.getMethods();
//		for(Method m : methods) {
//			System.out.println(m.getName());
//		}
//		
//	}

}
