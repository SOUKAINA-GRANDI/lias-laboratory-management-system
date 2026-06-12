package ma.lias.app.dao.impl;

import java.util.List;
import java.util.Optional;

import ma.lias.app.entity.Membre;
import ma.lias.app.enums.StatutMembre;

public class MembreDAO extends GenericDAOImpl<Membre, Long> {

    public Optional<Membre> findByEmail(String email) {
        return getEM().createNamedQuery("Membre.findByEmail", Membre.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public List<Membre> findByStatut(StatutMembre statut) {
        return getEM().createNamedQuery("Membre.findByStatut", Membre.class)
                .setParameter("statut", statut)
                .getResultList();
    }

    public List<Membre> findActifs() {
        return getEM().createNamedQuery("Membre.findAll", Membre.class)
                .getResultList();
    }

    public List<Membre> findByEquipe(Long equipeId) {
        return getEM().createNamedQuery("Membre.findByEquipe", Membre.class)
                .setParameter("equipeId", equipeId)
                .getResultList();
    }

    public List<Membre> rechercher(String mot) {
        String m = "%" + mot.toLowerCase() + "%";
        return getEM().createQuery(
                "SELECT m FROM Membre m WHERE " +
                "LOWER(m.nom) LIKE :mot OR " +
                "LOWER(m.prenom) LIKE :mot OR " +
                "LOWER(m.email) LIKE :mot", Membre.class)
                .setParameter("mot", m)
                .getResultList();
    }

    public long countByStatut(StatutMembre statut) {
        return getEM().createNamedQuery("Membre.countByStatut", Long.class)
                .setParameter("statut", statut)
                .getSingleResult();
    }
}