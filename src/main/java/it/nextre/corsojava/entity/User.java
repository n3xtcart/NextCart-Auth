package it.nextre.corsojava.entity;

public class User extends Entity {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private Group group;


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
