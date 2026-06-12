package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.dao.impl.UtilisateurDAO;
import ma.lias.app.entity.Utilisateur;
import ma.lias.app.enums.TypeUtilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class UtilisateurService extends GenericServiceImpl<Utilisateur, Long> {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @Override
    protected GenericDAOImpl<Utilisateur, Long> getDAO() {
        return utilisateurDAO;
    }

    // ========== AUTHENTIFICATION ==========

    /**
     * Authentifier un utilisateur
     * Retourne l'utilisateur si credentials valides, empty sinon
     */
    public Optional<Utilisateur> authentifier(String username, String password) {
        Optional<Utilisateur> opt = utilisateurDAO.authenticate(username);

        if (opt.isEmpty()) return Optional.empty();

        Utilisateur u = opt.get();

        // Compte verrouillé ?
        if (u.estVerrouille()) {
            throw new RuntimeException("Compte verrouillé. Réessayez dans 24h.");
        }

        // Vérification BCrypt
        if (!BCrypt.checkpw(password, u.getPassword())) {
            u.incrementerTentativesEchouees();
            utilisateurDAO.update(u);
            return Optional.empty();
        }

        // Succès
        u.resetTentativesEchouees();
        utilisateurDAO.update(u);
        return Optional.of(u);
    }

    /**
     * Créer un compte avec password hashé
     */
    public void creerCompte(Utilisateur utilisateur, String passwordBrut) {
        String hash = BCrypt.hashpw(passwordBrut, BCrypt.gensalt());
        utilisateur.setPassword(hash);
        utilisateurDAO.save(utilisateur);
    }

    /**
     * Changer le mot de passe
     */
    public void changerPassword(Long userId, String ancienPwd, String nouveauPwd) {
        Utilisateur u = utilisateurDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!BCrypt.checkpw(ancienPwd, u.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        u.setPassword(BCrypt.hashpw(nouveauPwd, BCrypt.gensalt()));
        utilisateurDAO.update(u);
    }

    // ========== GESTION COMPTES ==========

    public Optional<Utilisateur> findByUsername(String username) {
        return utilisateurDAO.findByUsername(username);
    }

    public Optional<Utilisateur> findByMembreId(Long membreId) {
        return utilisateurDAO.findByMembreId(membreId);
    }

    public List<Utilisateur> findByType(TypeUtilisateur type) {
        return utilisateurDAO.findByType(type);
    }

    public void desactiver(Long userId, String motif) {
        utilisateurDAO.findById(userId).ifPresent(u -> {
            u.desactiver(motif);
            utilisateurDAO.update(u);
        });
    }

    public void deverrouiller(Long userId) {
        utilisateurDAO.findById(userId).ifPresent(u -> {
            u.deverrouiller();
            utilisateurDAO.update(u);
        });
    }
}