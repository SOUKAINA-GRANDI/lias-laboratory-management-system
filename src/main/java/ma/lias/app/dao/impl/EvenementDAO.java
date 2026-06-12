package ma.lias.app.dao.impl;

import ma.lias.app.entity.Evenement;

import java.time.LocalDate;
import java.util.List;

public class EvenementDAO extends GenericDAOImpl<Evenement, Long> {

    public List<Evenement> findAVenir() {
        return getEM().createNamedQuery("Evenement.findAVenir", Evenement.class)
                .setParameter("today", LocalDate.now())
                .getResultList();
    }

    public List<Evenement> findByType(Evenement.TypeEvenement type) {
        return getEM().createNamedQuery("Evenement.findByType", Evenement.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Evenement> findByAnnee(int annee) {
        return getEM().createNamedQuery("Evenement.findByAnnee", Evenement.class)
                .setParameter("debut", LocalDate.of(annee, 1, 1))
                .setParameter("fin",   LocalDate.of(annee, 12, 31))
                .getResultList();
    }

    public List<Evenement> findByOrganisateur(Long membreId) {
        return getEM().createNamedQuery("Evenement.findByOrganisateur", Evenement.class)
                .setParameter("membreId", membreId)
                .getResultList();
    }

    public List<Evenement> findByStatut(Evenement.StatutEvenement statut) {
        return getEM().createNamedQuery("Evenement.findByStatut", Evenement.class)
                .setParameter("statut", statut)
                .getResultList();
    }
}