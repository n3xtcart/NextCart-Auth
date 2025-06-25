package it.nextre.corsojava.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import io.agroal.api.AgroalDataSource;
import it.nextre.corsojava.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserJdbcDao extends JdbcDao<User> {



	@Inject
	public  UserJdbcDao(AgroalDataSource dataSource) {
		super(User.class, "user",dataSource,"CREATE TABLE IF NOT EXISTS `user` ("
				  + "  `id` int NOT NULL AUTO_INCREMENT,"
				  + "  `nome` varchar(45) DEFAULT NULL,"
				  + "  `cognome` varchar(45) DEFAULT NULL,"
				  + "  `password` varchar(45) DEFAULT NULL,"
				  + "  `groupId` varchar(45) DEFAULT NULL,"
				  + "  `email` varchar(45) DEFAULT NULL,"
				  + "  `ultimaModifica` timestamp(2) NULL DEFAULT NULL,"
				  + "  `active` tinyint(1) DEFAULT '0',"
				  + "  `roleId` int DEFAULT NULL,"
				  + "  `dataCreazione` timestamp(1) NULL DEFAULT NULL,"
				  + "  `creationUser` varchar(45) DEFAULT NULL,"
				  + "  PRIMARY KEY (`id`)"
				  + ") ENGINE=InnoDB AUTO_INCREMENT=1601 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; "

				  );
	
	
	

		PreparedStatement statement = null;
		ResultSet rSet = null;
		try (Connection connection = dataSource.getConnection()) {
			
				LOGGER.info("controllo e creazione tabella "+tableName);
				statement=connection.prepareStatement("CREATE TABLE IF NOT EXISTS `user_role_mapping` ("
						  + "  `userId` int NOT NULL,"
						  + "  `roleId` int NOT NULL,"
						  + "  PRIMARY KEY (`userId`,`roleId`)"
					
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
	}
	
	
	
	public UserJdbcDao() {
		super(User.class, "user", null);
	}
	
	public Optional<User> findByEmailPassword(String email, String password) {
		String[] colStrings={"email","password"};
		String[] val={email,password};
		User by_ = findBy_(colStrings, val);
		return by_ != null ? Optional.of(by_) : Optional.empty();
	}

	

	public User getByEmail(String email) {
		String[] colStrings={"email"};
		String[] val={email};
	return findBy_(colStrings, val);
	}

	

}
