package it.nextre.corsojava.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.SQLException;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.QuarkusTest;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import jakarta.inject.Inject;
@QuarkusTest
public class GroupJdbcDaoTest {
	@Inject
	GroupJdbcDao dao ;
//	@Inject
//	AgroalDataSource dataSource;
	@Test
	public void testSaveGroup() throws SQLException {
//		System.out.println(dataSource+" ");
//		System.out.println(dataSource.getConnection());
//		
//		GroupJdbcDao dao = new GroupJdbcDao(dataSource);
		Group group = new Group();
		group.setId(1L);
		Role role = new Role();
		role.setId(1L);
		HashSet<Role> hashSet = new HashSet<Role>();
		hashSet.add(role);
		group.setRoles(hashSet);
		Long long1 = dao.add(group);
		Group byId = dao.getById(long1);
		assertEquals(long1, byId.getId());
		dao.delete(long1);
	}
	
	
	@Test
	public void testUpdateGroup() {
		Group group = new Group();
		group.setId(1L);
		Role role = new Role();
		role.setId(1L);
		group.setRoles(new HashSet<Role>());
		Long long1 = dao.add(group);
		Group groupNew = new Group();
		groupNew.setId(long1);
		Role roleNew = new Role();
		roleNew.setId(2L);
		HashSet<Role> hashSet = new HashSet<Role>();
		hashSet.add(roleNew);
		groupNew.setRoles(hashSet);
		dao.update(long1,groupNew);
		Group byId = dao.getById(long1);
		assertNotEquals(group.getUltimaModifica(), byId.getUltimaModifica());
		dao.delete(long1);
		
		
		}
	
	
	@Test
	public void testDeleteGroup() {
		Group group = new Group();
		group.setId(1L);
		Role role = new Role();
		role.setId(1L);
		HashSet<Role> hashSet = new HashSet<Role>();
		hashSet.add(role);
		group.setRoles(hashSet);
		Long long1 = dao.add(group);
		dao.delete(long1);
		Group byId = dao.getById(long1);
		assertEquals(null, byId);
		
	}
	
	
	@Test
	public void testRelazioni() {
		Group byId =dao.getById(1L);
		System.out.println(byId);
	}
	
	@Test
	public void testGetAllGroup() {
		Group group = new Group();
		group.setId(1L);
		Role role = new Role();
		role.setId(1L);
		group.setRoles(new HashSet<Role>());
		int size = dao.getAll().size();
		Long long1 = dao.add(group);
		int size2 = dao.getAll().size();
		dao.delete(long1);
		assertEquals(size+1, size2);
		
		
	}
		
	

}
