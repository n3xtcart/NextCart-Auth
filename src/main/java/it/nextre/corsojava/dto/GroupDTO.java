package it.nextre.corsojava.dto;

import it.nextre.corsojava.entity.Group;

public class GroupDTO {
    private RoleDTO roleDTO;
    private Long id;

    public GroupDTO(Group group) {
    			this.id = group.getId();
		if (group.getRole() != null) {
			this.roleDTO = new RoleDTO(group.getRole());
		}
	}


	public GroupDTO() {
		// TODO Auto-generated constructor stub
	}


	public RoleDTO getRoleDTO() {
        return roleDTO;
    }

    public void setRoleDTO(RoleDTO roleDTO) {
        this.roleDTO = roleDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
