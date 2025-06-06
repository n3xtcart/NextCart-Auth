package it.nextre.corsojava.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.security.UnauthorizedException;
import it.nextre.aut.dto.LoginInfo;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.service.UserService;
import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
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
    protected final UserJdbcDao userDAO = UserJdbcDao.getInstance();
    protected final TokenJdbcDao tokenUserDAO = TokenJdbcDao.getInstance();
    protected final EntityConverter entityConverter;
    
    public UserServiceJdbc(EntityConverter entityConverter) {
		this.entityConverter = new EntityConverter();
	}


 


    @Override
    public TokenJwtDTO login(LoginInfo info) {
        LOGGER.info("Login in corso per l'utente: " + info.getEmail());
        if (info.getEmail().isBlank() || info.getPassword().isBlank()) {
            LOGGER.warn("Email o password non validi");
            throw new UnauthorizedException("Email o password non validi");
        }
        Optional<User> byEmailPassword = userDAO.findByEmailPassword(info.getEmail(), info.getPassword());
        User u;
        if (byEmailPassword.isPresent() && byEmailPassword.get().getActive()) {
            u = byEmailPassword.get();
        } else {
            LOGGER.warn("Credenziali non valide");
            throw new UnauthorizedException("Credenziali non valide");
        }
        LOGGER.info("Login effettuato con successo per l'utente: " + info.getEmail());
        return JwtGenerator.generateTokens(u.getEmail(), u.getRoles());
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
       
        if (userDAO.getByEmail(user.getEmail()) != null) {
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
       User u = userDAO.getById(user.getId());

        LOGGER.info("Modifica in corso per l'utente: " + user.getEmail());
        
        
        u.setNome(user.getNome());
        u.setCognome(user.getCognome());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        userDAO.update(user.getId(), u);
        LOGGER.info("Modifica effettuata con successo per l'utente: " + user.getEmail());
    }

    @Override
    public void delete(UserDTO user) {
        if (user == null) throw new UnauthorizedException("Utente non valido");
        if (user.getRuoli() == null) throw new UnauthorizedException("Ruolo non valido");
       
        User u = userDAO.getById(user.getId());
        LOGGER.info("Cancellazione in corso per l'utente: " + user.getEmail());
        if (u == null || !u.getActive()) throw new UnauthorizedException("Utente non trovato");
        
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
		LOGGER.info("Refresh token in corso per l'utente: " + user.getEmail());
		if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
			LOGGER.warn("Utente o email non validi");
			throw new UnauthorizedException("Utente o email non validi");
		}
		Optional<User> byEmail = Optional.of(userDAO.getByEmail(user.getEmail()));
		User u;
		if (byEmail.isPresent() && byEmail.get().getActive()) {
			u = byEmail.get();
		} else {
			LOGGER.warn("Utente non trovato o non attivo");
			throw new UnauthorizedException("Utente non trovato o non attivo");
		}
		
		// Generazione nuovo token
		return JwtGenerator.generateTokens(u.getEmail(), u.getRoles());
	}





	@Override
	public TokenJwtDTO confirmRegistration(String tokenDTO) {
        Token token2 = tokenUserDAO.getTokenByValue(tokenDTO);
        if (token2 == null || !token2.getDataScadenza().isAfter(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            throw new RuntimeException("token scaduto :rifare la registrazione");
        User user = token2.getUser();
        user.setActive(true);
        userDAO.update(user.getId(), user);
        return JwtGenerator.generateTokens(user.getEmail(), user.getRoles());

    }





	

}
