package it.nextre.corsojava.dao.jdbc;

import java.time.Instant;

import io.agroal.api.AgroalDataSource;
import it.nextre.corsojava.entity.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RoleJdbcDao extends JdbcDao<Role> {

	@Inject
	public  RoleJdbcDao(AgroalDataSource dataSource) {
		super(Role.class, "role",dataSource);
		LOGGER.info("inizializzazione ruoli");
		Role admin = this.findByDescrizione("admin");
		Role user = this.findByDescrizione("user");
		if(admin==null) {
			Role role=new Role();
			role.setAdmin(true);
			role.setDescrizione("admin");
			role.setPriority(10L);
			role.setDataCreazione(Instant.now());
			this.add(role);
		}
		if(user==null) {
			Role role=new Role();
			role.setAdmin(false);
			role.setDescrizione("user");
			role.setPriority(1L);
			role.setDataCreazione(Instant.now());
			this.add(role);
		}
	}
	public RoleJdbcDao() {
		super(Role.class, "role", null);
	}
	
	public Role findByDescrizione(String value) {
		String[] field={"descrizione"};
		return findBy_(field, new String[] {value});
	}
	



}
