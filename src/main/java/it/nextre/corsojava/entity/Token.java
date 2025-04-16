package it.nextre.corsojava.entity;

public class Token extends Entity {
    private String value;
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
