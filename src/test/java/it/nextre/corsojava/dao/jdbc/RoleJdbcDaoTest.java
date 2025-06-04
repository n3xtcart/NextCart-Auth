package it.nextre.corsojava.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;

public class RoleJdbcDaoTest {
	
	@Test
	public void testSaveRole() {
		RoleJdbcDao dao = RoleJdbcDao.getInstance();
		Role role = new Role();
		role.setId(1L);
		role.setAdmin(true);
		role.setDescrizione("dfvdffdv");
		role.setPriority(3L);
		Group group=new Group();
		group.setId(1L);
		Long long1 = dao.add(role);
		Role byId = dao.getById(long1);
		assertEquals(long1, byId.getId());
		assertEquals(role.isAdmin(), byId.isAdmin());
		assertEquals(role.getDescrizione(), byId.getDescrizione());
		dao.delete(long1);
	}
	
	
	@Test
	public void testUpdateRole() {
		RoleJdbcDao dao = RoleJdbcDao.getInstance();
		Role role = new Role();
		role.setId(1L);
		role.setAdmin(true);
		role.setDescrizione("dfvdffdv");
		role.setPriority(3L);
		Group group=new Group();
		group.setId(1L);
		Long long1 = dao.add(role);
		role.setDescrizione("novaDescrizione");
		dao.update(long1,role);
		Role byId = dao.getById(long1);
		dao.delete(long1);
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
		Group group=new Group();
		group.setId(1L);
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

		Group group=new Group();
		group.setId(1L);
		
		int size = dao.getAll().size();
		Long long1 = dao.add(role);
		int size2 = dao.getAll().size();
		dao.delete(long1);
		assertEquals(size+1, size2);
		
		
	}
		
	

}
