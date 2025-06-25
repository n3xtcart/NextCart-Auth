package it.nextre.corsojava.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.security.UnauthorizedException;
import io.quarkus.test.junit.QuarkusTest;
import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.dto.LoginInfo;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.service.group.GroupServiceJdbc;
import it.nextre.corsojava.service.role.RoleServiceJdbc;
import it.nextre.corsojava.service.user.UserServiceJdbc;
import it.nextre.corsojava.service.user.admin.UserAdminServiceJdbc;
import it.nextre.corsojava.utils.EntityConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
@TestInstance(TestInstance.Lifecycle.PER_CLASS) 


@QuarkusTest
class ServiceJdbcTest {
	@Inject
	@Named("defaultJdbc")
    UserServiceJdbc userService;

	@Inject
    UserAdminServiceJdbc userAdminService;

	@Inject
    GroupServiceJdbc groupService;

	@Inject
    RoleServiceJdbc roleService;

	@Inject
    GroupJdbcDao groupDAO;

	@Inject
    RoleJdbcDao roleDAO;

	@Inject
    TokenJdbcDao tokenUserDAO;

	@Inject
    UserJdbcDao userDAO;
    long roleAdmin;
    long roleBase;
    long groupAdmin;
    long groupBase;
    long userAdmin;
    long userBase;
    UserDTO userDTOAdmin;
    private EntityConverter entityConverter = new EntityConverter();


    @BeforeAll
    void  setUp() {
    	//aggingo due ruoli base
    	Role adminRole =new Role();
    	adminRole.setAdmin(true);
    	adminRole.setDescrizione("admin");
    	adminRole.setPriority(1L);
    	roleAdmin = roleDAO.add(adminRole);
    	Role userRole =new Role();
    	userRole.setAdmin(false);
    	userRole.setDescrizione("user");
    	userRole.setPriority(2L);
    	roleBase = roleDAO.add(userRole);
    	//aggiungo due gruppi base
    	Group adminGroup = new Group();
    	Set<Role> hashSet = new HashSet<Role>();
    	hashSet.add(roleDAO.getById(roleAdmin));
    	adminGroup.setRoles(hashSet);
    	groupAdmin = groupDAO.add(adminGroup);
    	Group userGroup = new Group();
    	Set<Role> hashSet2 = new HashSet<Role>();
    	hashSet2.add(roleDAO.getById(roleBase));
    	userGroup.setRoles(hashSet2);
    	groupBase = groupDAO.add(userGroup);
    	User user = new User();
    	user.setCognome("cognome1");
    	user.setNome("nome1");
    	user.setActive(true);
    	user.setEmail("email");
    	user.setPassword("password1");
    	user.setGroup(groupDAO.getById(groupAdmin));
		Set< Role> set=new HashSet<Role>();
		set.add(roleDAO.getById(roleAdmin));
		user.setRoles(set);
    	userAdmin = userDAO.add(user);
    	userDTOAdmin = entityConverter.fromEntity(user);
    	User user2 = new User();
    	user2.setCognome("cognome2");
    	user2.setNome("nome2");
    	user2.setActive(true);
    	user.setGroup(groupDAO.getById(groupBase));
		Set< Role> set2=new HashSet<Role>();
		set.add(roleDAO.getById(roleBase));
		user.setRoles(set2);
    	userBase = userDAO.add(user2);
    	
    }


    @Test
    void loginTest() {
    	LoginInfo loginInfo = new LoginInfo();
    	loginInfo.setEmail("email");
    	loginInfo.setPassword("password1");
        TokenJwtDTO loginOk = userService.login(loginInfo);
        assertNotNull(loginOk);

        loginInfo.setPassword("2");
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginInfo);
        });
        loginInfo.setPassword("");
        assertThrows(UnauthorizedException.class, () -> {
        	userService.login(loginInfo);
        });
        
        loginInfo.setEmail("");
        assertThrows(UnauthorizedException.class, () -> {
        	userService.login(loginInfo);
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
        userOk.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(3L)));
        userService.register(userOk);
        
        UserDTO userKo = new UserDTO();
        userKo.setCognome("cognome");
        userKo.setNome("nome");
        userKo.setId(30L);
        userKo.setEmail("user100@example.com");
        userKo.setPassword("password1");
        userKo.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(3L)));
        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userService.register(userKo);

        });
        assertEquals("Utente già registrato", exception.getMessage());

        UserDTO userKo2 = new UserDTO();
        userKo2.setCognome("cognome");
        userKo2.setNome("nome");
        userKo2.setId(30L);
        userKo2.setEmail("user500@example.com");
        userKo2.setPassword("password100");
        userKo2.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(groupAdmin)));
        exception = assertThrows(it.nextre.corsojava.exception.UnauthorizedException.class, () -> {
            userService.register(userKo2);

        });
        assertEquals("Non puoi registrarti come admin", exception.getMessage());
     
        

        exception = assertThrows(it.nextre.corsojava.exception.UnauthorizedException.class, () -> {
            userService.register(null);

        });
        assertEquals("Utente non valido", exception.getMessage());
        
       
    }


//TODO:da rivedere dopo aver rifatto il logOut

//    @Test
//    void logoutTest() {
//    	Token token=new Token();
//    	token.setValue("dffdfd");
//    	User user=new User();
//    	user.setId(1L);
//    	token.setUser(user);
//    	token.setUser(userDAO.getById(token.getUser().getId()));
//    	token.getUser().setGroup(groupDAO.getById(token.getUser().getGroup().getId()));
//    	token.getUser().getGroup().setRoles(token.getUser().getGroup().getRoles().stream().map(a->{
//			a=roleDAO.getById(a.getId());
//			return a;
//		}).collect(java.util.stream.Collectors.toSet()));
//    	
//    	Long val= tokenUserDAO.add(token);
//        Token t = tokenUserDAO.getById(val);
//        t.setUser(userDAO.getById(t.getUser().getId()));
//        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//        t.getUser().getGroup().setRoles(token.getUser().getGroup().getRoles().stream().map(a->{
//			a=roleDAO.getById(a.getId());
//			return a;
//		}).collect(java.util.stream.Collectors.toSet()));
//        userService.logout(t);
//        assertThrows(UnauthorizedException.class, () -> userAdminService.getAllUsers(userDTOAdmin));
//        Token toDelete2 =new Token();
//        toDelete2.setValue("tokenNonValido");
//        toDelete2.setId(100L);
//        toDelete2.setUser(new User());
//        assertThrows(UnauthorizedException.class, () -> userService.logout(toDelete2));
//    }

    @Test
    void createUser() {
        UserDTO toSave = new UserDTO();
        Group byId = groupDAO.getById(groupBase);
        toSave.setGroupDTO(entityConverter.fromEntity(byId));
        toSave.setRuoli(toSave.getGroupDTO().getRoleDTO());
        toSave.setNome("Nomefico");
        toSave.setCognome("Cognomefico");
        toSave.setPassword("passwordComplessa");
        toSave.setEmail("email125343d35r5y56673d@example.com");
        int old=userAdminService.getAllUsers(userDTOAdmin).size();
        userAdminService.createUser(toSave,userDTOAdmin);
        assertEquals(old+1, userAdminService.getAllUsers(userDTOAdmin).size());
        userAdminService.delete(toSave, userDTOAdmin);
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
//        Token req = userService.register(userOk);
//        UserDTO toBeDeleted = req.getUserDTO();
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        userService.deleteUser(toBeDeleted, req);
//        var exception = assertThrows(UnauthorizedException.class, () -> userService.login(toBeDeleted));
//        assertEquals("Credenziali non valide", exception.getMessage());
//
//        Token toDelete2 = tokenUserDAO.getById(2L);
//        Token req2 = new Token(toDelete2);
//        UserDTO toBeDeleted2 = new UserDTO(userDAO.getById(11L));
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        userService.deleteUser(toBeDeleted2, req2);
//        
//        Token toDelete3 = tokenUserDAO.getById(3L);
//        Token req3 = new Token(toDelete3);
//        UserDTO toBeDeleted3 = new UserDTO(userDAO.getById(2L));
//        toBeDeleted.setPassword(toDelete.getUser().getPassword());
//        Exception exception2= assertThrows(UnauthorizedException.class, () -> userService.deleteUser(toBeDeleted3, req3));
//        assertEquals("Non puoi cancellare un utente con priorità maggiore alla tua", exception2.getMessage());
//    }

    @Test
    void updateUser() {
    			UserDTO toUpdate = entityConverter.fromEntity(userDAO.getById(userBase));
    					toUpdate.setNome("NuovoNome");
    							toUpdate.setCognome("NuovoCognome");
    		
    							userService.update(toUpdate, toUpdate);
    							assertEquals("NuovoNome", entityConverter.fromEntity(userDAO.getById(userBase)).getNome());
    }

    @Test
    void getAllUsers() {
        int old=userAdminService.getAllUsers(userDTOAdmin).size();
        assertEquals(old, userAdminService.getAllUsers(userDTOAdmin).size());
    }

    @Test
    void createGroup() {
        GroupDTO newGroup = new GroupDTO();
        Set<RoleDTO> roles = new HashSet<>();
        roles.add(entityConverter.fromEntity(roleDAO.getById(roleBase)));
        newGroup.setRoleDTO(roles);
        int val =groupService.getAllGroups(userDTOAdmin).size();
        groupService.create(newGroup,userDTOAdmin);
        assertEquals(val+1, groupService.getAllGroups(userDTOAdmin).size());
        groupDAO.delete(newGroup.getId());
    }

    @Test
    void updateGroup() {
        
        GroupDTO groupDTO = entityConverter.fromEntity(groupDAO.getById(groupBase));
      
        groupService.update(groupDTO,userDTOAdmin);
        var groups = groupService.getAllGroups(userDTOAdmin);
        //assertEquals(groups.get(0).getRoleDTO().get(), groups.get(1).getRoleDTO().getDescrizione());
    }

    @Test
    void deleteGroup() {
        Group group=new Group();
      
        Set<Role> roles = new HashSet<>();
        roles.add(roleDAO.getById(roleBase));
        group.setRoles(roles);
        Long vaLong=groupDAO.add(group);
        Group byId = groupDAO.getById(vaLong);
        int oldSize=groupService.getAllGroups(userDTOAdmin).size();
        groupService.delete(entityConverter.fromEntity(byId),userDTOAdmin);
        assertEquals(oldSize-1, groupService.getAllGroups(userDTOAdmin).size());
    }

    @Test
    void createRole() {
    	RoleDTO roleDTO=new RoleDTO();
    	roleDTO.setAdmin(false);
    	roleDTO.setPriority(1L);
    	
        int old =roleService.getAllRoles(userDTOAdmin).size();
        roleService.create(roleDTO,userDTOAdmin);
        assertEquals(old+1, roleService.getAllRoles(userDTOAdmin).size());
        roleDAO.delete(roleDTO.getId());
    }

    @Test
    void updateRole() {
        RoleDTO toUpdate = entityConverter.fromEntity(roleDAO.getById(roleBase));
        toUpdate.setDescrizione("Nuova descrizione");
        roleService.update(toUpdate,userDTOAdmin);
        assertEquals("Nuova descrizione",  entityConverter.fromEntity(roleDAO.getById(roleBase)).getDescrizione());
    }

    @Test
    void deleteRole() {
        Role role=new Role();
        role.setAdmin(true);
        role.setPriority(1L);
        role.setDescrizione("");
        role.setId(3L);
        int size = roleService.getAllRoles(userDTOAdmin).size();
        Long idLong=roleDAO.add(role);
        RoleDTO toDelete = entityConverter.fromEntity(roleDAO.getById(idLong));
        roleService.delete(toDelete,userDTOAdmin);
        assertEquals(size, roleService.getAllRoles(userDTOAdmin).size());
    }
}
