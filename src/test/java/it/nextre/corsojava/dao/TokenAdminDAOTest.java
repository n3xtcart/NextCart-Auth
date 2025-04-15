package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.entity.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenUserDAOTest {

    private TokenUserDAO sut;

    @BeforeEach
    void setUp() {
        sut = new TokenUserDAO();
        for (int i = 0; i < 5; i++) {
            User a = new User();
            a.setId(i);
            a.setNome("nome " + i);
            a.setCognome("cognome " + i);
            a.setEmail("a" + i + "@example.com");
            Token t = new Token();
            t.setToken("token" + i);
            t.setUser(a);
            sut.add(t);
        }
    }

    @Test
    void add() {
        Token t = new Token();
        t.setToken("newToken");
        sut.add(t);
        assertEquals("newToken", sut.getById(6L).getToken());
    }

    @Test
    void delete() {
        sut.delete(3L);
        assertNull(sut.getById(3L));
    }

    @Test
    void getById() {
        assertEquals("cognome 1", sut.getById(2L).getUser().getCognome());
    }

    @Test
    void getAll() {
        assertEquals(5, sut.getAll().size());
    }

    @Test
    void update() {
        Token t = new Token();
        t.setToken("newToken");
        t.setUser(new User());
        sut.update(3L, t);
        assertEquals("newToken", sut.getById(3L).getToken());
        assertNotNull(sut.getById(3L).getUser());
    }
}