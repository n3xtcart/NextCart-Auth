package it.nextre.corsojava.entity;

import it.nextre.corsojava.dto.UserDTO;
import it.nextre.corsojava.entity.annotation.Attribute;

public class User extends Entity {
	@Attribute(colName = "nome",fieldName = "nome")
    private String nome;
		@Attribute(colName = "cognome",fieldName = "cognome")
    private String cognome;
		@Attribute(colName = "email",fieldName = "email")
    private String email;
		@Attribute(colName = "password",fieldName = "password")
    private String password;
		@Attribute(colName = "groupId",fieldName = "group" ,type = "long", className = Group.class,colClass = long.class)
    private Group group;


    public User(UserDTO user) {
    			this.id = user.getId();
		this.nome = user.getNome();
		this.cognome = user.getCognome();
		this.email = user.getEmail();
		this.password = user.getPassword();
		if (user.getGroupDTO() != null) {
			this.group = new Group(user.getGroupDTO());
		}
	}
	


	public User() {
		// TODO Auto-generated constructor stub
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


}
