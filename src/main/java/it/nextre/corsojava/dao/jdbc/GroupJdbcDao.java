package it.nextre.corsojava.dao.jdbc;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import io.agroal.api.AgroalDataSource;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
@ApplicationScoped
public class GroupJdbcDao extends JdbcDao<Group> {

	@Inject
	public GroupJdbcDao(AgroalDataSource dataSource,RoleJdbcDao roleJdbcDao) {
		super(Group.class, "groupT", dataSource);
		LOGGER.info("inizializzazione gruppi");
		Group admin = findByDescrizione("admin");
		Group user = findByDescrizione("user");
		if(admin==null) {
			Group group=new Group();
			group.setDescrizione("admin");
			group.setDataCreazione(Instant.now());
			Set<Role> hashSet = new HashSet<Role>();
			hashSet.add(roleJdbcDao.findByDescrizione("admin"));
			group.setRoles(hashSet);
			add(group);
		}
		if(user==null) {
			Group group=new Group();
			group.setDescrizione("user");
			group.setDataCreazione(Instant.now());
			Set<Role> hashSet = new HashSet<Role>();
			hashSet.add(roleJdbcDao.findByDescrizione("user"));
			group.setRoles(hashSet);
			add(group);
		}
	}
	
	
 public GroupJdbcDao() {
		super(Group.class, "groupT", null);
	}
 
 
 public Group findByDescrizione(String descrizione) {
	 return findBy_(new String[] {"descrizione"}, new String[] {descrizione});
 }
	

	
	
	

}
