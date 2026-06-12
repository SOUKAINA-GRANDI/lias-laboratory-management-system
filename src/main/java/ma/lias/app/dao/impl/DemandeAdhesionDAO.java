package ma.lias.app.dao.impl;

import ma.lias.app.entity.DemandeAdhesion;
import ma.lias.app.enums.StatutDemande;

import java.util.List;
import java.util.Optional;

public class DemandeAdhesionDAO extends GenericDAOImpl<DemandeAdhesion, Long> {

    public List<DemandeAdhesion> findEnAttente() {
        return getEM().createNamedQuery("DemandeAdhesion.findEnAttente", DemandeAdhesion.class)
                .getResultList();
    }

    public List<DemandeAdhesion> findByStatut(StatutDemande statut) {
        return getEM().createNamedQuery("DemandeAdhesion.findByStatut", DemandeAdhesion.class)
                .setParameter("statut", statut)
                .getResultList();
    }

    public Optional<DemandeAdhesion> findByEmail(String email) {
        return getEM().createNamedQuery("DemandeAdhesion.findByEmail", DemandeAdhesion.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public List<DemandeAdhesion> findAll() {
        return getEM().createNamedQuery("DemandeAdhesion.findAll", DemandeAdhesion.class)
                .getResultList();
    }
}