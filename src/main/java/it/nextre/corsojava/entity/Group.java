package it.nextre.corsojava.entity;

import java.util.Set;

import it.nextre.aut.dto.GroupDTO;
import it.nextre.corsojava.entity.annotation.Attribute;
import it.nextre.corsojava.entity.annotation.ManyToMany;

public class Group extends Entity {
	@ManyToMany(joinColumn = "roleId" ,mapObject = Role.class,joinTable = "role",supportTable="group_role_mapping",
			supportJoinColumn = "groupId")
    private Set<Role> roles;

    @Attribute(fieldName = "descrizione", colName = "descrizione")
    private String descrizione;

    

    

    
    
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
        aggiornaUltimaModifica();
		this.descrizione = descrizione;
	}

	public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        aggiornaUltimaModifica();
        this.roles = roles;
    }

}
