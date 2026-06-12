package ma.lias.app.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("lias-pu");

    private static final ThreadLocal<EntityManager> threadLocal =
            new ThreadLocal<>();

    // ========== ENTITY MANAGER ==========

    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
            threadLocal.set(em);
        }
        return em;
    }

    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null && em.isOpen()) {
            em.close();
            threadLocal.remove();
        }
    }

    // ✅ Méthode close() manquante — utilisée dans les tests
    public static void close() {
        closeEntityManager();
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    // ✅ Vérifier si la factory est ouverte
    public static boolean isOpen() {
        return emf != null && emf.isOpen();
    }
}