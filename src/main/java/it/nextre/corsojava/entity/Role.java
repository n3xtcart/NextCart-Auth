package it.nextre.corsojava.entity;

public class Role extends Entity {
    private String descrizione;
    private Boolean admin;
    private Long priority;


    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        aggiornaUltimaModifica();
        this.descrizione = descrizione;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        aggiornaUltimaModifica();
        this.admin = admin;
    }


    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        aggiornaUltimaModifica();
        this.priority = priority;
    }


}
