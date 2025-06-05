package it.nextre.corsojava.service.UserService;

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
import it.nextre.aut.dto.LoginInfo;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.UserService;
import it.nextre.corsojava.dao.memory.GroupDAO;
import it.nextre.corsojava.dao.memory.RoleDAO;
import it.nextre.corsojava.dao.memory.TokenUserDAO;
import it.nextre.corsojava.dao.memory.UserDAO;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.GroupMissingException;
import it.nextre.corsojava.exception.RoleMissingException;
import it.nextre.corsojava.exception.UnauthorizedException;
import it.nextre.corsojava.exception.UserMissingException;
import it.nextre.corsojava.utils.JwtGenerator;
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

@LookupIfProperty(name = "source.Mem", stringValue = "mem")
public class UserServiceMemory implements UserService{
    private static final Logger LOGGER = Logger.getLogger(UserServiceMemory.class);
    private final UserDAO userDAO = UserDAO.getInstance();
    private final TokenUserDAO tokenUserDAO = TokenUserDAO.getIstance();
    private final GroupDAO groupDAO = GroupDAO.getIstance();
    private final RoleDAO roleDAO = RoleDAO.getIstance();


    

    


    @Override
    public TokenJwtDTO login(LoginInfo user) {
        LOGGER.info("Login in corso per l'utente: " + user.getEmail());
        if (user.getEmail().isBlank() || user.getPassword().isBlank()) {
            LOGGER.warn("Email o password non validi");
            throw new UnauthorizedException("Email o password non validi");
        }
        Optional<User> byEmailPassword = userDAO.findByEmailPassword(user.getEmail(), user.getPassword());
        User u;
        if (byEmailPassword.isPresent() && byEmailPassword.get().getActive()) {
            u = byEmailPassword.get();
        } else {
            LOGGER.warn("Credenziali non valide");
            throw new UnauthorizedException("Credenziali non valide");
        }
        LOGGER.info("Login effettuato con successo per l'utente: " + user.getEmail());
        return JwtGenerator.generateTokens(u.getEmail(), u.getRoles());
    }

    public void logout(Token token) {
        if (token == null) throw new UnauthorizedException("Token mancante");
        LOGGER.info("Logout in corso per l'utente: " + token.getUser().getEmail());
        Token t = tokenUserDAO.getTokenByValue(token.getValue());
        if (t == null) throw new UnauthorizedException("Token non presente");
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
                    + "<a href='http//localhost:8080/confirmeRegistration/" + token.getValue() + "' style='"
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
        if (user.getGroupDTO() == null || groupDAO.getById(user.getGroupDTO().getId()) == null) {
            throw new UnauthorizedException("Gruppo non valido");
        }
        LOGGER.info("Registrazione in corso per l'utente: " + user.getEmail());
       
        if (userDAO.getByEmail(user.getEmail()) != null) {
            LOGGER.warn("Utente già registrato con l'email: " + user.getEmail());
            throw new UnauthorizedException("Utente già registrato");
        }
        User user2 = new User(user);
        user2.setActive(false);
        userDAO.add(user2);
        Token token = generateToken(userDAO.getByEmail(user2.getEmail()));
        tokenUserDAO.add(token);
        sendMail(user2, token);
        LOGGER.info("email inviata all'utente: " + user.getEmail());


    }

    public boolean checkToken(Token token) {
        LOGGER.info("Controllo token in corso per l'utente: " + token.getUser().getEmail());
        Token token2 = tokenUserDAO.getTokenByValue(token.getValue());
        if (token2 == null || !token2.getDataScadenza().isAfter(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            return false;
        token2.setDataScadenza(LocalDateTime.now().plusMinutes(10).toInstant(ZoneOffset.UTC));
        tokenUserDAO.update(token2.getId(), token2);
        return true;
    }
   

    @Override
    public void update(UserDTO user) {
        if (user == null) throw new UnauthorizedException("Utente non valido");
        if (user.getGroupDTO() == null) throw new UnauthorizedException("Gruppo non valido");
        if (user.getGroupDTO().getRoleDTO() == null) throw new UnauthorizedException("Ruolo non valido");
       User u = userDAO.getById(user.getId());
        LOGGER.info("Modifica in corso per l'utente: " + user.getEmail());
        if (u == null || !u.getActive()) throw new UserMissingException("Utente non trovato");
       
        u.setNome(user.getNome());
        u.setCognome(user.getCognome());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        userDAO.update(user.getId(), u);
        LOGGER.info("Modifica effettuata con successo per l'utente: " + user.getEmail());
    }

    public void delete(UserDTO user) {
        if (user == null) throw new UnauthorizedException("Utente non valido");
        if (user.getGroupDTO() == null) throw new UnauthorizedException("Gruppo non valido");
        if (user.getGroupDTO().getRoleDTO() == null) throw new UnauthorizedException("Ruolo non valido");
       
        User u = userDAO.getById(user.getId());
        LOGGER.info("Cancellazione in corso per l'utente: " + user.getEmail());
        if (u == null || !u.getActive()) throw new UnauthorizedException("Utente non trovato");
        
        LOGGER.info("Cancellazione effettuata con successo per l'utente: " + user.getEmail());
    }

    public void createUser(UserDTO user) {
        LOGGER.info("Creazione in corso per l'utente: " + user.getEmail());
       
       
        User toSave = new User(user);

        Group toPut = groupDAO.getById(user.getGroupDTO().getId());

        if (toPut == null) {
            LOGGER.warn("Tentativo di creazione di un utente con gruppo non valido");
            throw new GroupMissingException("Il gruppo di appartenenza non esiste");
        }

        toSave.setGroup(toPut);
        toSave.setActive(true);
        userDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per l'utente: " + user.getEmail());
    }

    public List<UserDTO> getAllUsers() {

        LOGGER.info("Recupero lista utenti in corso");

        return userDAO.getAll().stream().filter(a -> a.getActive()).map(user -> {
            UserDTO dto =UserDTO.of()
					.id(user.getId())
					.nome(user.getNome())
					.cognome(user.getCognome())
					.email(user.getEmail())
					.groupDTO(GroupDTO.of().id(user.getGroup().getId())
							.roleDTO(user.getGroup().getRoles().stream()
									.map(role -> RoleDTO.of().id(role.getId())
											.admin(role.isAdmin())
											.priority(role.getPriority())
											.descrizione(role.getDescrizione())
											.build()).collect(Collectors.toSet()))
							.build())
					.build();
            dto.setPassword(user.getPassword());
            return dto;
        }).toList();
    }

    public void createGroup(GroupDTO group) {
        LOGGER.info("Creazione in corso per il gruppo: " + group.getId());
      
        Group toSave = new Group(group);
        groupDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il gruppo: " + group.getId());
    }

    public void updateGroup(GroupDTO group) {
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

    public void deleteGroup(GroupDTO group) {
        LOGGER.info("Cancellazione in corso per il gruppo: " + group.getId());
       
        if (groupDAO.getById(group.getId()) == null) {
            LOGGER.warn("Tentativo di cancellazione di un gruppo non valido");
            throw new GroupMissingException("Impossibile cancellare un gruppo non presente");
        }
        groupDAO.delete(group.getId());
        LOGGER.info("Cancellazione effettuata con successo per il gruppo: " + group.getId());
    }

    public List<GroupDTO> getAllGroup() {
        LOGGER.info("Recupero lista gruppi in corso");
        
        LOGGER.info("Fine recupero lista gruppi");
        return groupDAO.getAll().stream().map(group -> {
            GroupDTO dto =GroupDTO.of().id(group.getId())
            		.roleDTO(group.getRoles().stream()
						.map(role -> RoleDTO.of().id(role.getId())
								.admin(role.isAdmin())
								.priority(role.getPriority())
								.descrizione(role.getDescrizione())
								.build())
						.collect(Collectors.toSet())).build();
            return dto;
        }).toList();
    }

    public void createRole(RoleDTO roleDTO) {
        LOGGER.info("Creazione in corso per il ruolo: " + roleDTO.getId());
        
        Role toSave = new Role(roleDTO);
        roleDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    public void updateRole(RoleDTO roleDTO) {
        LOGGER.info("Modifica in corso per il ruolo: " + roleDTO.getId());
       
        Role role = new Role(roleDTO);

        roleDAO.update(roleDTO.getId(), role);
        LOGGER.info("Modifica effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    public void deleteRole(RoleDTO roleDTO) {
        LOGGER.info("Cancellazione in corso per il ruolo: " + roleDTO.getId());
      
        Role r = roleDAO.getById(roleDTO.getId());
        if (r == null) {
            LOGGER.warn("Tentativo di cancellazione di un ruolo non valido");
            throw new RoleMissingException("Ruolo richiesto da cancellare non presente");
        }
        roleDAO.delete(roleDTO.getId());
        LOGGER.info("Cancellazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    public List<RoleDTO> getAllRole() {
        LOGGER.info("Recupero lista ruoli in corso");
        
        LOGGER.info("Fine recupero lista ruoli");
        return roleDAO.getAll().stream().map(role -> {
            RoleDTO dto =RoleDTO.of()
				.id(role.getId())
				//TODO: aggiungere i campi necessari
				.build();
            return dto;
        }).toList();
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

    public TokenJwtDTO confirmRegistration(String token) {
        Token token2 = tokenUserDAO.getTokenByValue(token);
        if (token2 == null || !token2.getDataScadenza().isAfter(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            throw new RuntimeException("token scaduto :rifare la registrazione");
        User user = token2.getUser();
        user.setActive(true);
        userDAO.update(user.getId(), user);
        return JwtGenerator.generateTokens(user.getEmail(), user.getRoles());

    }

	public Token findTokenByValue(String val) {
		return tokenUserDAO.getTokenByValue(val);
	}

	public PagedResult<UserDTO> getAllUsersPag( int page, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	public PagedResult<GroupDTO> getAllGroupsPag( int page, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	public PagedResult<RoleDTO> getAllRolesPag( int page, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TokenJwtDTO refreshToken(UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}

}
