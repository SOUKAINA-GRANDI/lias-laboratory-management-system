package ma.lias.app.dao.impl;

import ma.lias.app.entity.Mandat;

import java.util.List;
import java.util.Optional;

public class MandatDAO extends GenericDAOImpl<Mandat, Long> {

    public Optional<Mandat> findActif() {
        return getEM().createNamedQuery("Mandat.findActif", Mandat.class)
                .getResultStream()
                .findFirst();
    }

    public List<Mandat> findTous() {
        return getEM().createNamedQuery("Mandat.findAll", Mandat.class)
                .getResultList();
    }

    public Optional<Mandat> findByDirecteur(Long membreId) {
        return getEM().createQuery(
                "SELECT m FROM Mandat m WHERE m.directeur.id = :id",
                Mandat.class)
                .setParameter("id", membreId)
                .getResultStream()
                .findFirst();
    }
}