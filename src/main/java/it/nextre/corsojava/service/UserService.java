package it.nextre.corsojava.service;

import it.nextre.corsojava.dao.GroupDAO;
import it.nextre.corsojava.dao.RoleDAO;
import it.nextre.corsojava.dao.TokenUserDAO;
import it.nextre.corsojava.dao.UserDAO;
import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.RoleDTO;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.exception.UnauthorizedException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserService implements UserServiceInterface {
    private UserDAO userDAO;
    private TokenUserDAO tokenUserDAO;
    private GroupDAO groupDAO;
    private RoleDAO roleDAO;

    public UserService(UserDAO userDAO, TokenUserDAO tokenUserDAO, GroupDAO groupDAO, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.tokenUserDAO = tokenUserDAO;
        this.groupDAO = groupDAO;
        this.roleDAO = roleDAO;
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
        return tokenUserDAO.getAll().stream().anyMatch(t -> t.getValue().equals(token.getToken()));
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
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        return userDAO.getAll().stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setNome(user.getNome());
            dto.setCognome(user.getCognome());
            dto.setPassword(user.getPassword());
            dto.setId(user.getId());

            GroupDTO groupDTO = new GroupDTO();

            RoleDTO roleDTO = new RoleDTO();
            Role r = user.getGroup().getRole();
            roleDTO.setAdmin(r.isAdmin());
            roleDTO.setPriority(r.getPriority());
            roleDTO.setDescrizione(r.getDescrizione());

            groupDTO.setRoleDTO(roleDTO);

            dto.setGroupDTO(groupDTO);
            return dto;
        }).toList();
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
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        return groupDAO.getAll().stream().map(group -> {
            GroupDTO dto = new GroupDTO();
            RoleDTO roleDTO = new RoleDTO();
            Role r = group.getRole();

            roleDTO.setAdmin(r.isAdmin());
            roleDTO.setDescrizione(r.getDescrizione());
            roleDTO.setPriority(r.getPriority());

            dto.setRoleDTO(roleDTO);
            return dto;
        }).toList();
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
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        return roleDAO.getAll().stream().map(role -> {
            RoleDTO dto = new RoleDTO();
            dto.setPriority(role.getPriority());
            dto.setDescrizione(role.getDescrizione());
            dto.setAdmin(role.isAdmin());
            return dto;
        }).toList();
    }


    private String generateToken() {
        Set<String> allTokens = tokenUserDAO.getAll().stream().map(Token::getValue).collect(Collectors.toSet());
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (allTokens.contains(token));
        return token;
    }

}
