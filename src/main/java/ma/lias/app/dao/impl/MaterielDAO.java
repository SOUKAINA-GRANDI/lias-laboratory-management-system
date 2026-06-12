package ma.lias.app.dao.impl;

import ma.lias.app.entity.Materiel;

import java.util.List;

public class MaterielDAO extends GenericDAOImpl<Materiel, Long> {

    public List<Materiel> findDisponibles() {
        return getEM().createNamedQuery("Materiel.findDisponibles", Materiel.class)
                .setParameter("statut", Materiel.StatutMateriel.DISPONIBLE)
                .getResultList();
    }

    public List<Materiel> findByType(Materiel.TypeMateriel type) {
        return getEM().createNamedQuery("Materiel.findByType", Materiel.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Materiel> findByMembre(Long membreId) {
        return getEM().createNamedQuery("Materiel.findByMembre", Materiel.class)
                .setParameter("membreId", membreId)
                .getResultList();
    }

    public List<Materiel> findByStatut(Materiel.StatutMateriel statut) {
        return getEM().createQuery(
                "SELECT m FROM Materiel m WHERE m.statut = :statut ORDER BY m.dateArrivage DESC",
                Materiel.class)
                .setParameter("statut", statut)
                .getResultList();
    }
}