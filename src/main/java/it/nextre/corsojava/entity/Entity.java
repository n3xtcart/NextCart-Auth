package it.nextre.corsojava.entity;

import java.time.Instant;

import it.nextre.corsojava.entity.annotation.Attribute;

public abstract class Entity {
	@Attribute(name = "id")
    protected Long id;

    protected Instant instant;

    protected Entity() {
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


}
