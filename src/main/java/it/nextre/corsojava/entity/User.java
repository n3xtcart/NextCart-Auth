package it.nextre.corsojava.entity;

import java.util.Set;

import it.nextre.aut.dto.UserDTO;
import it.nextre.corsojava.entity.annotation.Attribute;
import it.nextre.corsojava.entity.annotation.OneToOne;

public class User extends Entity {
    @Attribute(colName = "nome", fieldName = "nome")
    private String nome;
    @Attribute(colName = "cognome", fieldName = "cognome")
    private String cognome;
    @Attribute(colName = "email", fieldName = "email")
    private String email;
    @Attribute(colName = "password", fieldName = "password")
    private String password;
    @OneToOne(joinColumn = "groupId", joinTable = "groupT", mapObject = Group.class)
    @Attribute(fieldName = "group",colName = "groupId",className = Group.class,colClass = long.class,type = "long")
    private Group group;
    @Attribute(fieldName = "active", colName = "active", className = Boolean.class, colClass = boolean.class, type = "boolean")
    private Boolean active;
    @OneToOne(joinColumn = "roleId", joinTable = "role", mapObject = Role.class)
    @Attribute(fieldName = "role",colName = "roleId",className = Role.class,colClass = long.class,type = "long")
    private Role role;


    public User() {
    }

    public User(UserDTO user) {

    			this.nome = user.getNome();
		this.cognome = user.getCognome();
		this.email = user.getEmail();
		this.password = user.getPassword();
		
	}

	public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        aggiornaUltimaModifica();
        this.group = group;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        aggiornaUltimaModifica();
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        aggiornaUltimaModifica();
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        aggiornaUltimaModifica();
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        aggiornaUltimaModifica();
        this.password = password;
    }
    
    public Set<Role> getRoles() {
    	Set<Role> roles = group.getRoles();
		if (role != null) {
			roles.add(role);
		}
		return roles;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
    
   


}
