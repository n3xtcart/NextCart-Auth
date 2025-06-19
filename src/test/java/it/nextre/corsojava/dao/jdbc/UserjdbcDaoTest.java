package it.nextre.corsojava.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;
import jakarta.inject.Inject;

@QuarkusTest
class UserjdbcDaoTest {
	@Inject
	UserJdbcDao dao ;
	
	
	@Test
	void testPag() {
		PagedResult<User> allPag = dao.getAllPag(1, 10);
		System.out.println(allPag);
	}
	
	
	
	@Test
	 void testRelazioni() {
		User byId = dao.getById(1L);
		System.out.println(byId);
	}
	
	
	
	
	@Test
	void testSaveUser() {
		User user = new User();
		user.setId(1L);
		user.setCognome("scarfone");
		user.setNome("salvatore");
		user.setEmail("bo");
		user.setPassword("lalalal");
		user.setActive(true);
		Group group = new Group();
		
		group.setId(500L);
		user.setGroup(group);
		Set<Role> hashSet = new HashSet<Role>();
		Role role1 = new Role();
		role1.setId(100L);
		hashSet.add(role1);
		group.setRoles(hashSet);
		Role role=new Role();
		role.setId(1L);
		Set< Role> set=new HashSet<Role>();
		set.add(role);
		user.setRoles(set);
		Long long1 = dao.add(user);
		User byId = dao.getById(long1);
		dao.delete(long1);
		assertEquals(long1, byId.getId());
		assertEquals(user.getCognome(), byId.getCognome());
		assertEquals(user.getNome(), byId.getNome());
		assertEquals(user.getEmail(), byId.getEmail());
		assertEquals(user.getPassword(), byId.getPassword());
	}
	
	
	@Test
	void testUpdateUser() {
		User user = new User();
		user.setId(1L);
		user.setCognome("Scarfone");
		user.setNome("Salvatore");
		user.setEmail("bo");
		user.setPassword("lalalal");

		Role role=new Role();
		role.setId(1L);
		Set< Role> set=new HashSet<Role>();
		set.add(role);
		user.setRoles(set);
		user.setActive(true);
		Group group = new Group();
		group.setId(3L);
		user.setGroup(group);
		Long long1 = dao.add(user);
		user.setCognome("nuovo");
		user.setEmail("nuovo");
		user.setNome("nuovo");
		dao.update(long1,user);
		User byId = dao.getById(long1);
		dao.delete(long1);
		assertEquals(user.getCognome(), byId.getCognome());
		assertEquals(user.getNome(), byId.getNome());
		assertEquals(user.getEmail(), byId.getEmail());
		
		
		}
	
	
	@Test
	void testDeleteUser() {
		User user = new User();
		user.setId(1L);
		user.setCognome("Scarfone");
		user.setNome("Salvatore");
		user.setEmail("bo");
		user.setPassword("lalalal");
		user.setActive(true);
		Role role=new Role();
		role.setId(1L);
		Set< Role> set=new HashSet<Role>();
		set.add(role);
		user.setRoles(set);
		Group group = new Group();
		group.setId(3L);
		user.setGroup(group);
		Long long1 = dao.add(user);
		dao.delete(long1);
		User byId = dao.getById(long1);
		assertEquals(null, byId);
		
	}
	
	@Test
	void testGetAllUser() {
		User user = new User();
		user.setId(1L);
		user.setCognome("Scarfone");
		user.setNome("Salvatore");
		user.setEmail("bo");
		user.setPassword("lalalal");
		Role role=new Role();
		role.setId(1L);
		Set< Role> set=new HashSet<Role>();
		set.add(role);
		user.setRoles(set);
		user.setActive(true);
		Group group = new Group();
		group.setId(3L);
		user.setGroup(group);
		int size = dao.getAll().size();
		Optional<User> byEmailPassword = dao.findByEmailPassword("nuovaemail@example.com", "NuovaPassword");
		System.out.println(byEmailPassword.get());
		Long long1 = dao.add(user);
		int size2 = dao.getAll().size();
		dao.delete(long1);
		assertEquals(size+1, size2);
		
		
	}
		
	

}
