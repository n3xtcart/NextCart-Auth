package it.nextre.corsojava.entity;

import java.util.Set;

import it.nextre.aut.dto.GroupDTO;

public class Group extends Entity {

    private Set<Role> roles;

    public Group() {
    }

    public Group(GroupDTO group) {
		this.id = group.getId();
		this.roles = group.getRoleDTO().stream()
				.map(roleDTO -> new Role(roleDTO))
				.collect(java.util.stream.Collectors.toSet());
	
	}

	public Set<Role> getRoles() {
        return roles;
    }

    public void setRole(Set<Role> roles) {
        aggiornaUltimaModifica();
        this.roles = roles;
    }

}
