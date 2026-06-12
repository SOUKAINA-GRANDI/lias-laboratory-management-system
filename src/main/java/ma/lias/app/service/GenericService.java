package ma.lias.app.service;

import java.util.List;
import java.util.Optional;

public interface GenericService<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    T update(T entity);
    void delete(T entity);
    void deleteById(ID id);
    long count();
    boolean exists(ID id);
}