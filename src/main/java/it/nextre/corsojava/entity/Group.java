package it.nextre.corsojava.entity;

import java.util.Set;

import it.nextre.aut.dto.GroupDTO;
import it.nextre.corsojava.entity.annotation.ManyToMany;
import it.nextre.corsojava.entity.annotation.OneToMeny;

public class Group extends Entity {
	@ManyToMany(joinColumn = "roleId" ,mapObject = Role.class,joinTable = "role",supportTable="group_role_mapping",
			supportJoinColumn = "groupId")
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

    public void setRoles(Set<Role> roles) {
        aggiornaUltimaModifica();
        this.roles = roles;
    }

}
