package it.nextre.corsojava.service;


import java.util.List;
import it.nextre.corsojava.dao.jdbc.PagedResult;
import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.RoleDTO;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.TokensJwt;
import it.nextre.corsojava.dto.UserDTO;

public interface UserServiceInterface {

    public TokensJwt login(UserDTO user);

    public void register(UserDTO user);
    
    public TokensJwt confirmRegistration(TokenDTO token);

    public boolean checkToken(TokenDTO token);

    public TokenDTO findTokenByValue(String val);

    public void updateUser(UserDTO user);

    public void deleteUser(UserDTO user);

    //solo admin
    public void createUser(UserDTO user);

    public List<UserDTO> getAllUsers();

    public void createGroup(GroupDTO group);


    public void updateGroup(GroupDTO group);

    public void deleteGroup(GroupDTO group);

    //solo admin
    public List<GroupDTO> getAllGroup();

    public void createRole(RoleDTO group);


    public void updateRole(RoleDTO group);

    public void deleteRole(RoleDTO group);

    //solo admin
    public List<RoleDTO> getAllRole();
    
    public PagedResult<UserDTO> getAllUsersPag( int page, int size);
    public PagedResult<GroupDTO> getAllGroupsPag( int page, int size);
    public PagedResult<RoleDTO> getAllRolesPag( int page, int size);


}
