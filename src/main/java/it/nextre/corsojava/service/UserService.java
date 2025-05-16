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
import it.nextre.corsojava.dao.GroupDAO;
import it.nextre.corsojava.dao.RoleDAO;
import it.nextre.corsojava.dao.TokenUserDAO;
import it.nextre.corsojava.dao.UserDAO;
import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.RoleDTO;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.GroupMissingException;
import it.nextre.corsojava.exception.RoleMissingException;
import it.nextre.corsojava.exception.UnauthorizedException;
import it.nextre.corsojava.exception.UserMissingException;
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
public class UserService implements UserServiceInterface {
    private static final Logger LOGGER = Logger.getLogger(UserService.class);
    private final UserDAO userDAO = UserDAO.getInstance();
    private final TokenUserDAO tokenUserDAO = TokenUserDAO.getIstance();
    private final GroupDAO groupDAO = GroupDAO.getIstance();
    private final RoleDAO roleDAO = RoleDAO.getIstance();


    

    


    @Override
    public TokenDTO login(UserDTO user) {
        LOGGER.info("Login in corso per l'utente: " + user.getEmail());
        TokenDTO tokenDTO = null;
        if (user.getEmail().isBlank() || user.getPassword().isBlank()) {
            LOGGER.warn("Email o password non validi");
            throw new UnauthorizedException("Email o password non validi");
        }
        Optional<User> byEmailPassword = userDAO.findByEmailPassword(user.getEmail(), user.getPassword());
        if (byEmailPassword.isPresent() && byEmailPassword.get().getActive()) {
            User u = byEmailPassword.get();
            Token token = generateToken(u);
            tokenUserDAO.add(token);
            tokenDTO = new TokenDTO(token);
        } else {
            LOGGER.warn("Credenziali non valide");
            throw new UnauthorizedException("Credenziali non valide");
        }
        LOGGER.info("Login effettuato con successo per l'utente: " + user.getEmail());
        return tokenDTO;
    }

    public void logout(TokenDTO token) {
        if (token == null) throw new UnauthorizedException("Token mancante");
        LOGGER.info("Logout in corso per l'utente: " + token.getUserDTO().getEmail());
        Token t = tokenUserDAO.getTokenByValue(token.getValue());
        if (t == null) throw new UnauthorizedException("Token non presente");
        tokenUserDAO.delete(t.getId());
        LOGGER.info("Logout effettuato con successo per l'utente: " + token.getUserDTO().getEmail());
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
        Group group = groupDAO.getById(user.getGroupDTO().getId());
        LOGGER.info("Registrazione in corso per l'utente: " + user.getEmail());
        if (group.getRole().isAdmin() == true) {
            LOGGER.warn("Tentativo di registrazione come admin");
            throw new UnauthorizedException("Non puoi registrarti come admin");
        }
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

    @Override
    public boolean checkToken(TokenDTO token) {
        LOGGER.info("Controllo token in corso per l'utente: " + token.getUserDTO().getEmail());
        Token token2 = tokenUserDAO.getTokenByValue(token.getValue());
        if (token2 == null || !token2.getDataScadenza().isAfter(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            return false;
        token2.setDataScadenza(LocalDateTime.now().plusMinutes(10).toInstant(ZoneOffset.UTC));
        tokenUserDAO.update(token2.getId(), token2);
        return true;
    }

    @Override
    public void updateUser(UserDTO user, TokenDTO token) {
        if (user == null) throw new UnauthorizedException("Utente non valido");
        if (user.getGroupDTO() == null) throw new UnauthorizedException("Gruppo non valido");
        if (user.getGroupDTO().getRoleDTO() == null) throw new UnauthorizedException("Ruolo non valido");
        if (!checkToken(token)) throw new UnauthorizedException("Token non presente");
        User u = userDAO.getById(user.getId());
        LOGGER.info("Modifica in corso per l'utente: " + user.getEmail());
        if (u == null || !u.getActive()) throw new UserMissingException("Utente non trovato");
        Token t = tokenUserDAO.getTokenByValue(token.getValue());
        if (t.getUser().getGroup().getRole().compareTo(new Role(user.getGroupDTO().getRoleDTO())) < 0) {
            LOGGER.warn("Tentativo di modifica del ruolo con priorità maggiore");
            throw new UnauthorizedException("Non puoi cambiare il ruolo di un utente con uno di priorità maggiore al tuo");
        }
        if (t.getUser().getGroup().getRole().compareTo(u.getGroup().getRole()) < 0) {
            LOGGER.warn("Tentativo di modifica di un utente con priorità maggiore");
            throw new UnauthorizedException("Non puoi modificare un utente con priorità maggiore alla tua");
        }
        if (!t.getUser().getId().equals(u.getId()) && !t.getUser().getGroup().getRole().isAdmin()) {
            LOGGER.warn("Tentativo di modifica di un utente con priorità maggiore");
            throw new UnauthorizedException("Non puoi cambiare il ruolo di un utente se il tuo è più basso");
        }
        u.setNome(user.getNome());
        u.setCognome(user.getCognome());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        u.setGroup(new Group(user.getGroupDTO()));
        userDAO.update(user.getId(), u);
        LOGGER.info("Modifica effettuata con successo per l'utente: " + user.getEmail());
    }

    @Override
    public void deleteUser(UserDTO user, TokenDTO token) {
        if (user == null) throw new UnauthorizedException("Utente non valido");
        if (user.getGroupDTO() == null) throw new UnauthorizedException("Gruppo non valido");
        if (user.getGroupDTO().getRoleDTO() == null) throw new UnauthorizedException("Ruolo non valido");
        if (!checkToken(token)) {
            LOGGER.warn("Token non presente");
            throw new UnauthorizedException("Token non presente");
        }
        User u = userDAO.getById(user.getId());
        LOGGER.info("Cancellazione in corso per l'utente: " + user.getEmail());
        if (u == null || !u.getActive()) throw new UnauthorizedException("Utente non trovato");
        Token t = tokenUserDAO.getTokenByValue(token.getValue());
        if (t.getUser().getId().equals(user.getId())) {
            userDAO.delete(user.getId());
            tokenUserDAO.getTokenByIdUser(user.getId()).forEach(tok -> tokenUserDAO.delete(tok.getId()));
        } else if (t.getUser().getGroup().getRole().compareTo(u.getGroup().getRole()) > 0) {
            userDAO.delete(user.getId());
            tokenUserDAO.getTokenByIdUser(user.getId()).forEach(tok -> tokenUserDAO.delete(tok.getId()));
        } else {
            LOGGER.warn("Tentativo di cancellazione di un utente con priorità maggiore");
            throw new UnauthorizedException("Non puoi cancellare un utente con priorità maggiore alla tua");
        }
        LOGGER.info("Cancellazione effettuata con successo per l'utente: " + user.getEmail());
    }

    @Override
    public void createUser(UserDTO user, TokenDTO token) {
        LOGGER.info("Creazione in corso per l'utente: " + user.getEmail());
        if (!checkToken(token)) throw new UnauthorizedException("Token non presente");
        RoleDTO roleDTO = token.getUserDTO().getGroupDTO().getRoleDTO();
        if (!roleDTO.getAdmin() || roleDTO.getPriority() < user.getGroupDTO().getRoleDTO().getPriority()) {
            LOGGER.warn("Tentativo di creazione di un utente con priorità maggiore");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
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

    @Override
    public List<UserDTO> getAllUsers(TokenDTO token) {
        if (!checkToken(token) || !token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di accesso alla lista utenti non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        LOGGER.info("Recupero lista utenti in corso");

        return userDAO.getAll().stream().filter(a -> a.getActive()).map(user -> {
            UserDTO dto = new UserDTO(user);
            dto.setPassword(user.getPassword());
            return dto;
        }).toList();
    }

    @Override
    public void createGroup(GroupDTO group, TokenDTO token) {
        LOGGER.info("Creazione in corso per il gruppo: " + group.getId());
        if (!checkToken(token)) throw new UnauthorizedException("Token non presente");
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di creazione di un gruppo non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        Group toSave = new Group(group);
        groupDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il gruppo: " + group.getId());
    }

    @Override
    public void updateGroup(GroupDTO group, TokenDTO token) {
        LOGGER.info("Modifica in corso per il gruppo: " + group.getId());
        if (!checkToken(token)) throw new UnauthorizedException("Token non presente");
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di modifica di un gruppo non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        Group g = groupDAO.getById(group.getId());
        if (g == null) {
            LOGGER.warn("Tentativo di modifica di un gruppo non valido");
            throw new GroupMissingException("Impossibile modificare un gruppo non presente");
        }
        Group group2 = new Group(group);
        g.setRole(group2.getRole());
        groupDAO.update(group.getId(), g);
        LOGGER.info("Modifica effettuata con successo per il gruppo: " + group.getId());
    }

    @Override
    public void deleteGroup(GroupDTO group, TokenDTO token) {
        LOGGER.info("Cancellazione in corso per il gruppo: " + group.getId());
        if (!checkToken(token)) {
            LOGGER.warn("Tentativo di cancellazione di un gruppo non autorizzato");
            throw new UnauthorizedException("Token non presente");
        }
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di cancellazione di un gruppo non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        if (groupDAO.getById(group.getId()) == null) {
            LOGGER.warn("Tentativo di cancellazione di un gruppo non valido");
            throw new GroupMissingException("Impossibile cancellare un gruppo non presente");
        }
        groupDAO.delete(group.getId());
        LOGGER.info("Cancellazione effettuata con successo per il gruppo: " + group.getId());
    }

    @Override
    public List<GroupDTO> getAllGroup(TokenDTO token) {
        LOGGER.info("Recupero lista gruppi in corso");
        if (!checkToken(token)) {
            LOGGER.warn("Tentativo di accesso alla lista gruppi non autorizzato");
            throw new UnauthorizedException("Token non presente");
        }
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di accesso alla lista gruppi non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        LOGGER.info("Fine recupero lista gruppi");
        return groupDAO.getAll().stream().map(group -> {
            GroupDTO dto = new GroupDTO(group);
            return dto;
        }).toList();
    }

    @Override
    public void createRole(RoleDTO roleDTO, TokenDTO token) {
        LOGGER.info("Creazione in corso per il ruolo: " + roleDTO.getId());
        if (!checkToken(token)) {
            LOGGER.warn("Tentativo di creazione di un ruolo non autorizzato");
            throw new UnauthorizedException("Token non presente");
        }
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di creazione di un ruolo non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        Role toSave = new Role(roleDTO);
        roleDAO.add(toSave);
        LOGGER.info("Creazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    @Override
    public void updateRole(RoleDTO roleDTO, TokenDTO token) {
        LOGGER.info("Modifica in corso per il ruolo: " + roleDTO.getId());
        if (!checkToken(token)) {
            LOGGER.warn("Tentativo di modifica di un ruolo non autorizzato");
            throw new UnauthorizedException("Token non presente");
        }
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di modifica di un ruolo non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        Role role = new Role(roleDTO);

        roleDAO.update(roleDTO.getId(), role);
        LOGGER.info("Modifica effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    @Override
    public void deleteRole(RoleDTO roleDTO, TokenDTO token) {
        LOGGER.info("Cancellazione in corso per il ruolo: " + roleDTO.getId());
        if (!checkToken(token)) {
            LOGGER.warn("Tentativo di cancellazione di un ruolo non autorizzato");
            throw new UnauthorizedException("Token non presente");
        }
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di cancellazione di un ruolo non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        Role r = roleDAO.getById(roleDTO.getId());
        if (r == null) {
            LOGGER.warn("Tentativo di cancellazione di un ruolo non valido");
            throw new RoleMissingException("Ruolo richiesto da cancellare non presente");
        }
        roleDAO.delete(roleDTO.getId());
        LOGGER.info("Cancellazione effettuata con successo per il ruolo: " + roleDTO.getId());
    }

    @Override
    public List<RoleDTO> getAllRole(TokenDTO token) {
        LOGGER.info("Recupero lista ruoli in corso");
        if (!checkToken(token)) {
            LOGGER.warn("Tentativo di accesso alla lista ruoli non autorizzato");
            throw new UnauthorizedException("Token non presente");
        }
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin()) {
            LOGGER.warn("Tentativo di accesso alla lista ruoli non autorizzato");
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        }
        LOGGER.info("Fine recupero lista ruoli");
        return roleDAO.getAll().stream().map(role -> {
            RoleDTO dto = new RoleDTO(role);
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

    @Override
    public TokenDTO confirmRegistration(TokenDTO token) {
        Token token2 = tokenUserDAO.getTokenByValue(token.getValue());
        if (token2 == null || !token2.getDataScadenza().isAfter(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            throw new RuntimeException("token scaduto :rifare la registrazione");
        User user = token2.getUser();
        user.setActive(true);
        userDAO.update(user.getId(), user);
        return token;

    }

}
