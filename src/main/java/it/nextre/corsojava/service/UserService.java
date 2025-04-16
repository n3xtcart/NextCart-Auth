package it.nextre.corsojava.service;

import java.util.List;

import it.nextre.corsojava.dao.GroupDAO;
import it.nextre.corsojava.dao.RoleDAO;
import it.nextre.corsojava.dao.TokenAdminDAO;
import it.nextre.corsojava.dao.TokenUserDAO;
import it.nextre.corsojava.dao.UserDAO;
import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.RoleDTO;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;

public class UserService implements UserServiceInterface {
	private UserDAO userDAO;
	private TokenAdminDAO tokenService;
	private TokenUserDAO tokenUserDAO;
	private GroupDAO groupDAO;
	private RoleDAO roleDAO;
	
	public UserService() {
		super();
		this.userDAO = new UserDAO();
		this.tokenService = new TokenAdminDAO();
		this.tokenUserDAO = new TokenUserDAO();
		this.groupDAO = new GroupDAO();
		this.roleDAO = new RoleDAO();
	}
	
	@Override
	public TokenDTO login(UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public TokenDTO register(UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean checkToken(TokenDTO token) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void updateUser(UserDTO user, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteUser(UserDTO user, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void createUser(UserDTO user, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<UserDTO> getAllUsers(TokenDTO token) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void createGroup(GroupDTO group, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateGroup(GroupDTO group, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteGroup(GroupDTO group, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<GroupDTO> getAllGroup(TokenDTO token) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void createRole(RoleDTO group, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateRole(RoleDTO group, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteRole(RoleDTO group, TokenDTO token) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<RoleDTO> getAllRole(TokenDTO token) {
		// TODO Auto-generated method stub
		return null;
	}


	

}
