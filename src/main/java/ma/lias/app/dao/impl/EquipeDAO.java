package ma.lias.app.dao.impl;

import ma.lias.app.entity.Equipe;

import java.util.List;
import java.util.Optional;

public class EquipeDAO extends GenericDAOImpl<Equipe, Long> {

    public Optional<Equipe> findByNom(String nom) {
        return getEM().createQuery(
                "SELECT e FROM Equipe e WHERE e.nom = :nom", Equipe.class)
                .setParameter("nom", nom)
                .getResultStream()
                .findFirst();
    }

    public List<Equipe> findActives() {
        return getEM().createQuery(
                "SELECT e FROM Equipe e WHERE e.active = true ORDER BY e.nom",
                Equipe.class)
                .getResultList();
    }

    public List<Equipe> findInactives() {
        return getEM().createQuery(
                "SELECT e FROM Equipe e WHERE e.active = false ORDER BY e.nom",
                Equipe.class)
                .getResultList();
    }
}