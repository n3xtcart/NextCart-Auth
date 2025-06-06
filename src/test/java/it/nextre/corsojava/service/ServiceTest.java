//package it.nextre.corsojava.service;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import it.nextre.aut.dto.GroupDTO;
//import it.nextre.aut.dto.RoleDTO;
//import it.nextre.aut.dto.UserDTO;
//import it.nextre.corsojava.dao.memory.GroupDAO;
//import it.nextre.corsojava.dao.memory.RoleDAO;
//import it.nextre.corsojava.dao.memory.TokenUserDAO;
//import it.nextre.corsojava.dao.memory.UserDAO;
//import it.nextre.corsojava.dto.TokensJwtDTO;
//import it.nextre.corsojava.entity.Group;
//import it.nextre.corsojava.entity.Role;
//import it.nextre.corsojava.entity.Token;
//import it.nextre.corsojava.entity.User;
//import it.nextre.corsojava.exception.UnauthorizedException;
//import it.nextre.corsojava.exception.UserMissingException;
//import it.nextre.corsojava.utils.EntityConverter;
//
//class ServiceTest {
//    UserService userService;
//    GroupDAO groupDAO;
//    RoleDAO roleDAO;
//    TokenUserDAO tokenUserDAO;
//    UserDAO userDAO;
//    EntityConverter entityConverter=new EntityConverter();
//
//
//    @BeforeEach
//    void setUp() {
//        groupDAO=GroupDAO.getIstance();
//        roleDAO=RoleDAO.getIstance();
//        tokenUserDAO=TokenUserDAO.getIstance();
//        userDAO = UserDAO.getInstance();
//        userService =new UserService();
//        groupDAO.setDatabase(new HashMap<Long, Group>());
//        roleDAO.setDatabase(new HashMap<Long, Role>());
//        tokenUserDAO.setDatabase(new HashMap<Long, Token>());
//        userDAO.setDatabase(new HashMap<Long, User>());
//        Role role = new Role();
//        role.setDescrizione("admin" + 1);
//        role.setPriority((long) 1);
//        role.setAdmin(true);
//        roleDAO.add(role);
//
//        Role role2 = new Role();
//        role2.setDescrizione("admin" + 2);
//        role2.setPriority((long) 2);
//        role2.setAdmin(false);
//        roleDAO.add(role2);
//
//        Group group = new Group();
//        Set<Role> roleSet = new HashSet<>();
//        roleSet.add(role);
//        group.setRoles(roleSet);
//        groupDAO.add(group);
//
//        Group group2 = new Group();
//        Set<Role> roleSet2 = new HashSet<>();
//        roleSet2.add(role2);
//        group.setRoles(roleSet2);
//        groupDAO.add(group2);
//
//        for (int i = 0; i < 10; i++) {
//            User user = new User();
//            user.setCognome("cognome" + i);
//            user.setNome("meUser" + i);
//            user.setEmail("user" + i + "@example.com");
//            user.setPassword("password" + i);
//            user.setActive(true);
//            user.setGroup(group);
//            userDAO.add(user);
//            Token token = userService.generateToken(user);
//            tokenUserDAO.add(token);
//
//        }
//        for (int i = 10; i < 20; i++) {
//            User user = new User();
//            user.setCognome("cognome" + i);
//            user.setNome("meUser" + i);
//            user.setEmail("user" + i + "@example.com");
//            user.setPassword("password" + i);
//            user.setGroup(group2);
//            user.setActive(true);
//            userDAO.add(user);
//            Token token = userService.generateToken(user);
//            tokenUserDAO.add(token);
//
//        }
//
//
//    }
//
//
//    @Test
//    void loginTest() {
//        UserDTO userOk = new UserDTO();
//        userOk.setCognome("cognome");
//        userOk.setNome("nome");
//        userOk.setEmail("user1@example.com");
//        userOk.setPassword("password1");
//        userOk.setGroupDTO(null);
//        TokensJwtDTO loginOk = userService.login(userOk);
//        assertNotNull(loginOk);
//
//        UserDTO userKo = new UserDTO();
//        userKo.setCognome("cognome");
//        userKo.setNome("nome");
//        userKo.setEmail("user100@example.com");
//        userKo.setPassword("password100");
//        userKo.setGroupDTO(null);
//        assertThrows(UnauthorizedException.class, () -> {
//            userService.login(userKo);
//        });
//        
//        UserDTO userKo2 = new UserDTO();
//        userKo2.setCognome("cognome");
//        userKo2.setNome("nome");
//        userKo2.setEmail("user1@example.com");
//        userKo2.setPassword("");
//        userKo2.setGroupDTO(null);
//        assertThrows(UnauthorizedException.class, () -> {
//        	userService.login(userKo2);
//        });
//        
//        UserDTO userKo3 = new UserDTO();
//        userKo3.setCognome("cognome");
//        userKo3.setNome("nome");
//        userKo3.setEmail("");
//        userKo3.setPassword("password1");
//        userKo3.setGroupDTO(null);
//        assertThrows(UnauthorizedException.class, () -> {
//        	userService.login(userKo3);
//        });
//    }
//
//    @Test
//    void registerTest() {
//    	UserDTO userOk = new UserDTO();
//        userOk.setCognome("cognome");
//        userOk.setNome("nome");
//        userOk.setId(30L);
//        userOk.setEmail("user100@example.com");
//        userOk.setPassword("password1");
//        userOk.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(2L)));
//        userService.register(userOk);
//        
//        UserDTO userKo = new UserDTO();
//        userKo.setCognome("cognome");
//        userKo.setNome("nome");
//        userKo.setId(30L);
//        userKo.setEmail("user1@example.com");
//        userKo.setPassword("password1");
//        userKo.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(2L)));
//        Exception exception = assertThrows(UnauthorizedException.class, () -> {
//            userService.register(userKo);
//
//        });
//        assertEquals("Utente già registrato", exception.getMessage());
//
//        UserDTO userKo2 = new UserDTO();
//        userKo2.setCognome("cognome");
//        userKo2.setNome("nome");
//        userKo2.setId(30L);
//        userKo2.setEmail("user101@example.com");
//        userKo2.setPassword("password100");
//        userKo2.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(1L)));
//        exception = assertThrows(UnauthorizedException.class, () -> {
//            userService.register(userKo2);
//
//        });
//        assertEquals("Non puoi registrarti come admin", exception.getMessage());
//
//        UserDTO userKo3 = new UserDTO();
//        userKo3.setCognome("cognome");
//        userKo3.setNome("nome");
//        userKo3.setId(30L);
//        userKo3.setEmail("user1@example.com");
//        userKo3.setPassword("password100");
//        RoleDTO roleDTO = new RoleDTO();
//        roleDTO.setDescrizione("admin");
//        roleDTO.setAdmin(false);
//        roleDTO.setPriority((long) 1);
//        GroupDTO groupDTO = new GroupDTO();
//        Set<RoleDTO> roleSet = new HashSet<>();
//        roleSet.add(roleDTO);
//        groupDTO.setRoleDTO(roleSet);
//        userKo3.setGroupDTO(groupDTO);
//        exception = assertThrows(UnauthorizedException.class, () -> {
//            userService.register(userKo3);
//
//        });
//        assertEquals("Gruppo non valido", exception.getMessage());
//        
//        
//
//        exception = assertThrows(UnauthorizedException.class, () -> {
//            userService.register(null);
//
//        });
//        assertEquals("Utente non valido", exception.getMessage());
//        
//        UserDTO userKo4 = new UserDTO();
//        exception = assertThrows(UnauthorizedException.class, () -> {
//        	userService.register(userKo4);
//        	
//        });
//        assertEquals("Gruppo non valido", exception.getMessage());
//
//    }
//
//
//    @Test
//    void updateTest() {
//        UserDTO userOk = new UserDTO();
//        userOk.setCognome("cognome");
//        userOk.setNome("nome");
//        userOk.setId(1L);
//        userOk.setEmail("");
//        userOk.setPassword("");
//        userOk.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(1L)));
//        UserDTO originUser = (entityConverter.fromEntity(userDAO.getById(1L)));
//        userService.updateUser(userOk);
//        assertNotEquals(originUser.getCognome(), userOk.getCognome());
//        assertNotEquals(originUser.getNome(), userOk.getNome());
//        assertNotEquals(originUser.getEmail(), userOk.getEmail());
//        assertNotEquals(originUser.getPassword(), userOk.getPassword());
//        assertEquals(originUser.getGroupDTO().getId(), userOk.getGroupDTO().getId());
//
//        UserDTO userKo = new UserDTO();
//        userKo.setCognome("cognome");
//        userKo.setNome("nome");
//        userKo.setId(100L);
//        userKo.setEmail("");
//        userKo.setPassword("");
//        userKo.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(1L)));
//        originUser = (entityConverter.fromEntity(userDAO.getById(1L)));
//        Exception exception = assertThrows(UserMissingException.class, () -> {
//            userService.updateUser(userKo);
//
//        });
//        assertEquals("Utente non trovato", exception.getMessage());
//
//        UserDTO userKo2 = new UserDTO();
//        userKo2.setCognome("cognome");
//        userKo2.setNome("nome");
//        userKo2.setId(1L);
//        userKo2.setEmail("");
//        userKo2.setPassword("");
//        userKo2.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(2L)));
//        exception = assertThrows(UnauthorizedException.class, () -> {
//            userService.updateUser(userKo2);
//
//        });
//        assertEquals("Non puoi modificare un utente con priorità maggiore alla tua", exception.getMessage());
//
//        UserDTO userKo3 = new UserDTO();
//        userKo3.setCognome("cognome");
//        userKo3.setNome("nome");
//        userKo3.setId(1L);
//        userKo3.setEmail("");
//        userKo3.setPassword("");
//        userKo3.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(1L)));
//        exception = assertThrows(UnauthorizedException.class, () -> {
//            userService.updateUser(userKo3);
//
//        });
//        assertEquals("Non puoi cambiare il ruolo di un utente con uno di priorità maggiore al tuo", exception.getMessage());
//        
//
//
//    }
//    
//    @Test 
//    public void confirmRegistration() {
//        User toSave = new User();
//        toSave.setGroup(groupDAO.getById(1L));
//        toSave.setNome("Nomefico");
//        toSave.setCognome("Cognomefico");
//        toSave.setPassword("passwordComplessa");
//        toSave.setEmail("email123455667@example.com");
//        toSave.setActive(false);
//        userDAO.add(toSave);
//    
//        Token token = userService.generateToken(userDAO.getByEmail(toSave.getEmail()));
//        tokenUserDAO.add(token);
//        userService.confirmRegistration((token));
//        toSave=userDAO.getByEmail(toSave.getEmail());
//        assertEquals(toSave.getActive(), true);
//    }
//    
//    @Test
//    public void testMail() {
//    	User user=new User();
//    	user.setEmail("salvatore.scarfone1999@gmail.com");
//    	userService.sendMail(user,userService.generateToken(user));
//    }
//
//    @Test
//    void logoutTest() {
//        Token toDelete = tokenUserDAO.getById(1L);
//        userService.logout(toDelete);
//        assertThrows(UnauthorizedException.class, () -> userService.getAllUsers());
//        Token toDelete2 =new Token();
//        toDelete2.setValue("tokenNonValido");
//        toDelete2.setId(100L);
//        toDelete2.setUser(new User());
//        assertThrows(UnauthorizedException.class, () -> userService.logout(toDelete2));
//    }
//
//    @Test
//    void createUser() {
//        Token t = tokenUserDAO.getById(3L);
//        UserDTO toSave = new UserDTO();
//        toSave.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(1L)));
//        toSave.setNome("Nomefico");
//        toSave.setCognome("Cognomefico");
//        toSave.setPassword("passwordComplessa");
//        toSave.setEmail("email123455667@example.com");
//        userService.createUser(toSave);
//        assertEquals(21, userService.getAllUsers().size());
//    }
//
//    @Test
//    void deleteUserTest() {
//        Token toDelete = tokenUserDAO.getById(1L);
//        UserDTO toBeDeleted = entityConverter.fromEntity(toDelete.getUser());
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        userService.deleteUser(toBeDeleted);
//        var exception = assertThrows(UnauthorizedException.class, () -> userService.login(toBeDeleted));
//        assertEquals("Credenziali non valide", exception.getMessage());
//
//        Token toDelete2 = tokenUserDAO.getById(2L);
//        UserDTO toBeDeleted2 = (entityConverter.fromEntity(userDAO.getById(11L)));
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        userService.deleteUser(toBeDeleted2);
//        
//        Token toDelete3 = tokenUserDAO.getById(3L);
//        UserDTO toBeDeleted3 = (entityConverter.fromEntity(userDAO.getById(2L)));
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        Exception exception2= assertThrows(UnauthorizedException.class, () -> userService.deleteUser(toBeDeleted3));
//        assertEquals("Non puoi cancellare un utente con priorità maggiore alla tua", exception2.getMessage());
//    }
//
//    @Test
//    void updateUser() {
//        Token toUpdate = tokenUserDAO.getById(3L);
//        UserDTO toBeUpdate = entityConverter.fromEntity(toUpdate.getUser());
//        toBeUpdate.setNome("NuovoNome");
//        toBeUpdate.setCognome("NuovoCognome");
//        toBeUpdate.setPassword("NuovaPassword");
//        toBeUpdate.setEmail("nuovaemail@example.com");
//        userService.updateUser(toBeUpdate);
//        assertDoesNotThrow(() -> userService.login(toBeUpdate));
//    }
//
//    @Test
//    void getAllUsers() {
//        Token t = tokenUserDAO.getById(4L);
//        assertEquals(20, userService.getAllUsers().size());
//    }
//
//    @Test
//    void createGroup() {
//        Token t = tokenUserDAO.getById(5L);
//        GroupDTO newGroup = new GroupDTO();
//        Set<RoleDTO> roleSet = new HashSet<>();
//        roleSet.add(entityConverter.fromEntity(roleDAO.getById(1L)));
//        newGroup.setRoleDTO(roleSet);
//        userService.createGroup(newGroup);
//        assertEquals(3, userService.getAllGroup().size());
//    }
//
//    @Test
//    void updateGroup() {
//        Token t = tokenUserDAO.getById(3L);
//        GroupDTO groupDTO = (entityConverter.fromEntity(groupDAO.getById(1L)));
//      
//        userService.updateGroup(groupDTO);
//        var groups = userService.getAllGroup();
//        //assertEquals(groups.get(0).getRoleDTO().getDescrizione(), groups.get(1).getRoleDTO().getDescrizione());
//    }
//
//    @Test
//    void deleteGroup() {
//        Token t = tokenUserDAO.getById(2L);
//        GroupDTO groupDTO = (entityConverter.fromEntity(groupDAO.getById(1L)));
//        int oldSize=userService.getAllGroup().size();
//        userService.deleteGroup(groupDTO);
//        assertEquals(oldSize-1, userService.getAllGroup().size());
//    }
//
//    @Test
//    void createRole() {
//        Token t = tokenUserDAO.getById(6L);
//        RoleDTO toSave = new RoleDTO();
//        toSave.setAdmin(true);
//        toSave.setDescrizione("Nuovo ruolo super fico");
//        toSave.setPriority(5L);
//        userService.createRole(toSave);
//        assertEquals(3, userService.getAllRole().size());
//    }
//
//    @Test
//    void updateRole() {
//        Token t = tokenUserDAO.getById(7L);
//        RoleDTO toUpdate = (entityConverter.fromEntity(roleDAO.getById(1L)));
//        toUpdate.setDescrizione("Nuova descrizione");
//        userService.updateRole(toUpdate);
//        assertEquals("Nuova descrizione", userService.getAllRole().get(0).getDescrizione());
//    }
//
//    @Test
//    void deleteRole() {
//        Token t = tokenUserDAO.getById(9L);
//        RoleDTO toDelete = (entityConverter.fromEntity(roleDAO.getById(2L)));
//        userService.deleteRole(toDelete);
//        assertEquals(1, userService.getAllRole().size());
//    }
//}
