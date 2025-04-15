package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenDAOTest {
    private TokenUserDAO dao;

    @BeforeEach
    void setUp() {
        // Initialize the database or any required setup before each test
        this.dao = new TokenUserDAO();
        for (long i = 0; i < 10; i++) {
            Token token = new Token();
            token.setId(i);
            token.setToken("token" + i);
            token.setUser(new User());
            dao.add(token);
        }
    }

    @Test
    void testUpdate() {
        Token token = new Token();
        token.setToken("testToken");
        token.setId(1L);
        token.setUser(new User());

        dao.update(1L, token);

        Token updatedToken = dao.getById(1L);

        Assertions.assertEquals("testToken", updatedToken.getToken());
        Assertions.assertNotNull(updatedToken.getUser());
    }

    @Test
    void addTest() {
        Token token = new Token();
        token.setToken("testToken");
        token.setUser(new User());

        dao.add(token);

        Token addedToken = dao.getById(11L);

        Assertions.assertEquals("testToken", addedToken.getToken());
        Assertions.assertNotNull(addedToken.getUser());
    }


    @Test
    void deleteTest() {
        dao.delete(1L);

        Token deletedToken = dao.getById(1L);

        Assertions.assertNull(deletedToken);
    }

    @Test
    void getByIdTest() {
        Token token = dao.getById(1L);

        Assertions.assertNotNull(token);
        Assertions.assertEquals("token0", token.getToken());
    }

}
