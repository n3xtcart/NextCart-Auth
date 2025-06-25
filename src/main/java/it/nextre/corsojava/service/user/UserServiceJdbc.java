package it.nextre.corsojava.service.user;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.security.UnauthorizedException;
import it.nextre.aut.dto.LoginInfo;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.service.UserService;
import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.MailException;
import it.nextre.corsojava.exception.PriorityException;
import it.nextre.corsojava.utils.EntityConverter;
import it.nextre.corsojava.utils.JwtGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
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
@Named("defaultJdbc")
@LookupIfProperty(name = "source.Mem", stringValue = "db")
public class UserServiceJdbc implements UserService {
    private static final Logger LOGGER = Logger.getLogger(UserServiceJdbc.class);
    protected final UserJdbcDao userDAO;
    protected final TokenJdbcDao tokenUserDAO ;
    protected final GroupJdbcDao groupJdbcDao;
    protected final EntityConverter entityConverter;
    protected final JwtGenerator jwtGenerator;
    
    public UserServiceJdbc(EntityConverter entityConverter,
    		UserJdbcDao userDAO, TokenJdbcDao tokenUserDAO,JwtGenerator jwtGenerator, GroupJdbcDao groupJdbcDao) {
		this.entityConverter = entityConverter;
		this.userDAO = userDAO;
		this.tokenUserDAO = tokenUserDAO;
		this.jwtGenerator = jwtGenerator;
		this.groupJdbcDao=groupJdbcDao;
	}
  


 


    @Override
    public TokenJwtDTO login(LoginInfo info) {
        if (info==null||info.getEmail()==null || info.getPassword()==null|| info.getEmail().isBlank() || info.getPassword().isBlank()) {
            LOGGER.warn("Email o password non validi");
            throw new UnauthorizedException("Email o password non validi");
        }
        LOGGER.info("Login in corso per l'utente: " + info.getEmail());
        Optional<User> byEmailPassword = userDAO.findByEmailPassword(info.getEmail(), info.getPassword());
        User u;
        if (byEmailPassword.isPresent() && byEmailPassword.get().getActive()) {
            u = byEmailPassword.get();
        } else {
            LOGGER.warn("Credenziali non valide");
            throw new UnauthorizedException("Credenziali non valide");
        }
        LOGGER.info("Login effettuato con successo per l'utente: " + info.getEmail());
        return jwtGenerator.generateTokens(entityConverter.fromEntity(u));
    }

    public void logout(Token token) {
        if (token == null) throw new UnauthorizedException("Token mancante");
        LOGGER.info("Logout in corso per l'utente: " + token.getUser().getEmail());
        Token t = tokenUserDAO.getTokenByValue(token.getValue());
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
            throw new MailException("error loading properties email " + e.getMessage(), e);
        }
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = props.getProperty("username");
                String password = props.getProperty("password");

                if (username == null || password == null) {
                    throw new MailException("Username o password non definiti nelle proprietà!");
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
                    + "<a href='http://localhost:4200/verify?token=" + token.getValue() + "' style='"
                    + "display: inline-block; padding: 10px 20px; font-size: 16px; "
                    + "color: white; background-color: #007bff; text-decoration: none; "
                    + "border-radius: 5px; font-family: Arial, sans-serif;'>"
                    + "Visita il sito</a>"
                    + "</body>"

                    + "</html>";
            message.setContent(button, "text/html; charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new MailException("error creating message email " + e.getMessage(), e);
        }


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
        if(user.getGroupDTO()!=null && user.getGroupDTO().getRoleDTO() != null
        		&& user.getGroupDTO().getRoleDTO().stream().anyMatch(RoleDTO::getAdmin) ) {
			LOGGER.warn("tentativo di registrazione come admin");
			throw new it.nextre.corsojava.exception.UnauthorizedException("Non puoi registrarti come admin");
		}
        User user2 = new User();
        user2.setNome(user.getNome());
        user2.setCognome(user.getCognome());
        user2.setEmail(user.getEmail());
        user2.setPassword(user.getPassword());
        Group byDescrizione = groupJdbcDao.findByDescrizione("user");
        user2.setGroup(byDescrizione);
        user2.setActive(false);
        user2.setDataCreazione(Instant.now());
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
    public void update(UserDTO user,UserDTO userAction) {
       User u = userDAO.getById(user.getId());
       Optional<Role> maxUa = userAction.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
       
       if(!u.getId().equals(userAction.getId())) {
			LOGGER.warn("Tentativo di modifica su un utente diveso da quello che sta effettuando l'aggiornamento");
			throw new PriorityException("Impossibile modificare un utente diveso da quello che sta effettuando l'aggiornamento");
       	
       }
       Optional<Role> maxNu =user.getRuoli().stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
       if((!maxNu.isEmpty() && maxUa.isEmpty()) || (maxUa.isPresent() &&maxNu.isPresent()&&  maxNu.get().compareTo(maxUa.get()) > 0)) {
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
    public void delete(UserDTO user,UserDTO userAction) {
        if (user == null) throw new UnauthorizedException("Utente non valido");
        
        User u = userDAO.getById(user.getId());
        Set<RoleDTO> ruoliUA = userAction.getRuoli();
        ruoliUA.addAll(userAction.getGroupDTO()!=null?userAction.getGroupDTO().getRoleDTO():new HashSet<RoleDTO>());
        Optional<Role> maxUa = ruoliUA.stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        Set<RoleDTO> ruoliU = user.getRuoli();
        ruoliU.addAll(user.getGroupDTO()!=null?user.getGroupDTO().getRoleDTO():new HashSet<RoleDTO>());
        
        
        Optional<Role> maxU =ruoliU.stream().map(entityConverter::fromDTO).reduce((a,b)->a.compareTo(b) >0?a:b);
        if((!maxU.isEmpty() && maxUa.isEmpty()) || (maxUa.isPresent() && maxU.isPresent()&&  maxU.get().compareTo(maxUa.get()) > 0)) {
 			LOGGER.warn("Tentativo di cancellazione un utente con privilegi superiori a quelli dell'utente");
 			throw new PriorityException("Impossibile cancellare un utente con privilegi superiori a quelli dell'utente");
        	
        }
        LOGGER.info("Cancellazione in corso per l'utente: " + user.getEmail());
        if ( !u.getActive()) throw new UnauthorizedException("Utente non trovato");
        
        userDAO.delete(u.getId());
        LOGGER.info("Cancellazione effettuata con successo per l'utente: " + user.getEmail());
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

  

	public Token findTokenByValue(String val) {
		return tokenUserDAO.getTokenByValue(val);
	}








	@Override
	public TokenJwtDTO refreshToken(UserDTO user) {
		if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
			LOGGER.warn("Utente o email non validi");
			throw new UnauthorizedException("Utente o email non validi");
		}
		LOGGER.info("Refresh token in corso per l'utente: " + user.getEmail());
		User byEmail = userDAO.getByEmail(user.getEmail());
		if (byEmail==null || !byEmail.getActive()) {
			LOGGER.warn("Utente non trovato o non attivo");
			throw new UnauthorizedException("Utente non trovato o non attivo");
		}
		
		// Generazione nuovo token
		return jwtGenerator.generateTokens(entityConverter.fromEntity(byEmail));
	}





	@Override
	public TokenJwtDTO confirmRegistration(String tokenDTO) {
        Token token2 = tokenUserDAO.getTokenByValue(tokenDTO);
        if (token2 == null || !token2.getDataScadenza().isAfter(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            throw new RuntimeException("token scaduto :rifare la registrazione");
        User user = token2.getUser();
        user.setActive(true);
        userDAO.update(user.getId(), user);
        return jwtGenerator.generateTokens(entityConverter.fromEntity(user));

    }





	

}
