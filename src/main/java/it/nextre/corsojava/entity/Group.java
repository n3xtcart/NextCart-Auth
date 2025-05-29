package it.nextre.corsojava.entity;

import it.nextre.corsojava.entity.annotation.Attribute;

public class Group extends Entity {

    @Attribute(fieldName = "role", colName = "roleId", className = Role.class, colClass = long.class, type = "long")
    private Role role;

    public Group() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        aggiornaUltimaModifica();
        this.role = role;
    }

}
