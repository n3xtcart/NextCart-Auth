package it.nextre.corsojava.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.nextre.corsojava.dao.TokenUserDAO;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;

public class TokenDAOTest {
	private TokenUserDAO dao;
	
	@BeforeEach
	public void setUp() {
		// Initialize the database or any required setup before each test
		this.dao = new TokenUserDAO();
		for(int i = 0; i < 10; i++) {
			Token token = new Token();
			token.setId((long) i);
			token.setToken("token" + i);
			token.setUser(new User());
			dao.add(token);
		}
	}
	
	@Test
	public void testUpdate() {
		Token token = new Token();
		token.setToken("testToken");
		token.setId(1L);
		token.setUser(new User());
		
		dao.update(1L, token);
		
		Token updatedToken = dao.getById(1L);
		
		assertEquals("testToken", updatedToken.getToken());
		assertNotNull(updatedToken.getUser());
	}
	
	@Test
	public void addTest() {
		Token token = new Token();
		token.setToken("testToken");
		token.setId(11L);
		token.setUser(new User());
		
		dao.add(token);
		
		Token addedToken = dao.getById(11L);
		
		assertEquals("testToken", addedToken.getToken());
		assertNotNull(addedToken.getUser());
	}
	
	
	@Test
	public void deleteTest() {
		dao.delete(1L);
		
		Token deletedToken = dao.getById(1L);
		
		assertEquals(null, deletedToken);
	}
	
	public void getByIdTest() {
		Token token = dao.getById(1L);
		
		assertNotNull(token);
		assertEquals("token0", token.getToken());
	}

}
