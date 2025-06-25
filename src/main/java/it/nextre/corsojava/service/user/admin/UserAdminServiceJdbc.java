package it.nextre.corsojava.service.user.admin;

import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.security.UnauthorizedException;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.UserAdminService;
import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.PriorityException;
import it.nextre.corsojava.exception.RoleMissingException;
import it.nextre.corsojava.exception.UserAlreadyExistsException;
import it.nextre.corsojava.exception.UserMissingException;
import it.nextre.corsojava.service.user.UserServiceJdbc;
import it.nextre.corsojava.utils.EntityConverter;
import it.nextre.corsojava.utils.JwtGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@ApplicationScoped
@LookupIfProperty(name = "source.Mem", stringValue = "db")
public class UserAdminServiceJdbc extends UserServiceJdbc implements UserAdminService {

    private static final Logger LOGGER = Logger.getLogger(UserAdminServiceJdbc.class);

    public UserAdminServiceJdbc() {
        super(null, null, null, null, null);
    }

    @Inject
    public UserAdminServiceJdbc(EntityConverter entityConverter,
                                UserJdbcDao userDAO,
                                TokenJdbcDao tokenUserDAO,
                                JwtGenerator jwtGenerator,
                                GroupJdbcDao groupJdbcDao) {
        super(entityConverter, userDAO, tokenUserDAO, jwtGenerator, groupJdbcDao);
    }

    @Override
    public void register(UserDTO user) {
        if (user == null) {
            throw new it.nextre.corsojava.exception.UnauthorizedException("Utente non valido");
        }
        LOGGER.info("Registrazione in corso per l'utente: " + user.getEmail());

        if (userDAO.getByEmail(user.getEmail()) != null) {
            LOGGER.warn("Utente già registrato con l'email: " + user.getEmail());
            throw new UnauthorizedException("Utente già registrato");
        }
        if (user.getGroupDTO() != null && user.getGroupDTO().getRoleDTO() != null
                && user.getGroupDTO().getRoleDTO().stream().anyMatch(RoleDTO::getAdmin)) {
            LOGGER.warn("tentativo di registrazione come admin");
            throw new it.nextre.corsojava.exception.UnauthorizedException("Non puoi registrarti come admin");
        }
        User user2 = new User();
        user2.setNome(user.getNome());
        user2.setCognome(user.getCognome());
        user2.setEmail(user.getEmail());
        user2.setPassword(user.getPassword());
        Group byDescrizione = groupJdbcDao.findByDescrizione("admin");
        user2.setGroup(byDescrizione);
        user2.setActive(false);
        user2.setDataCreazione(Instant.now());
        userDAO.add(user2);
        Token token = generateToken(userDAO.getByEmail(user2.getEmail()));
        tokenUserDAO.add(token);
        sendMail(user2, token);
        LOGGER.info("email inviata all'utente: " + user.getEmail());

    }


    @Override
    public void createUser(UserDTO user, UserDTO userAction) {
        if (user == null || userAction == null) throw new UserMissingException("user non può essere null");


        LOGGER.info("Creazione in corso per l'utente: " + user.getEmail());

        if (user.getGroupDTO() == null || user.getRuoli() == null || user.getGroupDTO().getRoleDTO() == null ||
                user.getRuoli().isEmpty() || user.getGroupDTO().getRoleDTO().isEmpty())
            throw new RoleMissingException("non puoi creare un utente senza ruoli o senza gruppo");
        Set<RoleDTO> ruoliUA = userAction.getRuoli();
        ruoliUA.addAll(userAction.getGroupDTO() != null ? userAction.getGroupDTO().getRoleDTO() : new HashSet<RoleDTO>());
        Optional<Role> maxUa = ruoliUA.stream().map(entityConverter::fromDTO).reduce((a, b) -> a.compareTo(b) > 0 ? a : b);
        Set<RoleDTO> ruoliNu = user.getRuoli();
        ruoliNu.addAll(user.getGroupDTO() != null ? user.getGroupDTO().getRoleDTO() : new HashSet<RoleDTO>());


        Optional<Role> maxNu = ruoliNu.stream().map(entityConverter::fromDTO).reduce((a, b) -> a.compareTo(b) > 0 ? a : b);
        if ((!maxNu.isEmpty() && maxUa.isEmpty()) || (maxUa.isPresent() && maxNu.isPresent() && maxNu.get().compareTo(maxUa.get()) > 0)) {
            LOGGER.warn("Tentativo di creazione di un utente dandogli privilegi superiori a quelli dell'utente");
            throw new PriorityException("Tentativo di creazione di un utente dandogli privilegi superiori a quelli dell'utente");

        }
        if (userDAO.getByEmail(user.getEmail()) != null)
            throw new UserAlreadyExistsException("esiste già un utente con questa mail");


        User toSave = entityConverter.fromDTO(user);
        toSave.setActive(true);


        toSave.setDataCreazione(Instant.now());
        toSave.setCreationUser(entityConverter.fromDTO(userAction));

        user.setId(userDAO.add(toSave));
        LOGGER.info("Creazione effettuata con successo per l'utente: " + user.getEmail());
    }

    @Override
    public void update(UserDTO user, UserDTO userAction) {
        User u = userDAO.getById(user.getId());
        Set<Role> ruoliU = u.getRoles();
        ruoliU.addAll(u.getGroup() == null ? u.getGroup().getRoles() : new HashSet<Role>());


        Optional<Role> maxU = ruoliU.stream().reduce((a, b) -> a.compareTo(b) > 0 ? a : b);
        Set<RoleDTO> ruoliUA = userAction.getRuoli();
        ruoliUA.addAll(userAction.getGroupDTO() != null ? userAction.getGroupDTO().getRoleDTO() : new HashSet<RoleDTO>());
        Optional<Role> maxUa = ruoliUA.stream().map(entityConverter::fromDTO).reduce((a, b) -> a.compareTo(b) > 0 ? a : b);
        Set<RoleDTO> ruoliNu = user.getRuoli();
        ruoliNu.addAll(user.getGroupDTO() != null ? user.getGroupDTO().getRoleDTO() : new HashSet<RoleDTO>());


        Optional<Role> maxNu = ruoliNu.stream().map(entityConverter::fromDTO).reduce((a, b) -> a.compareTo(b) > 0 ? a : b);

        if ((!maxU.isEmpty() && maxUa.isEmpty()) || (maxUa.isPresent() && maxU.isPresent() && maxU.get().compareTo(maxUa.get()) > 0)) {
            LOGGER.warn("Tentativo di modifica su un utente con privilegi superiori a quelli dell'utente");
            throw new PriorityException("Impossibile modificare un utente con privilegi superiori a quelli dell'utente");

        }
        if ((!maxNu.isEmpty() && maxUa.isEmpty()) || (maxUa.isPresent() && maxNu.isPresent() && maxNu.get().compareTo(maxUa.get()) > 0)) {
            LOGGER.warn("Tentativo di modifica un utente dandogli privilegi superiori a quelli dell'utente");
            throw new PriorityException("Impossibile modificare un utente dandogli privilegi superiori a quelli dell'utente");

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
        return userDAO.getAll().stream().filter(user -> {
            Set<RoleDTO> ruoliUA = userAction.getRuoli();
            ruoliUA.addAll(userAction.getGroupDTO() != null ? userAction.getGroupDTO().getRoleDTO() : new HashSet<RoleDTO>());
            Optional<Role> maxUa = ruoliUA.stream().map(entityConverter::fromDTO).reduce((a, b) -> a.compareTo(b) > 0 ? a : b);
            Set<Role> ruoliU = user.getRoles();
            ruoliU.addAll(user.getGroup() != null ? user.getGroup().getRoles() : new HashSet<Role>());


            Optional<Role> maxU = ruoliU.stream().reduce((a, b) -> a.compareTo(b) > 0 ? a : b);

            return !((!maxU.isEmpty() && maxUa.isEmpty()) || (maxU.isPresent() && maxU.get().compareTo(maxUa.get()) >= 0));
        }).map(entityConverter::fromEntity).toList();
    }

    @Override
    public Optional<UserDTO> findById(Long id, UserDTO userAction) {
        return Optional.of(entityConverter.fromEntity(userDAO.getById(id)));
    }

    @Override
    public PagedResult<UserDTO> getAllUsersPag(int page, int size, UserDTO userAction) {

        LOGGER.info("Recupero lista utenti in corso");
        PagedResult<User> allUsersPaged = userDAO.getAllPag(page, size);
        PagedResult<UserDTO> copy = PagedResult.copy(allUsersPaged);
        copy.setContent(allUsersPaged.getContent().stream().map(entityConverter::fromEntity).toList());
        return copy;
    }

}
