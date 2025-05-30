package it.nextre.corsojava.entity;

import java.util.Set;

import it.nextre.corsojava.dto.GroupDTO;import it.nextre.corsojava.dto.RoleDTO;
import it.nextre.corsojava.entity.annotation.Attribute;

public class Group extends Entity {

	//@Attribute(fieldName = "role",colName = "roleId",className = Set.class,colClass = long.class,type = "long")
	private Set<Role> role;

	
	public Group() {
		// TODO Auto-generated constructor stub
	}
	public Group(GroupDTO group) {
		if(group.getRoleDTO()!=null) {
			this.role = group.getRoleDTO().stream().map(roleDTO -> {
				Role role = new Role(roleDTO);
				return role;
			}).collect(java.util.stream.Collectors.toSet());
		}
		else {
			this.role = null;
		}
	}
	public Set<Role> getRole() {
		return role;
	}
	public void setRole(Set<Role> role) {
		this.role = role;
	}

	

}
