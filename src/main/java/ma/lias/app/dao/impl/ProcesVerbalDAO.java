package ma.lias.app.dao.impl;

import ma.lias.app.entity.ProcesVerbal;

import java.time.LocalDate;
import java.util.List;

public class ProcesVerbalDAO extends GenericDAOImpl<ProcesVerbal, Long> {

    public List<ProcesVerbal> findByType(ProcesVerbal.TypeReunion type) {
        return getEM().createNamedQuery("ProcesVerbal.findByType", ProcesVerbal.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<ProcesVerbal> findByStatut(ProcesVerbal.StatutPV statut) {
        return getEM().createQuery(
                "SELECT p FROM ProcesVerbal p WHERE p.statut = :statut ORDER BY p.dateReunion DESC",
                ProcesVerbal.class)
                .setParameter("statut", statut)
                .getResultList();
    }

    public List<ProcesVerbal> findSansDocument() {
        return getEM().createNamedQuery("ProcesVerbal.findSansDocument", ProcesVerbal.class)
                .getResultList();
    }

    public List<ProcesVerbal> findByPeriode(LocalDate debut, LocalDate fin) {
        return getEM().createNamedQuery("ProcesVerbal.findByAnnee", ProcesVerbal.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getResultList();
    }
}