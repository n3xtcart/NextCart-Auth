package it.nextre.corsojava.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

public class UserService implements UserServiceInterface {
	private final UserDAO userDAO;
	private final TokenUserDAO tokenUserDAO;
	private final GroupDAO groupDAO;
	private final RoleDAO roleDAO;

	public UserService(UserDAO userDAO, TokenUserDAO tokenUserDAO, GroupDAO groupDAO, RoleDAO roleDAO) {
		this.userDAO = userDAO;
		this.tokenUserDAO = tokenUserDAO;
		this.groupDAO = groupDAO;
		this.roleDAO = roleDAO;
	}

	@Override
	public TokenDTO login(UserDTO user) {
		TokenDTO tokenDTO = null;
		if (user.getEmail().isBlank() || user.getPassword().isBlank()) {
			throw new UnauthorizedException("Email o password non validi");
		}
		Optional<User> byEmailPassword = userDAO.findByEmailPassword(user.getEmail(), user.getPassword());
		if (byEmailPassword.isPresent()) {
			User u = byEmailPassword.get();
			Token token = generateToken(u);
			tokenUserDAO.add(token);
			tokenDTO = new TokenDTO(token);
		} else {
			throw new UnauthorizedException("Credenziali non valide");
		}
		return tokenDTO;
	}

	public void logout(TokenDTO token) {
		Token t = tokenUserDAO.getTokenByValue(token.getValue());
		if (t != null) {
			tokenUserDAO.delete(t.getId());
		}
    }

	@Override
	public TokenDTO register(UserDTO user) {
		TokenDTO tokenDTO = null;
		if(user== null ) {
			throw new UnauthorizedException("Utente non valido");
		}
		if(user.getGroupDTO() == null ||  groupDAO.getById(user.getGroupDTO().getId()) == null ) {
			throw new UnauthorizedException("Gruppo non valido");
		}
		Group group = groupDAO.getById(user.getGroupDTO().getId());
		
		if(group.getRole().isAdmin()== true) {
			throw new UnauthorizedException("Non puoi registrarti come admin");
		}
		if (userDAO.getByEmail(user.getEmail()) != null) {
			throw new UnauthorizedException("Utente già registrato");
		}
		userDAO.add(new User(user));
		Token token = generateToken(userDAO.getByEmail(user.getEmail()));
		tokenUserDAO.add(token);
		tokenDTO = new TokenDTO(token);

		return tokenDTO;
	}

	@Override
	public boolean checkToken(TokenDTO token) {
		return tokenUserDAO.getAll().stream().anyMatch(t -> t.getValue().equals(token.getValue()));
	}

	@Override
    public void updateUser(UserDTO user, TokenDTO token) {
		if(user == null) throw new UnauthorizedException("Utente non valido");
		if(user.getGroupDTO() == null) throw new UnauthorizedException("Gruppo non valido");
		if(user.getGroupDTO().getRoleDTO() == null) throw new UnauthorizedException("Ruolo non valido");
		if(checkToken(token) == false) throw new UnauthorizedException("Token non valido");
		User u = userDAO.getById(user.getId());
		if(u == null) throw new UnauthorizedException("Utente non trovato");
		Token t = tokenUserDAO.getTokenByValue(token.getValue());
		if(t == null) throw new UnauthorizedException("Token non trovato");
		if(t.getUser().getGroup().getRole().compareTo(new Role(user.getGroupDTO().getRoleDTO())) < 0) {
			throw new UnauthorizedException("Non puoi cambiare il ruolo di un utente con uno di priorità maggiore al tuo");
		}
    	if(t.getUser().getId().equals(u.getId())) {
    		if(t.getUser().getGroup().getRole().compareTo(u.getGroup().getRole()) < 0) {
				throw new UnauthorizedException("Non puoi cambiare il tuo ruolo con uno di priorità maggiore");
			}
			u.setNome(user.getNome());
			u.setCognome(user.getCognome());
			u.setEmail(user.getEmail());
			u.setPassword(user.getPassword());
			u.setGroup(new Group(user.getGroupDTO()));
			userDAO.update(user.getId(), u);
    	}else if(t.getUser().getGroup().getRole().compareTo(u.getGroup().getRole()) > 0) {
    		u.setNome(user.getNome());
			u.setCognome(user.getCognome());
			u.setEmail(user.getEmail());
			u.setPassword(user.getPassword());
			u.setGroup(new Group(user.getGroupDTO()));
			userDAO.update(user.getId(), u);
    	}else {
			throw new UnauthorizedException("Non puoi cambiare il ruolo di un utente se il tuo è più basso");
		}
    }

	@Override
	public void deleteUser(UserDTO user, TokenDTO token) {
		if(user == null) throw new UnauthorizedException("Utente non valido");
		if(user.getGroupDTO() == null) throw new UnauthorizedException("Gruppo non valido");
		if(user.getGroupDTO().getRoleDTO() == null) throw new UnauthorizedException("Ruolo non valido");

		User u = userDAO.getById(user.getId());
		if(u == null) throw new UnauthorizedException("Utente non trovato");
		Token t = tokenUserDAO.getTokenByValue(token.getValue());
    	if(t.getUser().getId().equals(user.getId())) {
			userDAO.delete(user.getId());
			tokenUserDAO.getTokenByIdUser(user.getId()).forEach(tok -> tokenUserDAO.delete(tok.getId()));
    	}else if(t.getUser().getGroup().getRole().compareTo(u.getGroup().getRole()) > 0) {
    		userDAO.delete(user.getId());
			tokenUserDAO.getTokenByIdUser(user.getId()).forEach(tok -> tokenUserDAO.delete(tok.getId()));
    	}else {
    		throw new UnauthorizedException("Non puoi cancellare un utente con uno di priorità maggiore");
    	}
    }

	@Override
	public void createUser(UserDTO user, TokenDTO token) {
		RoleDTO roleDTO = token.getUserDTO().getGroupDTO().getRoleDTO();
		if (!roleDTO.getAdmin() || roleDTO.getPriority() < user.getGroupDTO().getRoleDTO().getPriority())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		User toSave = new User(user);

		Group toPut = groupDAO.getById(user.getGroupDTO().getId());

		if (toPut == null)
			throw new GroupMissingException("Il gruppo di appartenenza non esiste");

		toSave.setGroup(toPut);
		userDAO.add(toSave);
	}

	@Override
	public List<UserDTO> getAllUsers(TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		return userDAO.getAll().stream().map(user -> {
			UserDTO dto = new UserDTO(user);
			dto.setPassword(user.getPassword());
			return dto;
		}).toList();
	}

	@Override
	public void createGroup(GroupDTO group, TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		Group toSave = new Group(group);
		groupDAO.add(toSave);
	}

	@Override
	public void updateGroup(GroupDTO group, TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		Group g = groupDAO.getById(group.getId());
		if (g == null)
			throw new GroupMissingException("Impossibile modificare un gruppo non presente");
		Group group2 = new Group(group);
		g.setRole(group2.getRole());
		groupDAO.update(group.getId(), g);
	}

	@Override
	public void deleteGroup(GroupDTO group, TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		if (groupDAO.getById(group.getId()) == null)
			throw new GroupMissingException("Impossibile cancellare un gruppo non presente");
		groupDAO.delete(group.getId());
	}

	@Override
	public List<GroupDTO> getAllGroup(TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		return groupDAO.getAll().stream().map(group -> {
			GroupDTO dto = new GroupDTO(group);
			return dto;
		}).toList();
	}

	@Override
	public void createRole(RoleDTO roleDTO, TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		Role toSave = new Role(roleDTO);
		roleDAO.add(toSave);
	}

	@Override
	public void updateRole(RoleDTO roleDTO, TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		Role role = new Role(roleDTO);
		roleDAO.update(roleDTO.getId(), role);
	}

	@Override
	public void deleteRole(RoleDTO roleDTO, TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		Role r = roleDAO.getById(roleDTO.getId());
		if (r == null)
			throw new RoleMissingException("Ruolo richiesto da cancellare non presente");
		roleDAO.delete(roleDTO.getId());
	}

	@Override
	public List<RoleDTO> getAllRole(TokenDTO token) {
		if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
			throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
		return roleDAO.getAll().stream().map(role -> {
			RoleDTO dto = new RoleDTO(role);
			return dto;
		}).toList();
	}

	public Token generateToken(User user) {
		Set<String> allTokens = tokenUserDAO.getAll().stream().map(Token::getValue).collect(Collectors.toSet());
		String token;
		do {
			token = UUID.randomUUID().toString();
		} while (allTokens.contains(token));
		Token tokenUser = new Token();
		tokenUser.setValue(token);
		tokenUser.setUser(user);
		return tokenUser;
	}

}
