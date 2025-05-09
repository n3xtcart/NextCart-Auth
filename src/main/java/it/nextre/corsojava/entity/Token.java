package it.nextre.corsojava.entity;

import it.nextre.corsojava.entity.annotation.Attribute;

public class Token extends Entity {
	@Attribute(colName = "value",fieldName = "value")
    private String value;
	@Attribute(colName = "userId",type = "long",fieldName = "user",className = User.class,colClass = long.class)
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
