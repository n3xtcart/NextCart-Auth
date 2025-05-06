package it.nextre.corsojava.entity;

import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.entity.annotation.Attribute;

public class Group extends Entity {

	@Attribute(name = "ruolo")
	private Role role;

	public Group(GroupDTO groupDTO) {
		this.id = groupDTO.getId();
		if (groupDTO.getRoleDTO() != null) {
			this.role = new Role(groupDTO.getRoleDTO());
		}
	}
	public Group() {
		// TODO Auto-generated constructor stub
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		aggiornaUltimaModifica();
		this.role = role;
	}

}
