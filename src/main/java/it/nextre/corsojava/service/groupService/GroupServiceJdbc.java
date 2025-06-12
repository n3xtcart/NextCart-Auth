package it.nextre.corsojava.service.groupService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.GroupService;
import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.exception.GroupMissingException;
import it.nextre.corsojava.utils.EntityConverter;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
@LookupIfProperty(name = "source.Mem", stringValue = "db")
public class GroupServiceJdbc implements GroupService{
	private static final Logger LOGGER = Logger.getLogger(GroupServiceJdbc.class);

    private final GroupJdbcDao groupDAO ;

	private EntityConverter entityConverter;
	
    
    public GroupServiceJdbc(EntityConverter entityConverter, GroupJdbcDao groupDAO) {
		this.entityConverter = entityConverter;
		this.groupDAO = groupDAO;
	}
   
	@Override
	public void create(GroupDTO group,UserDTO user) {
        LOGGER.info("Creazione in corso per il gruppo: " + group.getId());
        Optional<Role> maxC = group.getRoleDTO().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        
        if((!maxC.isEmpty() && maxU.isEmpty()) ||  (maxU.isPresent() &&  maxC.get().compareTo(maxU.get()) > 0)) {
			LOGGER.warn("Tentativo di creazione di un gruppo con privilegi superiori a quelli dell'utente");
			throw new GroupMissingException("Impossibile creare un gruppo con privilegi superiori a quelli dell'utente");
        	
        }
		
        Group toSave = entityConverter.fromDTO(group);
        toSave.setDataCreazione(Instant.now());
        groupDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il gruppo: " + group.getId());
    }

	@Override
	public void update(GroupDTO group,UserDTO user) {
        LOGGER.info("Modifica in corso per il gruppo: " + group.getId());
      
        Group g = groupDAO.getById(group.getId());
        if (g == null) {
            LOGGER.warn("Tentativo di modifica di un gruppo non valido");
            throw new GroupMissingException("Impossibile modificare un gruppo non presente");
        }
        Optional<Role> maxC = group.getRoleDTO().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        
        if((!maxC.isEmpty() && maxU.isEmpty()) || (maxC.isPresent() &&  maxC.get().compareTo(maxU.get()) > 0)) {
			LOGGER.warn("Tentativo di creazione di un gruppo con privilegi superiori a quelli dell'utente");
			throw new GroupMissingException("Impossibile creare un gruppo con privilegi superiori a quelli dell'utente");
        	
        }
        Group group2 = new Group(group);
        g.setRoles(group2.getRoles());
        groupDAO.update(group.getId(), g);
        LOGGER.info("Modifica effettuata con successo per il gruppo: " + group.getId());
    }

	@Override
	public void delete(GroupDTO group,UserDTO user) {
        LOGGER.info("Cancellazione in corso per il gruppo: " + group.getId());
       
        if (groupDAO.getById(group.getId()) == null) {
            LOGGER.warn("Tentativo di cancellazione di un gruppo non valido");
            throw new GroupMissingException("Impossibile cancellare un gruppo non presente");
        }
        Optional<Role> maxC = group.getRoleDTO().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        
        if((!maxC.isEmpty() && maxU.isEmpty()) || (maxC.isPresent() &&  maxC.get().compareTo(maxU.get()) > 0)) {
			LOGGER.warn("Tentativo di creazione di un gruppo con privilegi superiori a quelli dell'utente");
			throw new GroupMissingException("Impossibile creare un gruppo con privilegi superiori a quelli dell'utente");
        	
        }
        groupDAO.delete(group.getId());
        LOGGER.info("Ca)ncellazione effettuata con successo per il gruppo: " + group.getId());
    }

	@Override
	public Optional<GroupDTO> findById(Long id,UserDTO user) {
		LOGGER.info("Ricerca in corso per il gruppo con ID: " + id);
		
		Group group = groupDAO.getById(id);
		if (group == null) {
			LOGGER.warn("Nessun gruppo trovato con ID: " + id);
			return Optional.empty();
		}
		Optional<Role> maxC = group.getRoles().stream().reduce((a,b)->a.compareTo(b) >0?a:b);
        Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        
        if((!maxC.isEmpty() && maxU.isEmpty()) || (maxC.isPresent() &&  maxC.get().compareTo(maxU.get()) > 0)) {
			LOGGER.warn("Tentativo di creazione di un gruppo con privilegi superiori a quelli dell'utente");
			throw new GroupMissingException("Impossibile creare un gruppo con privilegi superiori a quelli dell'utente");
        	
        }
		
		GroupDTO groupDTO = entityConverter.fromEntity(group);
		LOGGER.info("Gruppo trovato con successo: " + groupDTO.getId());
		return Optional.of(groupDTO);
	}

	@Override
	public List<GroupDTO> getAllGroups(UserDTO user) {
        LOGGER.info("Recupero lista gruppi in corso");
       
        LOGGER.info("Fine recupero lista gruppi");
        return groupDAO.getAll().stream().filter(group->{
        	Optional<Role> maxC = group.getRoles().stream().reduce((a,b)->a.compareTo(b) >0?a:b);
            Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
            return !((!maxC.isEmpty() && maxU.isEmpty()) || (maxC.isPresent() &&  maxC.get().compareTo(maxU.get()) >= 0));
        }).map(group -> entityConverter.fromEntity(group)).toList();
        
    }

	@Override
	public PagedResult<GroupDTO> getAllGroupsPag(int page, int size,UserDTO user) {
    	LOGGER.info("Recupero lista gruppi in corso");
    
    	LOGGER.info("Fine recupero lista gruppi");
    	PagedResult<Group> allGroupsPaged = groupDAO.getAllPag(page,size);
		PagedResult<GroupDTO> copy = PagedResult.copy(allGroupsPaged);
		copy.setContent(allGroupsPaged.getContent().stream().filter(group->{
        	Optional<Role> maxC = group.getRoles().stream().reduce((a,b)->a.compareTo(b) >0?a:b);
            Optional<Role> maxU = user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
            return !((!maxC.isEmpty() && maxU.isEmpty()) || (maxC.isPresent() &&  maxC.get().compareTo(maxU.get()) >= 0));
        }).map(group -> entityConverter.fromEntity(group)).toList());
		return copy;
    
    }

}
