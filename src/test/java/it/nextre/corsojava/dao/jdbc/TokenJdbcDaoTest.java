package it.nextre.corsojava.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import jakarta.inject.Inject;

@QuarkusTest
public class TokenJdbcDaoTest {
	@Inject
	TokenJdbcDao dao ;
	@Inject
	UserJdbcDao userDao;
	
	
	@Test
	public void testSaveToken() {
		Token token = new Token();
		token.setId(1L);
		User user = new User();
		user.setId(1L);
		Long long2 = userDao.add(user);
		user.setId(long2);
		token.setUser(user);
		token.setValue("ddvdvdfvdfv");
		Long long1 = dao.add(token);
		Token byId = dao.getById(long1);
		dao.delete(long1);
		userDao.delete(long2);
		assertEquals(long1, byId.getId());
		assertEquals(token.getUser().getId(), byId.getUser().getId());
		assertEquals(token.getValue(), byId.getValue());
	}
	
	
	@Test
	public void testUpdateToken() {
		Token token = new Token();
		token.setId(1L);
		User user = new User();
		user.setEmail("email");
		Long long2 = userDao.add(user);
		user.setId(long2);
		token.setUser(user);
		token.setValue("ddvdvdfvdfv");
		token.setDataScadenza(LocalDateTime.now().plusMinutes(10).toInstant(ZoneOffset.UTC));
		token.setValue("newVal");
		Long long1 = dao.add(token);
		dao.update(long1,token);
		Token byId = dao.getById(long1);
		dao.delete(long1);
		userDao.delete(long2);
		assertEquals(token.getId(), byId.getId());
		assertEquals(token.getUser().getId(), byId.getUser().getId());
		assertEquals(token.getValue(), byId.getValue());
		
		
		}
	
	
	@Test
	public void testDeleteToken() {
		Token token = new Token();
		token.setId(1L);
		User user = new User();
		user.setId(1L);
		token.setUser(user);
		token.setValue("ddvdvdfvdfv");
		Long long1 = dao.add(token);
		dao.delete(long1);
		Token byId = dao.getById(long1);
		assertEquals(null, byId);
		
	}
	
	@Test
	public void testGetAllToken() {
		Token token = new Token();
		token.setId(1L);
		User user = new User();
		user.setId(1L);
		token.setUser(user);
		token.setValue("ddvdvdfvdfv");
		int size = dao.getAll().size();
		Long long1 = dao.add(token);
		int size2 = dao.getAll().size();
		assertEquals(size+1, size2);
		
		
	}
		
	

}
