package it.nextre.corsojava.entity;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;

import it.nextre.corsojava.entity.annotation.Attribute;

public abstract class Entity {
	@Attribute(fieldName = "id",colName = "id",type = "long",className = Long.class,colClass = long.class,auto = true)
			
    protected Long id;

	@Attribute(fieldName = "ultimaModifica",colName = "ultimaModifica", className = Instant.class,colClass = Timestamp.class,type = "timestamp" )
    protected Instant instant;

    public Entity() {
        this.instant = Instant.now();
    }

    public Instant getUltimaModifica() {
        return instant;
    }

    protected void aggiornaUltimaModifica() {
        this.instant = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        aggiornaUltimaModifica();
        this.id = id;
    }
    public void setUltimaModifica(Instant i) {
    	this.instant=i;
    	
    }


}
