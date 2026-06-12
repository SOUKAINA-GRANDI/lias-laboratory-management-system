package ma.lias.app.dao.impl;

import ma.lias.app.entity.Convention;

import java.time.LocalDate;
import java.util.List;

public class ConventionDAO extends GenericDAOImpl<Convention, Long> {

    public List<Convention> findActives() {
        return getEM().createNamedQuery("Convention.findActives", Convention.class)
                .setParameter("statut", Convention.StatutConvention.ACTIVE)
                .getResultList();
    }

    public List<Convention> findByType(Convention.TypeConvention type) {
        return getEM().createNamedQuery("Convention.findByType", Convention.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Convention> findByPartenaire(String nom) {
        return getEM().createNamedQuery("Convention.findByPartenaire", Convention.class)
                .setParameter("nom", "%" + nom.toLowerCase() + "%")
                .getResultList();
    }

    public List<Convention> findExpirantAvant(LocalDate date) {
        return getEM().createNamedQuery("Convention.findExpirantAvant", Convention.class)
                .setParameter("debut", LocalDate.now())
                .setParameter("fin", date)
                .getResultList();
    }

    public List<Convention> findByStatut(Convention.StatutConvention statut) {
        return getEM().createQuery(
                "SELECT c FROM Convention c WHERE c.statut = :statut ORDER BY c.dateSignature DESC",
                Convention.class)
                .setParameter("statut", statut)
                .getResultList();
    }
}