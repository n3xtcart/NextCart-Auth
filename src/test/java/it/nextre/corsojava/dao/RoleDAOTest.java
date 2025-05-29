package it.nextre.corsojava.dao;

import it.nextre.corsojava.dao.memory.RoleDAO;
import it.nextre.corsojava.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;

class RoleDAOTest {

    private RoleDAO sut;

    @BeforeEach
    void setUp() {
        sut = RoleDAO.getIstance();
        sut.setDatabase(new HashMap<Long, Role>());
        String[] roles = {"user", "admin", "user", "user", "user"};
        for (int i = 0; i < 5; i++) {
            Role r = new Role();
            r.setDescrizione(roles[i]);
            r.setPriority((long) i);
            sut.add(r);
        }
    }

    @Test
    void add() {
        Role newRole = new Role();
        newRole.setDescrizione("super admin");
        newRole.setPriority(5L);
        sut.add(newRole);
        assertEquals("super admin", sut.getById(6L).getDescrizione());
    }

    @Test
    void delete() {
        sut.delete(2L);
        assertNull(sut.getById(2L));
    }

    @Test
    void getById() {
        assertEquals(2, sut.getById(3L).getPriority());
    }

    @Test
    void getAll() {
        assertEquals(5, sut.getAll().size());
        sut.delete(2L);
        assertEquals(4, sut.getAll().size());
    }

    @Test
    void update() {
        Role newRole = new Role();
        newRole.setPriority(15L);
        sut.update(2L, newRole);
        newRole = sut.getById(2L);
        newRole.setDescrizione("admin");
        assertEquals("admin", newRole.getDescrizione());
        assertEquals(15L, newRole.getPriority());
    }
}