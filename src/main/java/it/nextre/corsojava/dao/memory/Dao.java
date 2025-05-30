package it.nextre.corsojava.dao.memory;

import it.nextre.corsojava.dao.jdbc.PagedResult;
import it.nextre.corsojava.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Dao<T extends Entity> implements DaoInterface<T> {

    protected  Map<Long, T> database;
    protected Long idGenerator;

    protected Dao() {
        this.idGenerator = 1L;
        this.database = new HashMap<>();
    }

	@Override
	public void delete(Long id) {
      database.remove(id);
		
	}


	@Override
	public T getById(Long id) {
		return database.get(id);
	}

	
	@Override
	public List<T> getAll() {
		return database.values().stream().toList();
	}

	@Override
	public abstract void update(Long id, T item);

	@Override
	public Long add(T item) {
      item.setId(idGenerator++);
      database.put(item.getId(), item);
      return item.getId();
		
	}
	
	public void setDatabase(Map<Long,T> database) {
		this.database=database;
		this.idGenerator=1L;
	}


	public PagedResult<T>getAllPag(int page, int size) {
		int total = database.size();
		int start = (page - 1) * size;
		int end = Math.min(start + size, total);
		List<T> items = database.values().stream().skip(start).limit(size).toList();
		return new PagedResult<T>(total, items, size);
	}
	


}
