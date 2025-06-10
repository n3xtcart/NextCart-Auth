package it.nextre.corsojava.dao.jdbc;

import java.util.Optional;

import io.agroal.api.AgroalDataSource;
import it.nextre.corsojava.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserJdbcDao extends JdbcDao<User> {



	@Inject
	public  UserJdbcDao(AgroalDataSource dataSource) {
		super(User.class, "user",dataSource);
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
