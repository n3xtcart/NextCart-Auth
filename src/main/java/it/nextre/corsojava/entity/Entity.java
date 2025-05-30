package it.nextre.corsojava.entity;

import it.nextre.corsojava.entity.annotation.Attribute;

import java.sql.Timestamp;
import java.time.Instant;

public abstract class Entity {
    @Attribute(fieldName = "id", colName = "id", type = "long", className = Long.class, colClass = long.class, auto = true)
    protected Long id;

    @Attribute(fieldName = "ultimaModifica", colName = "ultimaModifica", className = Instant.class, colClass = Timestamp.class, type = "timestamp")
    protected Instant instant;

    public Entity() {
        this.instant = Instant.now();
    }

    public Instant getUltimaModifica() {
        return instant;
    }

    public void setUltimaModifica(Instant i) {
        this.instant = i;
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


}
