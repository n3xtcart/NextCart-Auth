package it.nextre.corsojava.service;

import it.nextre.corsojava.dao.GroupDAO;
import it.nextre.corsojava.dao.RoleDAO;
import it.nextre.corsojava.dao.TokenUserDAO;
import it.nextre.corsojava.dao.UserDAO;
import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.RoleDTO;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.UnauthorizedException;
import it.nextre.corsojava.exception.UserMissingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class ServiceJdbcTest {
    UserServiceJdbc userService=UserServiceJdbc.getInstance();
    GroupJdbcDao groupDAO=GroupJdbcDao.getInstance();
    RoleJdbcDao roleDAO=RoleJdbcDao.getInstance();
    TokenJdbcDao tokenUserDAO=TokenJdbcDao.getInstance();
    UserJdbcDao userDAO=UserJdbcDao.getInstance();


    @BeforeEach
    void setUp() {}


    @Test
    void loginTest() {
        UserDTO userOk = new UserDTO();
        userOk.setCognome("cognome");
        userOk.setNome("nome");
        userOk.setEmail("bo");
        userOk.setPassword("lalalal");
        userOk.setGroupDTO(null);
        TokenDTO loginOk = userService.login(userOk);
        assertNotNull(loginOk);

        UserDTO userKo = new UserDTO();
        userKo.setCognome("cognome");
        userKo.setNome("nome");
        userKo.setEmail("user100@example.com");
        userKo.setPassword("password100");
        userKo.setGroupDTO(null);
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(userKo);
        });
        
        UserDTO userKo2 = new UserDTO();
        userKo2.setCognome("cognome");
        userKo2.setNome("nome");
        userKo2.setEmail("user1@example.com");
        userKo2.setPassword("");
        userKo2.setGroupDTO(null);
        assertThrows(UnauthorizedException.class, () -> {
        	userService.login(userKo2);
        });
        
        UserDTO userKo3 = new UserDTO();
        userKo3.setCognome("cognome");
        userKo3.setNome("nome");
        userKo3.setEmail("");
        userKo3.setPassword("password1");
        userKo3.setGroupDTO(null);
        assertThrows(UnauthorizedException.class, () -> {
        	userService.login(userKo3);
        });
    }

    @Test
    void registerTest() {
    	UserDTO userOk = new UserDTO();
    	User user=userDAO.getByEmail("user100@example.com");
    	if(user!=null)   userDAO.delete(user.getId());
        userOk.setCognome("cognome");
        userOk.setNome("nome");
        userOk.setId(30L);
        userOk.setEmail("user100@example.com");
        userOk.setPassword("password1");
        userOk.setGroupDTO(new GroupDTO(groupDAO.getById(3L)));
        userService.register(userOk);
        
        UserDTO userKo = new UserDTO();
        userKo.setCognome("cognome");
        userKo.setNome("nome");
        userKo.setId(30L);
        userKo.setEmail("user100@example.com");
        userKo.setPassword("password1");
        userKo.setGroupDTO(new GroupDTO(groupDAO.getById(3L)));
        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userService.register(userKo);

        });
        assertEquals("Utente già registrato", exception.getMessage());

        UserDTO userKo2 = new UserDTO();
        userKo2.setCognome("cognome");
        userKo2.setNome("nome");
        userKo2.setId(30L);
        userKo2.setEmail("user101@example.com");
        userKo2.setPassword("password100");
        userKo2.setGroupDTO(new GroupDTO(groupDAO.getById(1L)));
        exception = assertThrows(UnauthorizedException.class, () -> {
            userService.register(userKo2);

        });
        assertEquals("Non puoi registrarti come admin", exception.getMessage());

        UserDTO userKo3 = new UserDTO();
        userKo3.setCognome("cognome");
        userKo3.setNome("nome");
        userKo3.setId(30L);
        userKo3.setEmail("user1@example.com");
        userKo3.setPassword("password100");
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setDescrizione("admin");
        roleDTO.setAdmin(false);
        roleDTO.setPriority((long) 1);
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setRoleDTO(roleDTO);
        userKo3.setGroupDTO(groupDTO);
        exception = assertThrows(UnauthorizedException.class, () -> {
            userService.register(userKo3);

        });
        assertEquals("Gruppo non valido", exception.getMessage());
        
        

        exception = assertThrows(UnauthorizedException.class, () -> {
            userService.register(null);

        });
        assertEquals("Utente non valido", exception.getMessage());
        
        UserDTO userKo4 = new UserDTO();
        exception = assertThrows(UnauthorizedException.class, () -> {
        	userService.register(userKo4);
        	
        });
        assertEquals("Gruppo non valido", exception.getMessage());

    }




    @Test
    void logoutTest() {
    	Token token=new Token();
    	token.setValue("dffdfd");
    	User user=new User();
    	user.setId(1L);
    	token.setUser(user);
    	token.setUser(userDAO.getById(token.getUser().getId()));
    	token.getUser().setGroup(groupDAO.getById(token.getUser().getGroup().getId()));
    	token.getUser().getGroup().setRole(roleDAO.getById(token.getUser().getGroup().getRole().getId()));
    	
    	Long val= tokenUserDAO.add(token);
        Token t = tokenUserDAO.getById(val);
        t.setUser(userDAO.getById(t.getUser().getId()));
        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
        t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
       
        TokenDTO req = new TokenDTO(t);
        userService.logout(req);
        assertThrows(UnauthorizedException.class, () -> userService.getAllUsers(req));
        Token toDelete2 =new Token();
        toDelete2.setValue("tokenNonValido");
        toDelete2.setId(100L);
        toDelete2.setUser(new User());
        TokenDTO req2 = new TokenDTO(toDelete2);
        assertThrows(UnauthorizedException.class, () -> userService.logout(req2));
    }

    @Test
    void createUser() {
    	Token t = tokenUserDAO.getById(6L);
        t.setUser(userDAO.getById(t.getUser().getId()));
        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
        t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
        
        TokenDTO req = new TokenDTO(t);
        UserDTO toSave = new UserDTO();
        toSave.setGroupDTO(new GroupDTO(groupDAO.getById(1L)));
        toSave.getGroupDTO().setRoleDTO(new RoleDTO(roleDAO.getById(toSave.getGroupDTO().getRoleDTO().getId())));
        toSave.setNome("Nomefico");
        toSave.setCognome("Cognomefico");
        toSave.setPassword("passwordComplessa");
        toSave.setEmail("email123455667@example.com");
        
        int old=userDAO.getAll().size();
        userService.createUser(toSave, req);
        assertEquals(old+1, userService.getAllUsers(req).size());
    }

//    @Test
//    void deleteUserTest() {
//    	UserDTO userOk = new UserDTO();
//	User user=userDAO.getByEmail("user100@example.com");
//	if(user!=null)   userDAO.delete(user.getId());
//    userOk.setCognome("cognome");
//    userOk.setNome("nome");
//    userOk.setId(30L);
//    userOk.setEmail("user100@example.com");
//    userOk.setPassword("password1");
//    userOk.setGroupDTO(new GroupDTO(groupDAO.getById(3L)));
//    
//    
//        Token toDelete = tokenUserDAO.getById(1L);
//        TokenDTO req = userService.register(userOk);
//        UserDTO toBeDeleted = req.getUserDTO();
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        userService.deleteUser(toBeDeleted, req);
//        var exception = assertThrows(UnauthorizedException.class, () -> userService.login(toBeDeleted));
//        assertEquals("Credenziali non valide", exception.getMessage());
//
//        Token toDelete2 = tokenUserDAO.getById(2L);
//        TokenDTO req2 = new TokenDTO(toDelete2);
//        UserDTO toBeDeleted2 = new UserDTO(userDAO.getById(11L));
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        userService.deleteUser(toBeDeleted2, req2);
//        
//        Token toDelete3 = tokenUserDAO.getById(3L);
//        TokenDTO req3 = new TokenDTO(toDelete3);
//        UserDTO toBeDeleted3 = new UserDTO(userDAO.getById(2L));
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        Exception exception2= assertThrows(UnauthorizedException.class, () -> userService.deleteUser(toBeDeleted3, req3));
//        assertEquals("Non puoi cancellare un utente con priorità maggiore alla tua", exception2.getMessage());
//    }

    @Test
    void updateUser() {Token t = tokenUserDAO.getById(6L);
    t.setUser(userDAO.getById(t.getUser().getId()));
    t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
    t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
    
        TokenDTO req = new TokenDTO(t);
        UserDTO toBeUpdate = new UserDTO(t.getUser());
        toBeUpdate.setNome("NuovoNome");
        toBeUpdate.setCognome("NuovoCognome");
        toBeUpdate.setPassword("NuovaPassword");
        toBeUpdate.setEmail("nuovaemail@example.com");
        userService.updateUser(toBeUpdate, req);
        assertDoesNotThrow(() -> userService.login(toBeUpdate));
    }

    @Test
    void getAllUsers() {
    	Token t = tokenUserDAO.getById(6L);
    t.setUser(userDAO.getById(t.getUser().getId()));
    t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
    t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
   
        TokenDTO req = new TokenDTO(t);
        int old=userDAO.getAll().size();
        assertEquals(old, userService.getAllUsers(req).size());
    }

    @Test
    void createGroup() {
    	Token t = tokenUserDAO.getById(6L);
        t.setUser(userDAO.getById(t.getUser().getId()));
        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
        t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
       
        TokenDTO req = new TokenDTO(t);
        GroupDTO newGroup = new GroupDTO();
        newGroup.setRoleDTO(new RoleDTO(roleDAO.getById(1L)));
        int val =groupDAO.getAll().size();
        userService.createGroup(newGroup, req);
        assertEquals(val+1, userService.getAllGroup(req).size());
    }

    @Test
    void updateGroup() {
        Token t = tokenUserDAO.getById(6L);
        t.setUser(userDAO.getById(t.getUser().getId()));
        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
        t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
       
        TokenDTO req = new TokenDTO(t);
        GroupDTO groupDTO = new GroupDTO(groupDAO.getById(2L));
        groupDTO.setRoleDTO(new RoleDTO(roleDAO.getById(2L)));
        userService.updateGroup(groupDTO, req);
        var groups = userService.getAllGroup(req);
        assertEquals(groups.get(0).getRoleDTO().getDescrizione(), groups.get(1).getRoleDTO().getDescrizione());
    }

    @Test
    void deleteGroup() {
        Token t = tokenUserDAO.getById(6L);
        t.setUser(userDAO.getById(t.getUser().getId()));
        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
        t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
        Group group=new Group();
        Role role =new Role();
        role.setId(1L);
        group.setRole(role);
        Long vaLong=groupDAO.add(group);
        TokenDTO req = new TokenDTO(t);
        Group byId = groupDAO.getById(vaLong);
        byId.setRole(roleDAO.getById(byId.getRole().getId()));
        GroupDTO groupDTO = new GroupDTO(byId);
        
        int oldSize=userService.getAllGroup(req).size();
        userService.deleteGroup(groupDTO, req);
        assertEquals(oldSize-1, userService.getAllGroup(req).size());
    }

    @Test
    void createRole() {
    	Token t = tokenUserDAO.getById(6L);
    t.setUser(userDAO.getById(t.getUser().getId()));
    t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
    t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
    
        TokenDTO req = new TokenDTO(t);
        RoleDTO toSave = new RoleDTO();
        toSave.setAdmin(true);
        toSave.setDescrizione("Nuovo ruolo super fico");
        toSave.setPriority(5L);
        int old =roleDAO.getAll().size();
        userService.createRole(toSave, req);
        assertEquals(old+1, userService.getAllRole(req).size());
    }

    @Test
    void updateRole() {
    	Token t = tokenUserDAO.getById(6L);
    t.setUser(userDAO.getById(t.getUser().getId()));
    t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
    t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
    
        TokenDTO req = new TokenDTO(t);
        RoleDTO toUpdate = new RoleDTO(roleDAO.getById(1L));
        toUpdate.setDescrizione("Nuova descrizione");
        userService.updateRole(toUpdate, req);
        assertEquals("Nuova descrizione", userService.getAllRole(req).get(0).getDescrizione());
    }

    @Test
    void deleteRole() {
    	Token t = tokenUserDAO.getById(6L);
        t.setUser(userDAO.getById(t.getUser().getId()));
        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
        t.getUser().getGroup().setRole(roleDAO.getById(t.getUser().getGroup().getRole().getId()));
        
        TokenDTO req = new TokenDTO(t);
        Role role=new Role();
        role.setAdmin(true);
        role.setPriority(2L);
        role.setDescrizione("");
        role.setId(3L);
        Long idLong=roleDAO.add(role);
        RoleDTO toDelete = new RoleDTO(roleDAO.getById(idLong));
        userService.deleteRole(toDelete, req);
        assertEquals(roleDAO.getAll().size(), userService.getAllRole(req).size());
    }
}
