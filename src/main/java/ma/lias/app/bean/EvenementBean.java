package ma.lias.app.bean;

import ma.lias.app.entity.Evenement;
import ma.lias.app.service.impl.EvenementService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

@Named("evenementBean")
@ViewScoped
public class EvenementBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final EvenementService evenementService = new EvenementService();

    private Evenement evenement = new Evenement();
    private List<Evenement> evenements;
    private List<Evenement> evenementsAVenir;

    public void charger() {
        evenements       = evenementService.findAll();
        evenementsAVenir = evenementService.findAVenir();
    }

    public void sauvegarder() {
        try {
            if (evenement.getId() == null) {
                evenementService.save(evenement);
                addMessage(FacesMessage.SEVERITY_INFO, "Événement créé");
            } else {
                evenementService.update(evenement);
                addMessage(FacesMessage.SEVERITY_INFO, "Événement modifié");
            }
            reinitialiser();
            charger();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void selectionner(Evenement e) { this.evenement = e; }

    public void terminer(Evenement e) {
        try {
            evenementService.terminer(e.getId());
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Événement terminé");
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + ex.getMessage());
        }
    }

    public void annuler(Evenement e) {
        try {
            evenementService.annuler(e.getId());
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Événement annulé");
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + ex.getMessage());
        }
    }

    public void reinitialiser() { evenement = new Evenement(); }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement e) { this.evenement = e; }

    public List<Evenement> getEvenements() {
        if (evenements == null) charger();
        return evenements;
    }

    public List<Evenement> getEvenementsAVenir() {
        if (evenementsAVenir == null) charger();
        return evenementsAVenir;
    }

    public Evenement.TypeEvenement[] getTypes() {
        return Evenement.TypeEvenement.values();
    }
}