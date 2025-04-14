package it.nextre.corsojava.dao;

import it.nextre.corsojava.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public abstract class Dao<T extends Entity> {

    protected final Map<Long, T> database;
    protected Long idGenerator;

    protected Dao() {
        this.idGenerator = 1L;
        this.database = new HashMap<>();
    }

    public void add(T item) {
        item.setId(idGenerator++);
        database.put(item.getId(), item);
    }

    public abstract void update(Long id, T item);

    public void delete(Long id) {
        database.remove(id);
    }
}
