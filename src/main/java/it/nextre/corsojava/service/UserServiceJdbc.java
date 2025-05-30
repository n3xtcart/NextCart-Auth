package it.nextre.corsojava.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.dao.jdbc.PagedResult;
import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.dto.TokensJwt;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.GroupMissingException;
import it.nextre.corsojava.exception.RoleMissingException;
import it.nextre.corsojava.exception.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;

@ApplicationScoped
@LookupIfProperty(name = "source.Mem", stringValue = "db")
public class UserServiceJdbc implements UserServiceInterface {
    private static final Logger LOGGER = Logger.getLogger(UserServiceJdbc.class);
    private static ObjectService objectService = ObjectService.getInstance();
    private final UserJdbcDao userDAO = UserJdbcDao.getInstance();
    private final TokenJdbcDao tokenUserDAO = TokenJdbcDao.getInstance();
    private final GroupJdbcDao groupDAO = GroupJdbcDao.getInstance();
    private final RoleJdbcDao roleDAO = RoleJdbcDao.getInstance();


 


    @Override
    public TokensJwt login(UserDTO user) {
        LOGGER.info("Login in corso per l'utente: " + user.getEmail());
        if (user.getEmail().isBlank() || user.getPassword().isBlank()) {
            LOGGER.warn("Email o password non validi");
            throw new UnauthorizedException("Email o password non validi");
        }
        Optional<User> byEmailPassword = objectService.getUserByEmailPassword(user.getEmail(), user.getPassword());
        User u;
        if (byEmailPassword.isPresent() && byEmailPassword.get().getActive()) {
            u = byEmailPassword.get();
        } else {
            LOGGER.warn("Credenziali non valide");
            throw new UnauthorizedException("Credenziali non valide");
        }
        LOGGER.info("Login effettuato con successo per l'utente: " + user.getEmail());
        return new TokensJwt(u.getEmail(), u.getRoles());
    }

    public void logout(Token token) {
        if (token == null) throw new UnauthorizedException("Token mancante");
        LOGGER.info("Logout in corso per l'utente: " + token.getUser().getEmail());
        Token t = objectService.getTokenByValue(token.getValue());
        if (t == null) throw new UnauthorizedException("token non presente");
        tokenUserDAO.delete(t.getId());
        LOGGER.info("Logout effettuato con successo per l'utente: " + token.getUser().getEmail());
    }


    public void sendMail(User user, Token token) {

        // Proprietà della connessione
        Properties props = new Properties();
        try {
            props.load(this.getClass().getResourceAsStream("/email.properties"));
        } catch (IOException e) {
            throw new RuntimeException("error loading properties email " + e.getMessage(), e);
        }
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = props.getProperty("username");
                String password = props.getProperty("password");

                if (username == null || password == null) {
                    throw new IllegalArgumentException("Username o password non definiti nelle proprietà!");
                }

                return new PasswordAuthentication(username, password);
            }
        });
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(props.getProperty("username")));
            message.setRecipient(RecipientType.TO, new InternetAddress(user.getEmail()));
            message.setSubject("Email di conferma registrazione");
            message.setText("premi il bottone sottostante per confermare la registrazione");
            String button = "<html><body>"
                    + "<h2>Ciao!</h2>"
                    + "<p>Clicca sul bottone qui sotto per visitare il nostro sito:</p>"
                    + "<a href='http://localhost:8080/users/confirmRegistration/" + token.getValue() + "' style='"
                    + "display: inline-block; padding: 10px 20px; font-size: 16px; "
                    + "color: white; background-color: #007bff; text-decoration: none; "
                    + "border-radius: 5px; font-family: Arial, sans-serif;'>"
                    + "Visita il sito</a>"
                    + "</body>"

                    + "</html>";
            message.setContent(button, "text/html; charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("error creating message email " + e.getMessage(), e);
        }


    }

    @Override
    public void register(UserDTO user) {
        if (user == null) {
            throw new UnauthorizedException("Utente non valido");
        }
        LOGGER.info("Registrazione in corso per l'utente: " + user.getEmail());
       
        if (objectService.getUserByEmail(user.getEmail()) != null) {
            LOGGER.warn("Utente già registrato con l'email: " + user.getEmail());
            throw new UnauthorizedException("Utente già registrato");
        }
        User user2 = new User();
        user2.setNome(user.getNome());
        user2.setCognome(user.getCognome());
        user2.setEmail(user.getEmail());
        user2.setPassword(user.getPassword());
        //TODO: gestire il gruppo di default
        user2.setGroup(null);
        //TODO: gestire il ruolo di default
        user2.setRole(null);
        user2.setActive(false);
        userDAO.add(user2);
        Token token = generateToken(objectService.getUserByEmail(user2.getEmail()));
        tokenUserDAO.add(token);
        sendMail(user2, token);
        LOGGER.info("email inviata all'utente: " + user.getEmail());

    }

    @Override
    public boolean checkToken(Token token) {
        LOGGER.info("Controllo token in corso per l'utente: " + token.getUser().getEmail());
        Token token2 = objectService.getTokenByValue(token.getValue());
        if (token2 == null || !token2.getDataScadenza().isAfter(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            return false;
        token2.setDataScadenza(LocalDateTime.now().plusMinutes(10).toInstant(ZoneOffset.UTC));
        tokenUserDAO.update(token2.getId(), token2);
        return true;
    }

    @Override
    public void updateUser(UserDTO user) {
       User u = objectService.getUserById(user.getId());

        LOGGER.info("Modifica in corso per l'utente: " + user.getEmail());
        
        
        u.setNome(user.getNome());
        u.setCognome(user.getCognome());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        userDAO.update(user.getId(), u);
        LOGGER.info("Modifica effettuata con successo per l'utente: " + user.getEmail());
    }

    @Override
    public void deleteUser(UserDTO user) {
        if (user == null) throw new UnauthorizedException("Utente non valido");
        if (user.getRuoli() == null) throw new UnauthorizedException("Ruolo non valido");
       
        User u = objectService.getUserById(user.getId());
        LOGGER.info("Cancellazione in corso per l'utente: " + user.getEmail());
        if (u == null || !u.getActive()) throw new UnauthorizedException("Utente non trovato");
        
        LOGGER.info("Cancellazione effettuata con successo per l'utente: " + user.getEmail());
    }

    @Override
    public void createUser(UserDTO user) {
        LOGGER.info("Creazione in corso per l'utente: " + user.getEmail());
        
        User toSave = new User(user);
        toSave.setActive(true);

        Group toPut = objectService.getGroupById(user.getGroupDTO().getId());

        if (toPut == null) {
            LOGGER.warn("Tentativo di creazione di un utente con gruppo non valido");
            throw new GroupMissingException("Il gruppo di appartenenza non esiste");
        }

        toSave.setGroup(toPut);
        userDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per l'utente: " + user.getEmail());
    }

    @Override
    public List<UserDTO> getAllUsers() {
       
        LOGGER.info("Recupero lista utenti in corso");
        return objectService.getAllUsers().stream().filter(a -> a.getActive()).map(user -> {
            UserDTO dto=UserDTO.of().cognome(user.getNome())
            		.nome(user.getCognome())
            		.email(user.getEmail())
            		.id(user.getId())
            		.ruoli(user.getRoles().stream().map(role -> {
						RoleDTO roleDTO =RoleDTO.of()
								.id(role.getId())
								.admin(role.getAdmin())
								.descrizione(role.getDescrizione())
								.priority(role.getPriority())
								.build();
						return roleDTO;
					}).collect(Collectors.toSet()))
            		.build();
            dto.setPassword(user.getPassword());
            return dto;
        }).toList();
    }
    

    @Override
    public PagedResult<UserDTO> getAllUsersPag(int pag,int pagSize) {
    	
    	LOGGER.info("Recupero lista utenti in corso");
		PagedResult<User> allUsersPaged = objectService.getAllUsersPaged(pag,pagSize);
		PagedResult<UserDTO> copy = PagedResult.copy(allUsersPaged);
		copy.setContent(allUsersPaged.getContent().stream().filter(a -> a.getActive()).map(user -> {
    		UserDTO dto =UserDTO.of().cognome(user.getNome())
            		.nome(user.getCognome())
            		.email(user.getEmail())
            		.id(user.getId())
            		.ruoli(user.getRoles().stream().map(role -> {
						RoleDTO roleDTO =RoleDTO.of()
								.id(role.getId())
								.admin(role.getAdmin())
								.descrizione(role.getDescrizione())
								.priority(role.getPriority())
								.build();
						return roleDTO;
					}).collect(Collectors.toSet()))
            		.build();
    		dto.setPassword(user.getPassword());
    		return dto;
    	}).toList());
		return copy;
    }

    @Override
    public void createGroup(GroupDTO group) {
        LOGGER.info("Creazione in corso per il gruppo: " + group.getId());
      
        Group toSave = new Group(group);
        groupDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il gruppo: " + group.getId());
    }

    @Override
    public void updateGroup(GroupDTO group) {
        LOGGER.info("Modifica in corso per il gruppo: " + group.getId());
      
        Group g = objectService.getGroupById(group.getId());
        if (g == null) {
            LOGGER.warn("Tentativo di modifica di un gruppo non valido");
            throw new GroupMissingException("Impossibile modificare un gruppo non presente");
        }
        Group group2 = new Group(group);
        g.setRole(group2.getRoles());
        groupDAO.update(group.getId(), g);
        LOGGER.info("Modifica effettuata con successo per il gruppo: " + group.getId());
    }

    @Override
    public void deleteGroup(GroupDTO group) {
        LOGGER.info("Cancellazione in corso per il gruppo: " + group.getId());
       
        if (objectService.getGroupById(group.getId()) == null) {
            LOGGER.warn("Tentativo di cancellazione di un gruppo non valido");
            throw new GroupMissingException("Impossibile cancellare un gruppo non presente");
        }
        groupDAO.delete(group.getId());
        LOGGER.info("Cancellazione effettuata con successo per il gruppo: " + group.getId());
    }

    @Override
    public List<GroupDTO> getAllGroup() {
        LOGGER.info("Recupero lista gruppi in corso");
       
        LOGGER.info("Fine recupero lista gruppi");
        return objectService.getAllGroup().stream().map(group -> {
            GroupDTO dto =GroupDTO.of().id(group.getId())
           // TODO aggiungere ruoli
            		
            		.build();
            return dto;
        }).toList();
    }
    

    @Override
    public PagedResult<GroupDTO> getAllGroupsPag(int pag,int pagSize) {
    	LOGGER.info("Recupero lista gruppi in corso");
    
    	LOGGER.info("Fine recupero lista gruppi");
    	PagedResult<Group> allGroupsPaged = objectService.getAllGroupPaged(pag,pagSize);
		PagedResult<GroupDTO> copy = PagedResult.copy(allGroupsPaged);
		copy.setContent(allGroupsPaged.getContent().stream().map(group -> {
    		GroupDTO dto  =GroupDTO.of().id(group.getId())
           // TODO aggiungere ruoli
            		
            		.build();
    		return dto;
    	}).toList());
		return copy;
    
    }

    @Override
    public void createRole(RoleDTO roleDTO) {
        LOGGER.info("Creazione in corso per il ruolo: " + roleDTO.getId());
       
        Role toSave = new Role(roleDTO);
        roleDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    @Override
    public void updateRole(RoleDTO roleDTO) {
        LOGGER.info("Modifica in corso per il ruolo: " + roleDTO.getId());
       
        Role role = new Role(roleDTO);

        roleDAO.update(roleDTO.getId(), role);
        LOGGER.info("Modifica effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    @Override
    public void deleteRole(RoleDTO roleDTO) {
        LOGGER.info("Cancellazione in corso per il ruolo: " + roleDTO.getId());
       
        Role r = objectService.getRoleById(roleDTO.getId());
        if (r == null) {
            LOGGER.warn("Tentativo di cancellazione di un ruolo non valido");
            throw new RoleMissingException("Ruolo richiesto da cancellare non presente");
        }
        roleDAO.delete(roleDTO.getId());
        LOGGER.info("Cancellazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    @Override
    public List<RoleDTO> getAllRole() {
        LOGGER.info("Recupero lista ruoli in corso");
      
        LOGGER.info("Fine recupero lista ruoli");
        return roleDAO.getAll().stream().map(role -> {
            RoleDTO dto = RoleDTO.of()
					.id(role.getId())
					.admin(role.getAdmin())
					.descrizione(role.getDescrizione())
					.priority(role.getPriority())
					.build();
            return dto;
        }).toList();
    }
    @Override
    public PagedResult<RoleDTO> getAllRolesPag(int pag,int pagSize) {
    	LOGGER.info("Recupero lista ruoli in corso");
    	
    	LOGGER.info("Fine recupero lista ruoli");
    	PagedResult<Role> allGroupsPaged = objectService.getAllRolePaged(pag,pagSize);
		PagedResult<RoleDTO> copy = PagedResult.copy(allGroupsPaged);
		copy.setContent(allGroupsPaged.getContent().stream().map(role -> {
			RoleDTO dto = RoleDTO.of()
					.id(role.getId())
					.admin(role.getAdmin())
					.descrizione(role.getDescrizione())
					.priority(role.getPriority())
					.build();
    		return dto;
    	}).toList());
		return copy;
    }

    public Token generateToken(User user) {
        LOGGER.info("Generazione token in corso per l'utente: " + user.getEmail());
        Set<String> allTokens = tokenUserDAO.getAll().stream().map(Token::getValue).collect(Collectors.toSet());
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (allTokens.contains(token));
        Token tokenUser = new Token();
        tokenUser.setValue(token);
        tokenUser.setUser(user);
        tokenUser.setDataScadenza(LocalDateTime.now().plusMinutes(10).toInstant(ZoneOffset.UTC));
        LOGGER.info("Token generato con successo per l'utente: " + user.getEmail());
        return tokenUser;
    }

    @Override
    public TokensJwt confirmRegistration(Token token) {
        Token token2 = objectService.getTokenByValue(token.getValue());
        if (token2 == null || !token2.getDataScadenza().isAfter(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            throw new RuntimeException("token scaduto :rifare la registrazione");
        User user = token2.getUser();
        user.setActive(true);
        userDAO.update(user.getId(), user);
        return new TokensJwt(user.getEmail(), user.getRoles());

    }

	@Override
	public Token findTokenByValue(String val) {
		return objectService.getTokenByValue(val);
	}

}
