package it.nextre.corsojava.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		super(Group.class, "groupT", dataSource,"CREATE TABLE IF NOT EXISTS `groupT` ("
				  + "  `id` int NOT NULL AUTO_INCREMENT,"
				  + "  `ultimaModifica` timestamp(2) NULL DEFAULT NULL,"
				  + "  `dataCreazione` timestamp(1) NULL DEFAULT NULL,"
				  + "  `creationUser` varchar(45) DEFAULT NULL,"
				  + "  `descrizione` varchar(45) DEFAULT NULL,"
				  + "  PRIMARY KEY (`id`),"
				  + "  UNIQUE KEY `id_UNIQUE` (`id`)"
				  + ") ENGINE=InnoDB AUTO_INCREMENT=1653 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;"

				  );
		
		
		PreparedStatement statement = null;
		ResultSet rSet = null;
		try (Connection connection = dataSource.getConnection()) {
			
				LOGGER.info("controllo e creazione tabella "+tableName);
				statement=connection.prepareStatement("CREATE TABLE IF NOT EXISTS `group_role_mapping` ("
						  + "  `groupId` varchar(45) NOT NULL,"
						  + "  `roleId` varchar(45) NOT NULL,"
						  + "  PRIMARY KEY (`groupId`,`roleId`)"
						  + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;");
				statement.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
				try {
					if(statement!=null)statement.close();
					if(rSet!=null)rSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
		LOGGER.info(roleJdbcDao);
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
