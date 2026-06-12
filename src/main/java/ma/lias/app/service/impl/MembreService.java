package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.dao.impl.MembreDAO;
import ma.lias.app.entity.Membre;
import ma.lias.app.enums.StatutMembre;

import java.util.List;
import java.util.Optional;

public class MembreService extends GenericServiceImpl<Membre, Long> {

    private final MembreDAO membreDAO = new MembreDAO();

    @Override
    protected GenericDAOImpl<Membre, Long> getDAO() {
        return membreDAO;
    }

    // ========== MÉTHODES MÉTIER ==========

    public Optional<Membre> findByEmail(String email) {
        return membreDAO.findByEmail(email);
    }

    public List<Membre> findByStatut(StatutMembre statut) {
        return membreDAO.findByStatut(statut);
    }

    public List<Membre> findActifs() {
        return membreDAO.findActifs();
    }

    public List<Membre> findByEquipe(Long equipeId) {
        return membreDAO.findByEquipe(equipeId);
    }

    public List<Membre> rechercher(String mot) {
        return membreDAO.rechercher(mot);
    }

    public long countByStatut(StatutMembre statut) {
        return membreDAO.countByStatut(statut);
    }

    /**
     * Désactiver un membre sans le supprimer
     */
    public void desactiver(Long membreId, String motif) {
        membreDAO.findById(membreId).ifPresent(m -> {
            m.desactiver(motif);
            membreDAO.update(m);
        });
    }

    /**
     * Mettre en retraite
     */
    public void mettreEnRetraite(Long membreId) {
        membreDAO.findById(membreId).ifPresent(m -> {
            m.mettreEnRetraite();
            membreDAO.update(m);
        });
    }

    /**
     * Réactiver un ancien membre
     */
    public void reactiver(Long membreId, StatutMembre nouveauStatut) {
        membreDAO.findById(membreId).ifPresent(m -> {
            m.reactiver(nouveauStatut);
            membreDAO.update(m);
        });
    }

    /**
     * Changer l'équipe d'un membre
     */
    public void changerEquipe(Membre membre, ma.lias.app.entity.Equipe equipe) {
        membre.setEquipeActuelle(equipe);
        membreDAO.update(membre);
    }
}