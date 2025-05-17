package it.nextre.corsojava.service;


import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.RoleDTO;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;

import java.util.List;

public interface UserServiceInterface {

    public TokenDTO login(UserDTO user);

    public void register(UserDTO user);
    
    public TokenDTO confirmRegistration(TokenDTO token);

    public boolean checkToken(TokenDTO token);

    public TokenDTO findTokenByValue(String val);

    public void updateUser(UserDTO user, TokenDTO token);

    public void deleteUser(UserDTO user, TokenDTO token);

    //solo admin
    public void createUser(UserDTO user, TokenDTO token);

    public List<UserDTO> getAllUsers(TokenDTO token);

    public void createGroup(GroupDTO group, TokenDTO token);


    public void updateGroup(GroupDTO group, TokenDTO token);

    public void deleteGroup(GroupDTO group, TokenDTO token);

    //solo admin
    public List<GroupDTO> getAllGroup(TokenDTO token);

    public void createRole(RoleDTO group, TokenDTO token);


    public void updateRole(RoleDTO group, TokenDTO token);

    public void deleteRole(RoleDTO group, TokenDTO token);

    //solo admin
    public List<RoleDTO> getAllRole(TokenDTO token);


}
