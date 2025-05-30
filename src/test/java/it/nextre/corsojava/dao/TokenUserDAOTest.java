package it.nextre.corsojava.dao;

import it.nextre.corsojava.dao.memory.TokenUserDAO;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TokenUserDAOTest {

    private TokenUserDAO sut;

    @BeforeEach
    void setUp() {
        sut = TokenUserDAO.getIstance();
        sut.setDatabase(new HashMap<Long, Token>());
        for (int i = 0; i < 5; i++) {
            User a = new User();
            a.setNome("nome " + i);
            a.setCognome("cognome " + i);
            a.setEmail("a" + i + "@example.com");
            Token t = new Token();
            t.setValue("token" + i);
            t.setUser(a);
            sut.add(t);
        }
    }

    @Test
    void add() {
        Token t = new Token();
        t.setValue("newToken");
        sut.add(t);
        assertEquals("newToken", sut.getById(6L).getValue());
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
        try {
            Instant old = sut.getById(3L).getUltimaModifica();
            Thread.sleep(1L);
            Token t = new Token();
            t.setValue("newToken");
            t.setUser(new User());
            sut.update(3L, t);
            assertEquals("newToken", sut.getById(3L).getValue());
            assertNotNull(sut.getById(3L).getUser());
            assertNotEquals(old, sut.getById(3L).getUltimaModifica());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}