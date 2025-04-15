package it.nextre.corsojava.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.nextre.corsojava.dao.UserDAO;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.User;


public class UserDAOTest {
	private UserDAO dao;
	
	@BeforeEach
	public void setUp() {
		// Initialize the database or any required setup before each test
		this.dao = new UserDAO();
		for(int i = 0; i < 10; i++) {
			User user = new User();
			user.setId((long) i);
			user.setCognome("cognome" + i);
			user.setNome("meUser" + i);
			user.setEmail("user" + i + "@example.com");
			user.setPassword("password" + i);
			user.setGroup(new Group());
			dao.add(user);
		}
	}
	
	@Test
	public void testUpdate() {
		User user = new User();
		int i=15;
		user.setCognome("cognome" + i);
		user.setNome("nome" + i);
		user.setEmail("user" + i + "@example.com");
		user.setPassword("password" + i);
		Group group = new Group();
		user.setGroup(group);
		
		int sizeBeforeUpdate = dao.getAll().size();
		
		dao.update(1L, user);
		
		User updatedUser = dao.getById(1L);
		assertEquals("cognome" + i, updatedUser.getCognome());
		assertEquals("nome" + i, updatedUser.getNome());
		assertEquals("user" + i + "@example.com", updatedUser.getEmail());
		assertEquals("password" + i, updatedUser.getPassword());
		assertEquals(group, updatedUser.getGroup());
		assertEquals(sizeBeforeUpdate, dao.getAll().size());
		
		
		
	}
	
	@Test
	public void addTest() {
		User user = new User();
		int i=15;
		user.setCognome("cognome" + i);
		user.setNome("nome" + i);
		user.setEmail("user" + i + "@example.com");
		user.setPassword("password" + i);
		Group group = new Group();
		user.setGroup(group);
		
		int sizeBeforeAdd = dao.getAll().size();
		
		dao.add(user);
		
		User addedUser = dao.getById(11L);
		assertEquals("cognome" + i, addedUser.getCognome());
		assertEquals("nome" + i, addedUser.getNome());
		assertEquals("user" + i + "@example.com", addedUser.getEmail());
		assertEquals("password" + i, addedUser.getPassword());
		assertEquals(group, addedUser.getGroup());
		assertEquals(sizeBeforeAdd + 1, dao.getAll().size());
		
		
	}
	
	
	@Test
	public void deleteTest() {
		dao.delete(1L);
		
		User deletedUser = dao.getById(1L);
		
		assertEquals(null, deletedUser);
	}
	
	public void getByIdTest() {
		User user = dao.getById(1L);
		
		assertNotNull(user);
		assertEquals("nome0", user.getNome());
	}

}
