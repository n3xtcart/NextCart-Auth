//package it.nextre.corsojava.service;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import it.nextre.aut.dto.GroupDTO;
//import it.nextre.aut.dto.RoleDTO;
//import it.nextre.aut.dto.UserDTO;
//import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
//import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
//import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
//import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
//import it.nextre.corsojava.dto.TokensJwtDTO;
//import it.nextre.corsojava.entity.Group;
//import it.nextre.corsojava.entity.Role;
//import it.nextre.corsojava.entity.Token;
//import it.nextre.corsojava.entity.User;
//import it.nextre.corsojava.exception.UnauthorizedException;
//import it.nextre.corsojava.utils.EntityConverter;
//
//class ServiceJdbcTest {
//    UserServiceJdbc userService=new UserServiceJdbc(new EntityConverter());
//    GroupJdbcDao groupDAO=GroupJdbcDao.getInstance();
//    RoleJdbcDao roleDAO=RoleJdbcDao.getInstance();
//    TokenJdbcDao tokenUserDAO=TokenJdbcDao.getInstance();
//    UserJdbcDao userDAO=UserJdbcDao.getInstance();
//    long[] rolesId=new long[2];
//    long[] groupId=new long[2];
//    private EntityConverter entityConverter = new EntityConverter();
//
//
//    @BeforeAll
//    void setUp() {
//    	//aggingo due ruoli base
//    	Role adminRole =new Role();
//    	adminRole.setAdmin(true);
//    	adminRole.setDescrizione("admin");
//    	adminRole.setPriority(1L);
//    	rolesId[0] = roleDAO.add(adminRole);
//    	Role userRole =new Role();
//    	userRole.setAdmin(false);
//    	userRole.setDescrizione("user");
//    	userRole.setPriority(2L);
//    	rolesId[1] = roleDAO.add(userRole);
//    	//aggiungo due gruppi base
//    	Group adminGroup = new Group();
//    	Set<Role> hashSet = new HashSet<Role>();
//    	hashSet.add(roleDAO.getById(rolesId[0]));
//    	adminGroup.setRoles(hashSet);
//    	groupId[0] = groupDAO.add(adminGroup);
//    	Group userGroup = new Group();
//    	Set<Role> hashSet2 = new HashSet<Role>();
//    	hashSet2.add(roleDAO.getById(rolesId[1]));
//    	userGroup.setRoles(hashSet2);
//    	groupId[1] = groupDAO.add(userGroup);	
//    	
//    }
//
//
//    @Test
//    void loginTest() {
//        UserDTO userOk = new UserDTO();
//        userOk.setCognome("cognome");
//        userOk.setNome("nome");
//        userOk.setEmail("bo");
//        userOk.setPassword("lalalal");
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
//    	User user=userDAO.getByEmail("user100@example.com");
//    	if(user!=null)   userDAO.delete(user.getId());
//        userOk.setCognome("cognome");
//        userOk.setNome("nome");
//        userOk.setId(30L);
//        userOk.setEmail("user100@example.com");
//        userOk.setPassword("password1");
//        userOk.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(3L)));
//        userService.register(userOk);
//        
//        UserDTO userKo = new UserDTO();
//        userKo.setCognome("cognome");
//        userKo.setNome("nome");
//        userKo.setId(30L);
//        userKo.setEmail("user100@example.com");
//        userKo.setPassword("password1");
//        userKo.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(3L)));
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
//        Set<RoleDTO> roles = new HashSet<>();
//        roles.add(roleDTO);
//        groupDTO.setRoleDTO(roles);
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
//
//
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
//    	Token t = tokenUserDAO.getById(6L);
//        t.setUser(userDAO.getById(t.getUser().getId()));
//        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//        t.getUser().getGroup().setRoles(t.getUser().getGroup().getRoles().stream().map(a->{
//			a=roleDAO.getById(a.getId());
//			return a;
//		}).collect(Collectors.toSet()));
//        UserDTO toSave = new UserDTO();
//        toSave.setGroupDTO(entityConverter.fromEntity(groupDAO.getById(1L)));
//        toSave.getGroupDTO().setRoleDTO((t.getUser().getGroup().getRoles().stream().map(a->{
//			a=roleDAO.getById(a.getId());
//			return entityConverter.fromEntity(a);
//		}).collect(Collectors.toSet())));
//        toSave.setNome("Nomefico");
//        toSave.setCognome("Cognomefico");
//        toSave.setPassword("passwordComplessa");
//        toSave.setEmail("email123455667@example.com");
//        int old=userDAO.getAll().stream().filter(a->a.getActive()).toList().size();
//        userService.createUser(toSave);
//        assertEquals(old+1, userService.getAllUsers().size());
//    }
//
////    @Test
////    void deleteUserTest() {
////    	UserDTO userOk = new UserDTO();
////	User user=userDAO.getByEmail("user100@example.com");
////	if(user!=null)   userDAO.delete(user.getId());
////    userOk.setCognome("cognome");
////    userOk.setNome("nome");
////    userOk.setId(30L);
////    userOk.setEmail("user100@example.com");
////    userOk.setPassword("password1");
////    userOk.setGroupDTO(new GroupDTO(groupDAO.getById(3L)));
////    
////    
////        Token toDelete = tokenUserDAO.getById(1L);
////        Token req = userService.register(userOk);
////        UserDTO toBeDeleted = req.getUserDTO();
////        toBeDeleted.setPassword(toDelete.getUser().getPassword());
////        userService.deleteUser(toBeDeleted, req);
////        var exception = assertThrows(UnauthorizedException.class, () -> userService.login(toBeDeleted));
////        assertEquals("Credenziali non valide", exception.getMessage());
////
////        Token toDelete2 = tokenUserDAO.getById(2L);
////        Token req2 = new Token(toDelete2);
////        UserDTO toBeDeleted2 = new UserDTO(userDAO.getById(11L));
////        toBeDeleted.setPassword(toDelete.getUser().getPassword());
////        userService.deleteUser(toBeDeleted2, req2);
////        
////        Token toDelete3 = tokenUserDAO.getById(3L);
////        Token req3 = new Token(toDelete3);
////        UserDTO toBeDeleted3 = new UserDTO(userDAO.getById(2L));
////        toBeDeleted.setPassword(toDelete.getUser().getPassword());
////        Exception exception2= assertThrows(UnauthorizedException.class, () -> userService.deleteUser(toBeDeleted3, req3));
////        assertEquals("Non puoi cancellare un utente con priorità maggiore alla tua", exception2.getMessage());
////    }
//
//    @Test
//    void updateUser() {Token t = tokenUserDAO.getById(6L);
//    t.setUser(userDAO.getById(t.getUser().getId()));
//    t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//    t.getUser().getGroup().setRoles(t.getUser().getGroup().getRoles().stream().map(a->{
//		a=roleDAO.getById(a.getId());
//		return a;
//	}).collect(java.util.stream.Collectors.toSet()));
//        UserDTO toBeUpdate = entityConverter.fromEntity(userDAO.getById(1L));
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
//    	Token t = tokenUserDAO.getById(6L);
//    t.setUser(userDAO.getById(t.getUser().getId()));
//    t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//    t.getUser().getGroup().setRoles(t.getUser().getGroup().getRoles().stream().map(a->{
//		a=roleDAO.getById(a.getId());
//		return a;
//	}).collect(java.util.stream.Collectors.toSet()));
//
//        int old=userDAO.getAll().stream().filter(a->a.getActive()).toList().size();
//        assertEquals(old, userService.getAllUsers().size());
//    }
//
//    @Test
//    void createGroup() {
//    	Token t = tokenUserDAO.getById(6L);
//        t.setUser(userDAO.getById(t.getUser().getId()));
//        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//        t.getUser().getGroup().setRoles(t.getUser().getGroup().getRoles().stream().map(a->{
//			a=roleDAO.getById(a.getId());
//			return a;
//		}).collect(java.util.stream.Collectors.toSet()));
//        GroupDTO newGroup = new GroupDTO();
//        Set<RoleDTO> roles = new HashSet<>();
//        roles.add(entityConverter.fromEntity(roleDAO.getById(rolesId[0])));
//        newGroup.setRoleDTO(roles);
//        int val =groupDAO.getAll().size();
//        userService.createGroup(newGroup);
//        assertEquals(val+1, userService.getAllGroup().size());
//    }
//
//    @Test
//    void updateGroup() {
//        Token t = tokenUserDAO.getById(6L);
//        t.setUser(userDAO.getById(t.getUser().getId()));
//        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//        t.getUser().getGroup().setRoles(t.getUser().getGroup().getRoles().stream().map(a->{
//        				a=roleDAO.getById(a.getId());
//        							return a;
//        									}).collect(Collectors.toSet()));
//        
//        GroupDTO groupDTO = entityConverter.fromEntity(groupDAO.getById(1L));
//      
//        userService.updateGroup(groupDTO);
//        var groups = userService.getAllGroup();
//        //assertEquals(groups.get(0).getRoleDTO().get(), groups.get(1).getRoleDTO().getDescrizione());
//    }
//
//    @Test
//    void deleteGroup() {
//        Token t = tokenUserDAO.getById(6L);
//        t.setUser(userDAO.getById(t.getUser().getId()));
//        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//        t.getUser().getGroup().setRoles(t.getUser().getGroup().getRoles().stream().map(a->{
//			a=roleDAO.getById(a.getId());
//			return a;
//		}).collect(Collectors.toSet()));Group group=new Group();
//      
//        Set<Role> roles = new HashSet<>();
//        roles.add(roleDAO.getById(rolesId[0]));
//        group.setRoles(roles);
//        Long vaLong=groupDAO.add(group);
//        Group byId = groupDAO.getById(vaLong);
//        int oldSize=userService.getAllGroup().size();
//        userService.deleteGroup(entityConverter.fromEntity(byId));
//        assertEquals(oldSize-1, userService.getAllGroup().size());
//    }
//
//    @Test
//    void createRole() {
//    	Token t = tokenUserDAO.getById(6L);
//    t.setUser(userDAO.getById(t.getUser().getId()));
//        RoleDTO toSave = new RoleDTO();
//        toSave.setAdmin(true);
//        toSave.setDescrizione("Nuovo ruolo super fico");
//        toSave.setPriority(5L);
//        int old =roleDAO.getAll().size();
//        userService.createRole(toSave);
//        assertEquals(old+1, userService.getAllRole().size());
//    }
//
//    @Test
//    void updateRole() {
//    	Token t = tokenUserDAO.getById(6L);
//    t.setUser(userDAO.getById(t.getUser().getId()));
//    t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//    t.getUser().getGroup().setRoles(t.getUser().getGroup().getRoles().stream().map(a->{
//    				a=roleDAO.getById(a.getId());
//    							return a;
//										}).collect(Collectors.toSet()));
//        RoleDTO toUpdate = entityConverter.fromEntity(roleDAO.getById(1L));
//        toUpdate.setDescrizione("Nuova descrizione");
//        userService.updateRole(toUpdate);
//        assertEquals("Nuova descrizione", userService.getAllRole().get(0).getDescrizione());
//    }
//
//    @Test
//    void deleteRole() {
//    	Token t = tokenUserDAO.getById(6L);
//        t.setUser(userDAO.getById(t.getUser().getId()));
//        t.getUser().setGroup(groupDAO.getById(t.getUser().getGroup().getId()));
//        t.getUser().getGroup().setRoles(t.getUser().getGroup().getRoles().stream().map(a->{
//        				a=roleDAO.getById(a.getId());
//        							return a;
//											}).collect(Collectors.toSet()));
//        
//       
//        Role role=new Role();
//        role.setAdmin(true);
//        role.setPriority(2L);
//        role.setDescrizione("");
//        role.setId(3L);
//        Long idLong=roleDAO.add(role);
//        RoleDTO toDelete = entityConverter.fromEntity(roleDAO.getById(idLong));
//        userService.deleteRole(toDelete);
//        assertEquals(roleDAO.getAll().size(), userService.getAllRole().size());
//    }
//}
