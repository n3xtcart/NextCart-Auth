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
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.GroupMissingException;
import it.nextre.corsojava.service.UserService.UserServiceJdbc;
import it.nextre.corsojava.utils.EntityConverter;
import it.nextre.corsojava.utils.JwtGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
@LookupIfProperty(name = "source.Mem", stringValue = "db")
public class UserAdminServiceJdbc extends UserServiceJdbc implements UserAdminService{
	
	 public UserAdminServiceJdbc() {
		 super(null, null, null,null); 
	   }
	
	@Inject
	public UserAdminServiceJdbc(EntityConverter entityConverter, UserJdbcDao userDAO, TokenJdbcDao tokenUserDAO,JwtGenerator jwtGenerator) {
		super(entityConverter, userDAO, tokenUserDAO, jwtGenerator);
	}

	private static final Logger LOGGER = Logger.getLogger(UserAdminServiceJdbc.class);
    
  


	
	


	

	@Override
	public void createUser(UserDTO user,UserDTO userAction) {
        LOGGER.info("Creazione in corso per l'utente: " + user.getEmail());
        Optional<Role> maxUa = userAction.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        
        
        Optional<Role> maxNu =user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        if((!maxNu.isEmpty() && maxUa.isEmpty()) || (maxUa.isPresent() &&  maxNu.get().compareTo(maxUa.get()) > 0)) {
 			LOGGER.warn("Tentativo di creazione di un utente dandogli privilegi superiori a quelli dell'utente");
 			throw new GroupMissingException("Tentativo di creazione di un utente dandogli privilegi superiori a quelli dell'utente");
       	
       }
        
        User toSave = new User(user);
        toSave.setActive(true);

        
        toSave.setDataCreazione(Instant.now());
        userDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per l'utente: " + user.getEmail());
    }
	
    @Override
    public void update(UserDTO user,UserDTO userAction) {
       User u = userDAO.getById(user.getId());
       Optional<Role> maxU = u.getRoles().stream().reduce((a,b)->a.compareTo(b) >0?a:b);
       Optional<Role> maxUa = userAction.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
       
       if((!maxU.isEmpty() && maxUa.isEmpty()) || (maxUa.isPresent() &&  maxU.get().compareTo(maxUa.get()) > 0)) {
			LOGGER.warn("Tentativo di modifica su un utente con privilegi superiori a quelli dell'utente");
			throw new GroupMissingException("Impossibile modificare un utente con privilegi superiori a quelli dell'utente");
       	
       }
       Optional<Role> maxNu =user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
       if((!maxNu.isEmpty() && maxUa.isEmpty()) || (maxUa.isPresent() &&  maxNu.get().compareTo(maxUa.get()) > 0)) {
			LOGGER.warn("Tentativo di modifica un utente dandogli privilegi superiori a quelli dell'utente");
			throw new GroupMissingException("Impossibile modificare un utente dandogli privilegi superiori a quelli dell'utente");
      	
      }

        LOGGER.info("Modifica in corso per l'utente: " + user.getEmail());
        
        
        u.setNome(user.getNome());
        u.setCognome(user.getCognome());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        userDAO.update(user.getId(), u);
        LOGGER.info("Modifica effettuata con successo per l'utente: " + user.getEmail());
    }

	@Override
	public List<UserDTO> getAllUsers(UserDTO userAction) {
       
        LOGGER.info("Recupero lista utenti in corso");
        return userDAO.getAll().stream().filter(user->{
        	  Optional<Role> maxU = user.getRoles().stream().reduce((a,b)->a.compareTo(b) >0?a:b);
              Optional<Role> maxUa = userAction.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
              
              return!((!maxU.isEmpty() && maxUa.isEmpty()) || (maxU.isPresent() &&  maxU.get().compareTo(maxUa.get()) >= 0)) ;
        }).map(entityConverter:: fromEntity).toList();
    }

	@Override
	public Optional<UserDTO> findById(Long id,UserDTO userAction) {
		return Optional.of(entityConverter.fromEntity( userDAO.getById(id)));
	}

	@Override
	public PagedResult<UserDTO> getAllUsersPag(int page, int size,UserDTO userAction) {
    	
    	LOGGER.info("Recupero lista utenti in corso");
		PagedResult<User> allUsersPaged = userDAO.getAllPag(page,size);
		PagedResult<UserDTO> copy = PagedResult.copy(allUsersPaged);
		copy.setContent(allUsersPaged.getContent().stream().filter(user->{
      	  Optional<Role> maxU = user.getRoles().stream().reduce((a,b)->a.compareTo(b) >0?a:b);
          Optional<Role> maxUa = userAction.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >=0?a:b);
          
          return!((!maxU.isEmpty() && maxUa.isEmpty()) || (maxU.isPresent() &&  maxU.get().compareTo(maxUa.get()) >= 0)) ;
    }).map(entityConverter:: fromEntity).toList());
		return copy;
    }

}
