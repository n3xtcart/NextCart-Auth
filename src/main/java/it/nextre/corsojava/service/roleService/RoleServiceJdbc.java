package it.nextre.corsojava.service.roleService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.RoleService;
import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.exception.GroupMissingException;
import it.nextre.corsojava.exception.RoleMissingException;
import it.nextre.corsojava.utils.EntityConverter;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
@LookupIfProperty(name = "source.Mem", stringValue = "db")
public class RoleServiceJdbc implements RoleService{
	private static final Logger LOGGER = Logger.getLogger(RoleServiceJdbc.class);
private final RoleJdbcDao roleDAO ;
private final EntityConverter entityConverter;

public RoleServiceJdbc(EntityConverter entityConverter, RoleJdbcDao roleDAO) {
	this.entityConverter = entityConverter;
	this.roleDAO = roleDAO;
}

	@Override
	public void create(RoleDTO roleDTO,UserDTO user) {
        LOGGER.info("Creazione in corso per il ruolo: " + roleDTO.getId());
        Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        
        if( (  entityConverter.fromDTO(roleDTO).compareTo(maxU.get()) > 0)) {
			LOGGER.warn("Tentativo di creazione di un ruolo con privilegi superiori a quelli dell'utente");
			throw new GroupMissingException("Impossibile creare un ruolo con privilegi superiori a quelli dell'utente");
        	
        }
       
        
        Role toSave = new Role(roleDTO);
        toSave.setDataCreazione(Instant.now());
        roleDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

	@Override
	public void update(RoleDTO roleDTO,UserDTO user) {
        LOGGER.info("Modifica in corso per il ruolo: " + roleDTO.getId());
       
        Role role = new Role(roleDTO);

        roleDAO.update(roleDTO.getId(), role);
        LOGGER.info("Modifica effettuata con successo per il ruolo: " + roleDTO.getId());
    }

	@Override
	public void delete(RoleDTO roleDTO,UserDTO user) {
        LOGGER.info("Cancellazione in corso per il ruolo: " + roleDTO.getId());
       
        Role r = roleDAO.getById(roleDTO.getId());
        if (r == null) {
            LOGGER.warn("Tentativo di cancellazione di un ruolo non valido");
            throw new RoleMissingException("Ruolo richiesto da cancellare non presente");
        } Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        
        if( (  entityConverter.fromDTO(roleDTO).compareTo(maxU.get()) > 0)) {
			LOGGER.warn("Tentativo di eliminazione di un ruolo con privilegi superiori a quelli dell'utente");
			throw new GroupMissingException("Impossibile eliminare un ruolo con privilegi superiori a quelli dell'utente");
        	
        }
       
        roleDAO.delete(roleDTO.getId());
        LOGGER.info("Cancellazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

	@Override
	public Optional<RoleDTO> findById(Long id,UserDTO user) {
				LOGGER.info("Recupero ruolo con id: " + id);
		
		Role role = roleDAO.getById(id);
		if (role == null) {
			LOGGER.warn("Tentativo di recupero di un ruolo non valido");
			throw new RoleMissingException("Ruolo richiesto non presente");
		}
		 Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
	        
	        if( (  role.compareTo(maxU.get()) > 0)) {
				LOGGER.warn("Tentativo di creazione di un ruolo con privilegi superiori a quelli dell'utente");
				throw new GroupMissingException("Impossibile creare un ruolo con privilegi superiori a quelli dell'utente");
	        	
	        }
	       
		
		RoleDTO roleDTO = entityConverter.fromEntity(role);
		LOGGER.info("Recupero ruolo con id: " + id + " effettuato con successo");
		return Optional.of(roleDTO);
	}

	@Override
	public List<RoleDTO> getAllRoles(UserDTO user) {
        LOGGER.info("Recupero lista ruoli in corso");
      
        LOGGER.info("Fine recupero lista ruoli");
        return roleDAO.getAll().stream().filter(role->{
        	 Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >=0?a:b);
             
             return( (  role.compareTo(maxU.get()) >= 0)) ;
            
        }).map(entityConverter::fromEntity).toList();
        
    }

	@Override
	public PagedResult<RoleDTO> getAllRolesPag(int page, int size,UserDTO user) {
    	LOGGER.info("Recupero lista ruoli in corso");
    	
    	LOGGER.info("Fine recupero lista ruoli");
    	PagedResult<Role> allGroupsPaged = roleDAO.getAllPag(page,size);
		PagedResult<RoleDTO> copy = PagedResult.copy(allGroupsPaged);
		copy.setContent(allGroupsPaged.getContent().stream().filter(role->{
       	 Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >=0?a:b);
         
         return( (  role.compareTo(maxU.get()) >= 0)) ;
        
    }).map(entityConverter::fromEntity).toList());
		return copy;
    }

}
