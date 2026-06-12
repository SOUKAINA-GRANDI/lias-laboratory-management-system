package ma.lias.app.dao.impl;

import ma.lias.app.entity.AttributionMateriel;

import java.util.List;
import java.util.Optional;

public class AttributionMaterielDAO extends GenericDAOImpl<AttributionMateriel, Long> {

    public List<AttributionMateriel> findByMembre(Long membreId) {
        return getEM().createQuery(
                "SELECT a FROM AttributionMateriel a WHERE a.membre.id = :id ORDER BY a.dateAttribution DESC",
                AttributionMateriel.class)
                .setParameter("id", membreId)
                .getResultList();
    }

    public List<AttributionMateriel> findByMateriel(Long materielId) {
        return getEM().createQuery(
                "SELECT a FROM AttributionMateriel a WHERE a.materiel.id = :id ORDER BY a.dateAttribution DESC",
                AttributionMateriel.class)
                .setParameter("id", materielId)
                .getResultList();
    }

    public List<AttributionMateriel> findActives() {
        return getEM().createQuery(
                "SELECT a FROM AttributionMateriel a WHERE a.dateRetour IS NULL ORDER BY a.dateAttribution DESC",
                AttributionMateriel.class)
                .getResultList();
    }

    public Optional<AttributionMateriel> findActiveByMembreAndMateriel(
            Long membreId, Long materielId) {
        return getEM().createQuery(
                "SELECT a FROM AttributionMateriel a " +
                "WHERE a.membre.id = :membreId " +
                "AND a.materiel.id = :materielId " +
                "AND a.dateRetour IS NULL",
                AttributionMateriel.class)
                .setParameter("membreId", membreId)
                .setParameter("materielId", materielId)
                .getResultStream()
                .findFirst();
    }
}