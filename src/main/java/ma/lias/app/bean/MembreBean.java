package ma.lias.app.bean;

import ma.lias.app.entity.Membre;
import ma.lias.app.enums.StatutMembre;
import ma.lias.app.service.impl.MembreService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

@Named("membreBeanc")
@ViewScoped
public class MembreBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final MembreService membreService = new MembreService();

    private Membre membre = new Membre();
    private Membre membreSelectionne;
    private List<Membre> membres;
    private String motRecherche;
    private StatutMembre statutFiltre;

    // ========== ACTIONS ==========

    public void charger() {
        membres = membreService.findActifs();
    }

    public String sauvegarder() {
        try {
            if (membre.getId() == null) {
                membreService.save(membre);
                addMessage(FacesMessage.SEVERITY_INFO, "Membre ajouté avec succès");
            } else {
                membreService.update(membre);
                addMessage(FacesMessage.SEVERITY_INFO, "Membre modifié avec succès");
            }
            reinitialiser();
            membres = membreService.findActifs();
            return null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
            return null;
        }
    }

    public void selectionner(Membre m) {
        this.membreSelectionne = m;
        this.membre = m;
    }

    public void desactiver(Membre m) {
        try {
            membreService.desactiver(m.getId(), "Désactivé via interface");
            membres = membreService.findActifs();
            addMessage(FacesMessage.SEVERITY_INFO, "Membre désactivé");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void mettreEnRetraite(Membre m) {
        try {
            membreService.mettreEnRetraite(m.getId());
            membres = membreService.findActifs();
            addMessage(FacesMessage.SEVERITY_INFO, "Membre mis en retraite");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void rechercher() {
        if (motRecherche != null && !motRecherche.trim().isEmpty()) {
            membres = membreService.rechercher(motRecherche.trim());
        } else {
            membres = membreService.findActifs();
        }
    }

    public void filtrerParStatut() {
        if (statutFiltre != null) {
            membres = membreService.findByStatut(statutFiltre);
        } else {
            membres = membreService.findActifs();
        }
    }

    public void reinitialiser() {
        membre = new Membre();
        membreSelectionne = null;
    }

    // ========== UTILITAIRES ==========

    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, message, null));
    }

    // ========== GETTERS & SETTERS ==========

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public Membre getMembreSelectionne() { return membreSelectionne; }
    public void setMembreSelectionne(Membre m) { this.membreSelectionne = m; }

    public List<Membre> getMembres() {
        if (membres == null) charger();
        return membres;
    }

    public String getMotRecherche() { return motRecherche; }
    public void setMotRecherche(String motRecherche) { this.motRecherche = motRecherche; }

    public StatutMembre getStatutFiltre() { return statutFiltre; }
    public void setStatutFiltre(StatutMembre statutFiltre) { this.statutFiltre = statutFiltre; }

    public StatutMembre[] getStatuts() { return StatutMembre.values(); }
}