package it.nextre.corsojava.entity;

import it.nextre.corsojava.entity.annotation.Attribute;

public class Token extends Entity {
	@Attribute(name = "value")
    private String value;
	@Attribute(name = "user")
    private User user;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        aggiornaUltimaModifica();
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        aggiornaUltimaModifica();
        this.user = user;
    }

}
