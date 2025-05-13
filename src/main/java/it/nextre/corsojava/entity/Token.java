package it.nextre.corsojava.entity;

import java.sql.Timestamp;
import java.time.Instant;

import it.nextre.corsojava.entity.annotation.Attribute;

public class Token extends Entity {
	@Attribute(colName = "value",fieldName = "value")
    private String value;
	@Attribute(colName = "userId",type = "long",fieldName = "user",className = User.class,colClass = long.class)
    private User user;
	@Attribute(fieldName = "scadenza",colName = "scadenza", className = Instant.class,colClass = Timestamp.class,type = "timestamp" )
    private Instant dataScandenza;

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

	public Instant getDataScandenza() {
		return dataScandenza;
	}

	public void setDataScandenza(Instant dataScandenza) {
		this.dataScandenza = dataScandenza;
	}
    
    
    
   

}
