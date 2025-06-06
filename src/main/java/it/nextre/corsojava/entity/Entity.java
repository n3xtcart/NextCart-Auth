package it.nextre.corsojava.entity;

import it.nextre.corsojava.entity.annotation.Attribute;

import java.sql.Timestamp;
import java.time.Instant;

public abstract class Entity {
    @Attribute(fieldName = "id", colName = "id", type = "long", className = Long.class, colClass = long.class, auto = true)
    protected Long id;

    @Attribute(fieldName = "ultimaModifica", colName = "ultimaModifica", className = Instant.class, colClass = Timestamp.class, type = "timestamp" ,update = false)
    protected Instant ultimaModifica;
    @Attribute(fieldName = "dataCreazione", colName = "dataCreazione", className = Instant.class, colClass = Timestamp.class, type = "timestamp")
    protected Instant dataCreazione;
    @Attribute(fieldName="user", colName = "creationUser", className = User.class, colClass = long.class, type = "long",update = false)
    User user;

    public Entity() {
        this.ultimaModifica = Instant.now();
    }

    
    
    public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public Instant getDataCreazione() {
		return dataCreazione;
	}


	public void setDataCreazione(Instant dataCreazione) {
		this.dataCreazione = dataCreazione;
	}


	public Instant getUltimaModifica() {
        return ultimaModifica;
    }

    public void setUltimaModifica(Instant i) {
        this.ultimaModifica = i;
    }

    protected void aggiornaUltimaModifica() {
        this.ultimaModifica = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        aggiornaUltimaModifica();
        this.id = id;
    }


}
