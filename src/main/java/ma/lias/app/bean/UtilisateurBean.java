package ma.lias.app.bean;

import ma.lias.app.entity.Utilisateur;
import ma.lias.app.enums.TypeUtilisateur;
import ma.lias.app.service.impl.UtilisateurService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Named("utilisateurBean")
@ViewScoped

public class UtilisateurBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final UtilisateurService utilisateurService = new UtilisateurService();

    private String username;
    private String password;
    private String ancienPassword;
    private String nouveauPassword;
    private Utilisateur utilisateurConnecte;
    private List<Utilisateur> utilisateurs;

    // ========== AUTHENTIFICATION ==========

    public String connecter() {
        try {
            Optional<Utilisateur> opt =
                    utilisateurService.authentifier(username, password);

            if (opt.isPresent()) {
                utilisateurConnecte = opt.get();
                HttpSession session = getSession();
                session.setAttribute("utilisateurConnecte", utilisateurConnecte);
                session.setAttribute("typeUtilisateur",
                        utilisateurConnecte.getTypeUtilisateur());

                // Redirection selon type
                if (utilisateurConnecte.estAdmin()) {
                    return "/admin/dashboard?faces-redirect=true";
                }
                return "/membre/dashboard?faces-redirect=true";

            } else {
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Identifiants incorrects ou compte verrouillé");
                return null;
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
            return null;
        }
    }

    public String deconnecter() {
        getSession().invalidate();
        return "/index?faces-redirect=true";
    }

    public void changerPassword() {
        try {
            Utilisateur u = (Utilisateur) getSession()
                    .getAttribute("utilisateurConnecte");
            utilisateurService.changerPassword(
                    u.getId(), ancienPassword, nouveauPassword);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Mot de passe modifié avec succès");
            ancienPassword = null;
            nouveauPassword = null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    // ========== GESTION ADMIN ==========

    public void charger() {
        utilisateurs = utilisateurService.findAll();
    }

    public void deverrouiller(Utilisateur u) {
        try {
            utilisateurService.deverrouiller(u.getId());
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Compte déverrouillé");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void desactiver(Utilisateur u) {
        try {
            utilisateurService.desactiver(u.getId(), "Désactivé par admin");
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Compte désactivé");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    // ========== UTILITAIRES ==========

    private HttpSession getSession() {
        return (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
    }

    private void addMessage(FacesMessage.Severity severity, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, msg, null));
    }

    // ========== GETTERS & SETTERS ==========

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAncienPassword() { return ancienPassword; }
    public void setAncienPassword(String ancienPassword) { this.ancienPassword = ancienPassword; }

    public String getNouveauPassword() { return nouveauPassword; }
    public void setNouveauPassword(String nouveauPassword) { this.nouveauPassword = nouveauPassword; }

    public Utilisateur getUtilisateurConnecte() { return utilisateurConnecte; }

    public List<Utilisateur> getUtilisateurs() {
        if (utilisateurs == null) charger();
        return utilisateurs;
    }

    public TypeUtilisateur[] getTypes() { return TypeUtilisateur.values(); }
}