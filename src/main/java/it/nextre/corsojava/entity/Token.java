package it.nextre.corsojava.entity;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import it.nextre.corsojava.entity.annotation.Attribute;

public class Token extends Entity {
	@Attribute(colName = "value",fieldName = "value")
    private String value;
	@Attribute(colName = "userId",type = "long",fieldName = "user",className = User.class,colClass = long.class)
    private User user;
	@Attribute(fieldName = "dataScadenza",colName = "scadenza", className = Instant.class,colClass = Timestamp.class,type = "timestamp" )
    private Instant dataScandenza;

	
	public Token() {
		this.dataScandenza=LocalDateTime.now().toInstant(ZoneOffset.UTC);
	}
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

	public Instant getDataScadenza() {
		return dataScandenza;
	}

	public void setDataScadenza(Instant dataScandenza) {
		this.dataScandenza = dataScandenza;
	}
    
    
    
   

}
