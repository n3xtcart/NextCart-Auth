package it.nextre.corsojava.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import it.nextre.corsojava.exception.UnauthorizedException;

public class ServiceTest {
	UserService userService;
	GroupDAO groupDAO;
	RoleDAO roleDAO;
	TokenUserDAO tokenUserDAO;
	UserDAO userDAO;
	
	
	@BeforeEach
	public void setUp() {
		userDAO = new UserDAO();
		groupDAO = new GroupDAO();
		tokenUserDAO = new TokenUserDAO();
		roleDAO = new RoleDAO();
		userService = new UserService(userDAO, tokenUserDAO, groupDAO, roleDAO);
			Role role = new Role(); 
			role.setDescrizione("admin" + 1);
			role.setPriority((long) 1);
			role.setAdmin(true);
			roleDAO.add(role);
		
			Role role2 = new Role(); 
			role2.setDescrizione("admin" + 2);
			role2.setPriority((long) 2);
			role2.setAdmin(false);
			roleDAO.add(role2);
			
	        Group group = new Group();
	        			group.setRole(role);
	        			groupDAO.add(group);
	        			
	        Group group2 = new Group();
		    			group2.setRole(role2);
		    			groupDAO.add(group2);
		    			
		   for(int i = 0; i < 10; i++) {
			   User user = new User();
			   user.setCognome("cognome" + i);
			   user.setNome("meUser" + i);
			   user.setEmail("user" + i + "@example.com");
			   user.setPassword("password" + i);
			   user.setGroup(group);
			   userDAO.add(user);
			   Token token=userService.generateToken(user);
			   tokenUserDAO.add(token);
			   
		   }
		   for(int i = 10; i < 20; i++) {
			   User user = new User();
			   user.setCognome("cognome" + i);
			   user.setNome("meUser" + i);
			   user.setEmail("user" + i + "@example.com");
			   user.setPassword("password" + i);
			   user.setGroup(group2);
			   userDAO.add(user);
			   Token token=userService.generateToken(user);
			   tokenUserDAO.add(token);
			   
		   }
		
		
	}
	
	
	
	@Test
	public void loginTest() {
		UserDTO userOk = new UserDTO();
		userOk.setCognome("cognome");
		userOk.setNome("nome");
		userOk.setEmail("user1@example.com");
		userOk.setPassword("password1");
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
	}
	@Test
	public void registerTest() {
		UserDTO userKo = new UserDTO();
		userKo.setCognome("cognome");
		userKo.setNome("nome");
		userKo.setId(30L);
		userKo.setEmail("user1@example.com");
		userKo.setPassword("password1");
		userKo.setGroupDTO(new GroupDTO(groupDAO.getById(2L)));
		Exception exception= assertThrows(UnauthorizedException.class, () -> {
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
		exception= assertThrows(UnauthorizedException.class, () -> {
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
		exception= assertThrows(UnauthorizedException.class, () -> {
			userService.register(userKo3);
			
		});
		assertEquals("Gruppo non valido", exception.getMessage());
		
	}
	
	
	
	@Test
	public void updateTest() {
		UserDTO userOk= new UserDTO();
		userOk.setCognome("cognome");
		userOk.setNome("nome");
		userOk.setId(1L);
		userOk.setEmail("");
		userOk.setPassword("");
		userOk.setGroupDTO(new GroupDTO( groupDAO.getById(1L)));
		TokenDTO tokenOk =new TokenDTO( tokenUserDAO.getTokenByIdUser(1L).getFirst());
		UserDTO originUser=new UserDTO( userDAO.getById(1L));
		userService.updateUser(userOk, tokenOk);
		assertNotEquals(originUser.getCognome(), userOk.getCognome());
		assertNotEquals(originUser.getNome(), userOk.getNome());
		assertNotEquals(originUser.getEmail(), userOk.getEmail());
		assertNotEquals(originUser.getPassword(), userOk.getPassword());
		assertEquals(originUser.getGroupDTO().getId(), userOk.getGroupDTO().getId());
		
		UserDTO userKo= new UserDTO();
		userKo.setCognome("cognome");
		userKo.setNome("nome");
		userKo.setId(100L);
		userKo.setEmail("");
		userKo.setPassword("");
		userKo.setGroupDTO(new GroupDTO( groupDAO.getById(1L)));
		originUser=new UserDTO( userDAO.getById(1L));
		Exception exception= assertThrows(UnauthorizedException.class, () -> {
			TokenDTO tokenOk2 =new TokenDTO( tokenUserDAO.getTokenByIdUser(1L).getFirst());
			userService.updateUser(userKo, tokenOk2);
			
		});
		assertEquals("Utente non trovato", exception.getMessage());
		
		UserDTO userKo2= new UserDTO();
		userKo2.setCognome("cognome");
		userKo2.setNome("nome");
		userKo2.setId(1L);
		userKo2.setEmail("");
		userKo2.setPassword("");
		userKo2.setGroupDTO(new GroupDTO( groupDAO.getById(2L)));
		exception= assertThrows(UnauthorizedException.class, () -> {
			TokenDTO tokenOk2 =new TokenDTO( tokenUserDAO.getTokenByIdUser(11L).getFirst());
			userService.updateUser(userKo2, tokenOk2);
			
		});
		assertEquals("Non puoi cambiare il ruolo di un utente se il tuo è più basso", exception.getMessage());
		
		UserDTO userKo3= new UserDTO();
		userKo3.setCognome("cognome");
		userKo3.setNome("nome");
		userKo3.setId(1L);
		userKo3.setEmail("");
		userKo3.setPassword("");
		userKo3.setGroupDTO(new GroupDTO( groupDAO.getById(1L)));
		exception= assertThrows(UnauthorizedException.class, () -> {
			TokenDTO tokenOk2 =new TokenDTO( tokenUserDAO.getTokenByIdUser(11L).getFirst());
			userService.updateUser(userKo3, tokenOk2);
			
		});
		assertEquals("Non puoi cambiare il ruolo di un utente con uno di priorità maggiore al tuo", exception.getMessage());
		
		
		
	}

}
