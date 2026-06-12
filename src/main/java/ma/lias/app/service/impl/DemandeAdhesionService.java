package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.DemandeAdhesionDAO;
import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.dao.impl.MembreDAO;
import ma.lias.app.dao.impl.UtilisateurDAO;
import ma.lias.app.entity.DemandeAdhesion;
import ma.lias.app.entity.Mandat;
import ma.lias.app.entity.Membre;
import ma.lias.app.entity.Utilisateur;
import ma.lias.app.enums.StatutDemande;
import ma.lias.app.enums.TypeUtilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class DemandeAdhesionService extends GenericServiceImpl<DemandeAdhesion, Long> {

    private final DemandeAdhesionDAO demandeDAO  = new DemandeAdhesionDAO();
    private final MembreDAO          membreDAO   = new MembreDAO();
    private final UtilisateurDAO     utilisateurDAO = new UtilisateurDAO();

    @Override
    protected GenericDAOImpl<DemandeAdhesion, Long> getDAO() {
        return demandeDAO;
    }

    public List<DemandeAdhesion> findEnAttente() {
        return demandeDAO.findEnAttente();
    }

    public List<DemandeAdhesion> findByStatut(StatutDemande statut) {
        return demandeDAO.findByStatut(statut);
    }

    public Optional<DemandeAdhesion> findByEmail(String email) {
        return demandeDAO.findByEmail(email);
    }

    /**
     * Accepter → crée automatiquement Membre + Utilisateur
     */
    public void accepter(Long demandeId, Membre decideur,
                         Mandat mandat, String passwordProvisoire) {
        demandeDAO.findById(demandeId).ifPresent(d -> {

            // 1. Créer le Membre
            Membre nouveau = new Membre(
                d.getNomCandidat(),
                d.getPrenomCandidat(),
                d.getEmailCandidat(),
                d.getStatutDemande()
            );
            nouveau.setEtablissementOrigine(d.getEtablissementOrigine());
            nouveau.setLaboratoireOrigine(d.getLaboratoireOrigine());
            nouveau.setGrade(d.getGrade());
            nouveau.setSpecialite(d.getSpecialite());
            membreDAO.save(nouveau);

            // 2. Créer le compte Utilisateur
            Utilisateur compte = new Utilisateur(
                nouveau,
                d.getEmailCandidat(),
                BCrypt.hashpw(passwordProvisoire, BCrypt.gensalt()),
                TypeUtilisateur.MEMBRE_PERMANENT
            );
            utilisateurDAO.save(compte);

            // 3. Mettre à jour la demande
            d.accepter(decideur, mandat, nouveau);
            demandeDAO.update(d);
        });
    }

    /**
     * Refuser avec motif obligatoire
     */
    public void refuser(Long demandeId, Membre decideur,
                        Mandat mandat, String motif) {
        if (motif == null || motif.trim().isEmpty()) {
            throw new RuntimeException("Le motif de refus est obligatoire");
        }
        demandeDAO.findById(demandeId).ifPresent(d -> {
            d.refuser(decideur, mandat, motif);
            demandeDAO.update(d);
        });
    }

    /**
     * Annuler par le candidat
     */
    public void annuler(Long demandeId) {
        demandeDAO.findById(demandeId).ifPresent(d -> {
            d.annuler();
            demandeDAO.update(d);
        });
    }
}