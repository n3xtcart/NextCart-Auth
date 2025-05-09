package it.nextre.corsojava.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;

public class RoleJdbcDaoTest {
	
	@Test
	public void testSaveRole() {
		RoleJdbcDao dao = RoleJdbcDao.getInstance();
		Role role = new Role();
		role.setId(1L);
		role.setAdmin(true);
		role.setDescrizione("dfvdffdv");
		role.setPriority(3L);
		Long long1 = dao.add(role);
		Role byId = dao.getById(long1);
		assertEquals(long1, byId.getId());
		assertEquals(role.isAdmin(), byId.isAdmin());
		assertEquals(role.getDescrizione(), byId.getDescrizione());
	}
	
	
	@Test
	public void testUpdateRole() {
		RoleJdbcDao dao = RoleJdbcDao.getInstance();
		Role role = new Role();
		role.setId(1L);
		role.setAdmin(true);
		role.setDescrizione("dfvdffdv");
		role.setPriority(3L);
		dao.update(2L,role);
		Role byId = dao.getById(3L);
		assertNotEquals(role.getId(), byId.getId());
		assertEquals(role.isAdmin(), byId.isAdmin());
		assertEquals(role.getDescrizione(), byId.getDescrizione());
		
		
		}
	
	
	@Test
	public void testDeleteRole() {
		RoleJdbcDao dao = RoleJdbcDao.getInstance();
		Role role = new Role();
		role.setId(1L);
		role.setAdmin(true);
		role.setDescrizione("dfvdffdv");
		role.setPriority(3L);
		Long long1 = dao.add(role);
		dao.delete(long1);
		Role byId = dao.getById(long1);
		assertEquals(null, byId);
		
	}
	
	@Test
	public void testGetAllRole() {
		RoleJdbcDao dao = RoleJdbcDao.getInstance();
		Role role = new Role();
		role.setId(1L);
		role.setAdmin(true);
		role.setDescrizione("dfvdffdv");
		role.setPriority(3L);
	
		
		int size = dao.getAll().size();
		Long long1 = dao.add(role);
		int size2 = dao.getAll().size();
		assertEquals(size+1, size2);
		
		
	}
		
	

}
