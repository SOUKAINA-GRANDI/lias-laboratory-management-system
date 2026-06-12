package ma.lias.app.dao.impl;

import ma.lias.app.dao.GenericDAO;
import ma.lias.app.util.JPAUtil;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class GenericDAOImpl<T, ID> implements GenericDAO<T, ID> {

    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public GenericDAOImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType)
                getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    protected EntityManager getEM() {
        return JPAUtil.getEntityManager();
    }

    @Override
    public void save(T entity) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new RuntimeException("Erreur save : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(getEM().find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return getEM().createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    @Override
    public T update(T entity) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            T updated = em.merge(entity);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new RuntimeException("Erreur update : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(T entity) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            T managed = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new RuntimeException("Erreur delete : " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public long count() {
        return getEM().createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class)
                .getSingleResult();
    }

    @Override
    public boolean exists(ID id) {
        return findById(id).isPresent();
    }
}