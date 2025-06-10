package it.nextre.corsojava.service.roleService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.RoleService;
import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.exception.RoleMissingException;
import it.nextre.corsojava.service.UserService.UserServiceJdbc;
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
	public void create(RoleDTO roleDTO) {
        LOGGER.info("Creazione in corso per il ruolo: " + roleDTO.getId());
       
        
        Role toSave = new Role(roleDTO);
        toSave.setDataCreazione(Instant.now());
        roleDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

	@Override
	public void update(RoleDTO roleDTO) {
        LOGGER.info("Modifica in corso per il ruolo: " + roleDTO.getId());
       
        Role role = new Role(roleDTO);

        roleDAO.update(roleDTO.getId(), role);
        LOGGER.info("Modifica effettuata con successo per il ruolo: " + roleDTO.getId());
    }

	@Override
	public void delete(RoleDTO roleDTO) {
        LOGGER.info("Cancellazione in corso per il ruolo: " + roleDTO.getId());
       
        Role r = roleDAO.getById(roleDTO.getId());
        if (r == null) {
            LOGGER.warn("Tentativo di cancellazione di un ruolo non valido");
            throw new RoleMissingException("Ruolo richiesto da cancellare non presente");
        }
        roleDAO.delete(roleDTO.getId());
        LOGGER.info("Cancellazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

	@Override
	public Optional<RoleDTO> findById(Long id) {
				LOGGER.info("Recupero ruolo con id: " + id);
		
		Role role = roleDAO.getById(id);
		if (role == null) {
			LOGGER.warn("Tentativo di recupero di un ruolo non valido");
			throw new RoleMissingException("Ruolo richiesto non presente");
		}
		
		RoleDTO roleDTO = entityConverter.fromEntity(role);
		LOGGER.info("Recupero ruolo con id: " + id + " effettuato con successo");
		return Optional.of(roleDTO);
	}

	@Override
	public List<RoleDTO> getAllRoles() {
        LOGGER.info("Recupero lista ruoli in corso");
      
        LOGGER.info("Fine recupero lista ruoli");
        return roleDAO.getAll().stream().map(role -> entityConverter.fromEntity(role)).toList();
    }

	@Override
	public PagedResult<RoleDTO> getAllRolesPag(int page, int size) {
    	LOGGER.info("Recupero lista ruoli in corso");
    	
    	LOGGER.info("Fine recupero lista ruoli");
    	PagedResult<Role> allGroupsPaged = roleDAO.getAllPag(page,size);
		PagedResult<RoleDTO> copy = PagedResult.copy(allGroupsPaged);
		copy.setContent(allGroupsPaged.getContent().stream().map(role -> entityConverter.fromEntity(role)).toList());
		return copy;
    }

}
