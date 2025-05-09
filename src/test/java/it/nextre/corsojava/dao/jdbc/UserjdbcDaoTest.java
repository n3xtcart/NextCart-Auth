package it.nextre.corsojava.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.User;

public class UserjdbcDaoTest {
	
	@Test
	public void testSaveUser() {
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
		Long long1 = dao.add(user);
		User byId = dao.getById(long1);
		assertEquals(long1, byId.getId());
		assertEquals(user.getCognome(), byId.getCognome());
		assertEquals(user.getNome(), byId.getNome());
		assertEquals(user.getEmail(), byId.getEmail());
		assertEquals(user.getPassword(), byId.getPassword());
		assertEquals(user.getGroup().getId(), byId.getGroup().getId());
	}
	
	
	@Test
	public void testUpdateUser() {
		UserJdbcDao dao = UserJdbcDao.getInstance();
		User user = new User();
		user.setId(1L);
		user.setCognome("Scarfone");
		user.setNome("Salvatore");
		user.setEmail("bo");
		user.setPassword("lalalal");
		Group group = new Group();
		group.setId(3L);
		user.setGroup(group);
		dao.update(2L,user);
		User byId = dao.getById(2L);
		assertEquals(user.getCognome(), byId.getCognome());
		assertEquals(user.getNome(), byId.getNome());
		assertEquals(user.getEmail(), byId.getEmail());
		assertEquals(user.getPassword(), byId.getPassword());
		assertEquals(user.getGroup().getId(), byId.getGroup().getId());
		assertNotEquals(user.getId(), byId.getId());
		
		
		}
	
	
	@Test
	public void testDeleteUser() {
		UserJdbcDao dao = UserJdbcDao.getInstance();
		User user = new User();
		user.setId(1L);
		user.setCognome("Scarfone");
		user.setNome("Salvatore");
		user.setEmail("bo");
		user.setPassword("lalalal");
		Group group = new Group();
		group.setId(3L);
		user.setGroup(group);
		Long long1 = dao.add(user);
		dao.delete(long1);
		User byId = dao.getById(long1);
		assertEquals(null, byId);
		
	}
	
	@Test
	public void testGetAllUser() {
		UserJdbcDao dao = UserJdbcDao.getInstance();
		User user = new User();
		user.setId(1L);
		user.setCognome("Scarfone");
		user.setNome("Salvatore");
		user.setEmail("bo");
		user.setPassword("lalalal");
		Group group = new Group();
		group.setId(3L);
		user.setGroup(group);
		int size = dao.getAll().size();
		Long long1 = dao.add(user);
		int size2 = dao.getAll().size();
		assertEquals(size+1, size2);
		
		
	}
		
	

}
