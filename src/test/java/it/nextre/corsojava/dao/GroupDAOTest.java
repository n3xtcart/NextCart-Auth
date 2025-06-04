package it.nextre.corsojava.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.nextre.corsojava.dao.memory.GroupDAO;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;

public class GroupDAOTest {
	private GroupDAO dao;
	
	@BeforeEach
	public void setUp() {
		// Initialize the database or any required setup before each test
		this.dao = GroupDAO.getIstance();
		dao.setDatabase(new HashMap<Long, Group>());
		for(int i = 0; i < 10; i++) {
			Group group = new Group();
			HashMap<Long, User> map = new HashMap<Long, User>();
			map.put((long) i, new User());
			group.setRoles(new HashSet<Role>());
			dao.add(group);
		}
	}
	
	@Test
	public void testUpdate() {
		Group group = new Group();
		
		group.setRoles(new HashSet<Role>());
		
		dao.update(1L, group);
		
		Group updatedGroup = dao.getById(1L);

		assertEquals(updatedGroup.getRoles(), group.getRoles());
	}
	
	@Test
	public void addTest() {
		Group group = new Group();

		HashMap<Long, User> map = new HashMap<Long, User>();
		map.put((long) 0L, new User());
		group.setRoles(new HashSet<Role>());
		
		dao.add(group);
		
		Group addedGroup = dao.getById(11L);

		assertEquals(addedGroup.getRoles(), group.getRoles());
	}
	
	
	@Test
	public void deleteTest() {
		dao.delete(1L);
		
		Group deletedGroup = dao.getById(1L);

        assertNull(deletedGroup);
	}
	
	public void getByIdTest() {
		Group group = dao.getById(1L);
		
		assertNotNull(group);
	}



}
