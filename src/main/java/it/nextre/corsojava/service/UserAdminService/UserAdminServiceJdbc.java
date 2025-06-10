package it.nextre.corsojava.service.UserAdminService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.UserAdminService;
import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.service.UserService.UserServiceJdbc;
import it.nextre.corsojava.utils.EntityConverter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
@LookupIfProperty(name = "source.Mem", stringValue = "db")
public class UserAdminServiceJdbc extends UserServiceJdbc implements UserAdminService{
	
	 public UserAdminServiceJdbc() {
		 super(null, null, null); 
	   }
	
	@Inject
	public UserAdminServiceJdbc(EntityConverter entityConverter, UserJdbcDao userDAO, TokenJdbcDao tokenUserDAO) {
		super(entityConverter, userDAO, tokenUserDAO);
	}

	private static final Logger LOGGER = Logger.getLogger(UserAdminServiceJdbc.class);
    
  


	
	


	

	@Override
	public void createUser(UserDTO user) {
        LOGGER.info("Creazione in corso per l'utente: " + user.getEmail());
        
        User toSave = new User(user);
        toSave.setActive(true);

        
        toSave.setDataCreazione(Instant.now());
        userDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per l'utente: " + user.getEmail());
    }

	@Override
	public List<UserDTO> getAllUsers() {
       
        LOGGER.info("Recupero lista utenti in corso");
        return userDAO.getAll().stream().filter(a -> a.getActive()).map(user -> 
        	entityConverter.fromEntity(user)
        ).toList();
    }

	@Override
	public Optional<UserDTO> findById(Long id) {
		return Optional.of(entityConverter.fromEntity( userDAO.getById(id)));
	}

	@Override
	public PagedResult<UserDTO> getAllUsersPag(int page, int size) {
    	
    	LOGGER.info("Recupero lista utenti in corso");
		PagedResult<User> allUsersPaged = userDAO.getAllPag(page,size);
		PagedResult<UserDTO> copy = PagedResult.copy(allUsersPaged);
		copy.setContent(allUsersPaged.getContent().stream().map(user -> entityConverter.fromEntity(user)).toList());
		return copy;
    }

}
