package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.service.GenericService;

import java.util.List;
import java.util.Optional;

public abstract class GenericServiceImpl<T, ID> implements GenericService<T, ID> {

    protected abstract GenericDAOImpl<T, ID> getDAO();

    @Override
    public void save(T entity) {
        getDAO().save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return getDAO().findById(id);
    }

    @Override
    public List<T> findAll() {
        return getDAO().findAll();
    }

    @Override
    public T update(T entity) {
        return getDAO().update(entity);
    }

    @Override
    public void delete(T entity) {
        getDAO().delete(entity);
    }

    @Override
    public void deleteById(ID id) {
        getDAO().deleteById(id);
    }

    @Override
    public long count() {
        return getDAO().count();
    }

    @Override
    public boolean exists(ID id) {
        return getDAO().exists(id);
    }
}