package it.nextre.corsojava.dao;

import java.util.List;

import it.nextre.corsojava.entity.Entity;

public interface DaoInterface<T extends Entity>{


    



     void delete(Long id);
    
     T getById(Long id);
    
     List<T> getAll();

	void update(Long id, T item);


	void add(T item);
}
