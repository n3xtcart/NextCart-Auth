package it.nextre.corsojava.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RoleTest {
	
	@Test
	public void testRole() {
		Role role = new Role();
		role.setDescrizione("admin");
		role.setPriority(1L);
		role.setAdmin(true);
		
		Role role3 = new Role();
		role3.setDescrizione("admin");
		role3.setPriority(2L);
		role3.setAdmin(true);

		Role role2 = new Role();
		role2.setDescrizione("admin");
		role2.setPriority(1L);
		role2.setAdmin(false);
		Role role4 = new Role();
		role4.setDescrizione("admin");
		role4.setPriority(2L);
		role4.setAdmin(false);
		
		assertEquals(1, role.compareTo(role2));
		assertEquals(-1, role.compareTo(role3));
		assertEquals(-1, role2.compareTo(role4));
		//assertEquals(1, role2.compareTo(role3));
	}

}
