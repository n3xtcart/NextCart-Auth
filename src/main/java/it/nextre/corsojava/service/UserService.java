package it.nextre.corsojava.service;

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
import it.nextre.corsojava.exception.GroupMissingException;
import it.nextre.corsojava.exception.RoleMissingException;
import it.nextre.corsojava.exception.UnauthorizedException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        Group toSave = new Group();
        RoleDTO infoRole = group.getRoleDTO();
        Role role = roleDAO.getById(infoRole.getId());
        toSave.setRole(role);
        groupDAO.add(toSave);
    }

    @Override
    public void updateGroup(GroupDTO group, TokenDTO token) {
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        Group g = groupDAO.getById(group.getId());
        if (g == null) throw new GroupMissingException("Impossibile modificare un gruppo non presente");
        Role role = new Role();
        RoleDTO roleDTO = group.getRoleDTO();
        role.setId(roleDTO.getId());
        role.setDescrizione(roleDTO.getDescrizione());
        role.setAdmin(roleDTO.getAdmin());
        role.setPriority(roleDTO.getPriority());
        g.setRole(role);
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
    public void createRole(RoleDTO roleDTO, TokenDTO token) {
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        Role toSave = new Role();
        toSave.setDescrizione(roleDTO.getDescrizione());
        toSave.setPriority(roleDTO.getPriority());
        toSave.setAdmin(roleDTO.getAdmin());
        roleDAO.add(toSave);
    }

    @Override
    public void updateRole(RoleDTO roleDTO, TokenDTO token) {
        if (!token.getUserDTO().getGroupDTO().getRoleDTO().getAdmin())
            throw new UnauthorizedException("Non possiedi i permessi per compiere questa azione.");
        Role role = new Role();
        role.setDescrizione(roleDTO.getDescrizione());
        role.setAdmin(roleDTO.getAdmin());
        role.setPriority(roleDTO.getPriority());
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
