package ma.lias.app.dao.impl;

import java.util.List;
import java.util.Optional;

import ma.lias.app.entity.Utilisateur;
import ma.lias.app.enums.TypeUtilisateur;

public class UtilisateurDAO extends GenericDAOImpl<Utilisateur, Long> {

    public Optional<Utilisateur> findByUsername(String username) {
        return getEM().createNamedQuery("Utilisateur.findByUsername", Utilisateur.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public Optional<Utilisateur> findByMembreId(Long membreId) {
        return getEM().createQuery(
                "SELECT u FROM Utilisateur u WHERE u.membre.id = :id",
                Utilisateur.class)
                .setParameter("id", membreId)
                .getResultStream()
                .findFirst();
    }

    public List<Utilisateur> findByType(TypeUtilisateur type) {
        return getEM().createNamedQuery("Utilisateur.findByType", Utilisateur.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Utilisateur> findActifs() {
        return getEM().createNamedQuery("Utilisateur.findAll", Utilisateur.class)
                .getResultList();
    }

    public Optional<Utilisateur> authenticate(String username) {
        return getEM().createNamedQuery("Utilisateur.authenticate", Utilisateur.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}