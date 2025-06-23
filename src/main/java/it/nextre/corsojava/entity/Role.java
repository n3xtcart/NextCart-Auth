package it.nextre.corsojava.entity;

import it.nextre.aut.dto.RoleDTO;
import it.nextre.corsojava.entity.annotation.Attribute;

public class Role extends Entity {
    @Attribute(fieldName = "descrizione", colName = "descrizione")
    private String descrizione;
    @Attribute(fieldName = "admin", colName = "admin", className = Boolean.class, colClass = boolean.class, type = "boolean")
    private Boolean admin;
    @Attribute(fieldName = "priority", colName = "priority", type = "long", className = Long.class, colClass = long.class)
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

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        aggiornaUltimaModifica();
        this.priority = priority;
    }

    public Boolean getAdmin() {
        return admin != null && admin.booleanValue();
    }

    public void setAdmin(Boolean admin) {
        aggiornaUltimaModifica();
        this.admin = admin;
    }
    
    /**
	 * Confronta due ruoli in base al loro stato di amministratore e priorità.
	 * 
	 * @param o Il ruolo da confrontare.
	 * @return 1 se maggiore,-1 se minore ,0 se uguale
	 */

    public int compareTo(Role o) {
        if (admin != null && o.isAdmin() != null && admin.booleanValue() == o.isAdmin().booleanValue()) {
            return priority.compareTo(o.getPriority());
        } else if (admin != null && admin) {
            return 1;
        } else if (o.isAdmin() != null && o.isAdmin()) {
            return -1;
        }
        return 0;

    }


}
