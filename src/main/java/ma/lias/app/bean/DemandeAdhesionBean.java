package ma.lias.app.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import ma.lias.app.entity.DemandeAdhesion;
import ma.lias.app.entity.Mandat;
import ma.lias.app.entity.Membre;
import ma.lias.app.enums.StatutMembre;
import ma.lias.app.service.impl.DemandeAdhesionService;
import ma.lias.app.service.impl.MandatService;

@Named("demandeAdhesionBean")
@ViewScoped
public class DemandeAdhesionBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final DemandeAdhesionService demandeService = new DemandeAdhesionService();
    private final MandatService          mandatService  = new MandatService();

    private DemandeAdhesion demande = new DemandeAdhesion();
    private List<DemandeAdhesion> demandes;
    private String motifRefus;
    private String passwordProvisoire;

    public void charger() {
        demandes = demandeService.findEnAttente();
    }

    // ========== SOUMISSION PUBLIQUE ==========

    public String soumettre() {
        try {
            demandeService.save(demande);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Votre demande a été soumise. Vous serez notifié par email.");
            demande = new DemandeAdhesion();
            return "/public/confirmation?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
            return null;
        }
    }

    // ========== DÉCISIONS DIRECTEUR ==========

    public void accepter(DemandeAdhesion d) {
        try {
            Membre decideur  = getMembreConnecte();
            Mandat mandat    = mandatService.findMandatActif()
                    .orElseThrow(() -> new RuntimeException("Aucun mandat actif"));

            demandeService.accepter(d.getId(), decideur, mandat,
                    passwordProvisoire != null ? passwordProvisoire : "Lias@2025");

            charger();
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Demande acceptée — compte créé pour " + d.getNomCompletCandidat());
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void refuser(DemandeAdhesion d) {
        try {
            Membre decideur = getMembreConnecte();
            Mandat mandat   = mandatService.findMandatActif()
                    .orElseThrow(() -> new RuntimeException("Aucun mandat actif"));

            demandeService.refuser(d.getId(), decideur, mandat, motifRefus);
            charger();
            motifRefus = null;
            addMessage(FacesMessage.SEVERITY_INFO, "Demande refusée");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    // ========== UTILITAIRES ==========

    private Membre getMembreConnecte() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session == null) throw new RuntimeException("Session expirée");
        Membre u = (Membre) session.getAttribute("membreConnecte");
        if (u == null) throw new RuntimeException("Utilisateur non connecté");
        return u;
    }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public DemandeAdhesion getDemande() { return demande; }
    public void setDemande(DemandeAdhesion d) { this.demande = d; }

    public List<DemandeAdhesion> getDemandes() {
        if (demandes == null) charger();
        return demandes;
    }

    public String getMotifRefus() { return motifRefus; }
    public void setMotifRefus(String m) { this.motifRefus = m; }

    public String getPasswordProvisoire() { return passwordProvisoire; }
    public void setPasswordProvisoire(String p) { this.passwordProvisoire = p; }

    public StatutMembre[] getStatuts() { return StatutMembre.values(); }
}