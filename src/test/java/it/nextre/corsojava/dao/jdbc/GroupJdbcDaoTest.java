package it.nextre.corsojava.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;

public class GroupJdbcDaoTest {
	
	@Test
	public void testSaveGroup() {
		GroupJdbcDao dao = GroupJdbcDao.getInstance();
		Group group = new Group();
		group.setId(1L);
		Role role = new Role();
		role.setId(1L);
		group.setRole(role);
		Long long1 = dao.add(group);
		Group byId = dao.getById(long1);
		assertEquals(long1, byId.getId());
	}
	
	
	@Test
	public void testUpdateGroup() {
		GroupJdbcDao dao = GroupJdbcDao.getInstance();
		Group group = new Group();
		group.setId(1L);
		Role role = new Role();
		role.setId(1L);
		group.setRole(role);
		dao.update(2L,group);
		Group byId = dao.getById(3L);
		assertNotEquals(group.getId(), byId.getId());
		
		
		}
	
	
	@Test
	public void testDeleteGroup() {
		GroupJdbcDao dao = GroupJdbcDao.getInstance();
		Group group = new Group();
		group.setId(1L);
		Role role = new Role();
		role.setId(1L);
		group.setRole(role);
		Long long1 = dao.add(group);
		dao.delete(long1);
		Group byId = dao.getById(long1);
		assertEquals(null, byId);
		
	}
	
	@Test
	public void testGetAllGroup() {
		GroupJdbcDao dao = GroupJdbcDao.getInstance();
		Group group = new Group();
		group.setId(1L);
		Role role = new Role();
		role.setId(1L);
		group.setRole(role);
		int size = dao.getAll().size();
		Long long1 = dao.add(group);
		int size2 = dao.getAll().size();
		assertEquals(size+1, size2);
		
		
	}
		
	

}
