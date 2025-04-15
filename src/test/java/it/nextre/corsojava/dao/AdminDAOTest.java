package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Admin;
import it.nextre.corsojava.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AdminDAOTest {

    private AdminDAO sut;

    @BeforeEach
    void setUp() {
        sut = new AdminDAO();
        for (int i = 0; i < 5; i++) {
            Role r = new Role();
            r.setRole("admin");
            r.setPriority((long) (i + 1));
            Admin a = new Admin();
            a.setNome("admin" + i);
            a.setCognome("adminCognome" + i);
            a.setEmail("admin" + i + "@example.com");
            a.setRole(r);
            a.setPassword("password" + i);
            sut.add(a);
        }
    }

    @Test
    void add() {
        Admin a = new Admin();
        a.setNome("newAdminName");
        a.setCognome("newAdminCognome");
        a.setPassword("password7");
        sut.add(a);
        assertEquals("newAdminName", sut.getById(6L).getNome());
    }

    @Test
    void delete() {
        sut.delete(4L);
        assertNull(sut.getById(4L));
    }

    @Test
    void getById() {
        assertEquals("password3", sut.getById(4L).getPassword());
    }

    @Test
    void getAll() {
        assertEquals(5, sut.getAll().size());
        sut.delete(4L);
        assertEquals(4, sut.getAll().size());
    }

    @Test
    void update() {
        Admin a = new Admin();
        Role r = new Role();
        r.setPriority(50L);
        r.setRole("super admin");
        a.setRole(r);
        sut.update(3L, a);
        assertEquals(50L, sut.getById(3L).getRole().getPriority());
        assertEquals("super admin", sut.getById(3L).getRole().getRole());
    }
}