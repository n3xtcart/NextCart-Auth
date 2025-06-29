package it.nextre.corsojava.utils;

import java.util.stream.Collectors;

import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EntityConverter {
    public UserDTO fromEntity(User user) {
    			if (user == null) {
			return null;
		}

		UserDTO dto = new UserDTO();
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setNome(user.getNome());
		dto.setNome(user.getNome());
		dto.setGroupDTO(fromEntity(user.getGroup()));
		dto.setRuoli(user.getRoles().stream().map(this::fromEntity).collect(Collectors.toSet()));
		dto.setCreationUser(user.getCreationUser()!= null ? user.getCreationUser().getId() : null);
		dto.setDataCreazione(user.getDataCreazione());
		dto.setUltimaModifica(user.getUltimaModifica());

	

		return dto;
    }

    public User fromDTO(UserDTO dto) {
    	if (dto == null) {
    		return null;
    	}
    	User user = new User();
    	user.setId(dto.getId());
    	user.setEmail(dto.getEmail());
    	user.setNome(dto.getNome());
    	user.setCognome(dto.getCognome());
    	user.setGroup(fromDTO(dto.getGroupDTO()));
    	user.setPassword(dto.getPassword());
    	user.setRoles(dto.getRuoli().stream().map(this::fromDTO).collect(Collectors.toSet()));
    	return user;
    }

    public GroupDTO fromEntity(Group group) {

    			if (group == null) {
			return null;
		}

		GroupDTO dto = new GroupDTO();
		dto.setId(group.getId());
		dto.setRoleDTO(group.getRoles().stream().map(this::fromEntity).collect(Collectors.toSet()));
		dto.setCreationUser(group.getCreationUser()!= null ? group.getCreationUser().getId() : null);
		dto.setDataCreazione(group.getDataCreazione());
		dto.setUltimaModifica(group.getUltimaModifica());
		dto.setDescrizione(group.getDescrizione());
		return dto;
    }
    
  
    public Group fromDTO(GroupDTO dto) {
    			if (dto == null) {
			return null;
		}

		Group group = new Group();
		group.setId(dto.getId());
		group.setDescrizione(dto.getDescrizione());
		group.setRoles(dto.getRoleDTO().stream().map(this::fromDTO).collect(Collectors.toSet()));
		return group;
    }

    public RoleDTO fromEntity(Role role) {

    	if (role == null) {
			return null;
		}
    	RoleDTO dto = new RoleDTO();
    	dto.setId(role.getId());
		dto.setAdmin(role.getAdmin());
		dto.setDescrizione(role.getDescrizione());
		dto.setPriority(role.getPriority());
		dto.setCreationUser(role.getCreationUser()!= null ? role.getCreationUser().getId() : null);
		dto.setDataCreazione(role.getDataCreazione());
		dto.setUltimaModifica(role.getUltimaModifica());
		
		return dto;
    }

    public Role fromDTO(RoleDTO dto) {
		if (dto == null) {
			return null;
		}
		Role role = new Role();
		role.setId(dto.getId());
		role.setAdmin(dto.getAdmin());
		role.setDescrizione(dto.getDescrizione());
		role.setPriority(dto.getPriority());
		return role;
    }
}
