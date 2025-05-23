package it.nextre.corsojava.entity;

import it.nextre.corsojava.dto.RoleDTO;
import it.nextre.corsojava.entity.annotation.Attribute;

public class Role extends Entity {
	@Attribute(fieldName = "descrizione",colName = "descrizione")
    private String descrizione;
		@Attribute(fieldName = "admin",colName = "admin",className = Boolean.class,colClass = boolean.class,type="boolean")
    private Boolean admin;
		@Attribute(fieldName = "priority",colName = "priority",type = "long",className = Long.class,colClass = long.class)
    private Long priority;


    public Role(RoleDTO roleDTO) {
		this.id = roleDTO.getId();
		this.descrizione = roleDTO.getDescrizione();
		this.admin = roleDTO.getAdmin();
		this.priority = roleDTO.getPriority();
	}

	public Role() {
		// TODO Auto-generated constructor stub
	}

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
    
    public Boolean getAdmin() {
		return admin != null && admin.booleanValue();
	}
    
    public int compareTo(Role o) {
    	if(admin !=null && o.isAdmin()!=null && admin.booleanValue()==o.isAdmin().booleanValue()) {
			return priority.compareTo(o.getPriority());
		}
    	else if(admin!=null && admin.booleanValue() ) {
    		return 1;
    	}
    	else if(o.isAdmin()!=null && o.isAdmin().booleanValue()) {
			return -1;
		}
		return 0;
    
    }


}
