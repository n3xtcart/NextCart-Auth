package it.nextre.corsojava.dto;

import java.util.Set;
import java.util.stream.Collectors;

import it.nextre.corsojava.entity.Group;

public class GroupDTO {
    private Set<RoleDTO> roleDTO;
    private Long id;

    public GroupDTO(Group group) {
    			this.id = group.getId();
		if (group.getRole() != null) {
			this.roleDTO =group.getRole().stream().map(RoleDTO::new).collect(Collectors.toSet());
		}
	}


	public GroupDTO() {
		// TODO Auto-generated constructor stub
	}


	public Set<RoleDTO> getRoleDTO() {
        return roleDTO;
    }

    public void setRoleDTO(Set<RoleDTO> roleDTO) {
        this.roleDTO = roleDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
