package it.nextre.corsojava.service.groupService;

import java.util.List;
import java.util.Optional;

import org.jboss.logging.Logger;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.GroupService;
import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.exception.GroupMissingException;
import it.nextre.corsojava.utils.EntityConverter;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
@LookupIfProperty(name = "source.Mem", stringValue = "db")
public class GroupServiceJdbc implements GroupService{
	private static final Logger LOGGER = Logger.getLogger(GroupServiceJdbc.class);

    private final GroupJdbcDao groupDAO = GroupJdbcDao.getInstance();

	private EntityConverter entityConverter;
	
    
    public GroupServiceJdbc(EntityConverter entityConverter) {
		this.entityConverter = new EntityConverter();
	}

	@Override
	public void create(GroupDTO group) {
        LOGGER.info("Creazione in corso per il gruppo: " + group.getId());
      
        Group toSave = new Group(group);
        groupDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il gruppo: " + group.getId());
    }

	@Override
	public void update(GroupDTO group) {
        LOGGER.info("Modifica in corso per il gruppo: " + group.getId());
      
        Group g = groupDAO.getById(group.getId());
        if (g == null) {
            LOGGER.warn("Tentativo di modifica di un gruppo non valido");
            throw new GroupMissingException("Impossibile modificare un gruppo non presente");
        }
        Group group2 = new Group(group);
        g.setRoles(group2.getRoles());
        groupDAO.update(group.getId(), g);
        LOGGER.info("Modifica effettuata con successo per il gruppo: " + group.getId());
    }

	@Override
	public void delete(GroupDTO group) {
        LOGGER.info("Cancellazione in corso per il gruppo: " + group.getId());
       
        if (groupDAO.getById(group.getId()) == null) {
            LOGGER.warn("Tentativo di cancellazione di un gruppo non valido");
            throw new GroupMissingException("Impossibile cancellare un gruppo non presente");
        }
        groupDAO.delete(group.getId());
        LOGGER.info("Cancellazione effettuata con successo per il gruppo: " + group.getId());
    }

	@Override
	public Optional<GroupDTO> findById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<GroupDTO> getAllGroups() {
        LOGGER.info("Recupero lista gruppi in corso");
       
        LOGGER.info("Fine recupero lista gruppi");
        return groupDAO.getAll().stream().map(group -> entityConverter.fromEntity(group)).toList();
    }

	@Override
	public PagedResult<GroupDTO> getAllGroupsPag(int page, int size) {
    	LOGGER.info("Recupero lista gruppi in corso");
    
    	LOGGER.info("Fine recupero lista gruppi");
    	PagedResult<Group> allGroupsPaged = groupDAO.getAllPag(page,size);
		PagedResult<GroupDTO> copy = PagedResult.copy(allGroupsPaged);
		copy.setContent(allGroupsPaged.getContent().stream().map(group -> entityConverter.fromEntity(group)).toList());
		return copy;
    
    }

}
