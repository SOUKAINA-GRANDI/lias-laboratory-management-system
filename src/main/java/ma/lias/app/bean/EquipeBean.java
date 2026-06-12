package ma.lias.app.bean;

import ma.lias.app.entity.Equipe;
import ma.lias.app.service.impl.EquipeService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

@Named( "equipeBean")
@ViewScoped
public class EquipeBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final EquipeService equipeService = new EquipeService();

    private Equipe equipe = new Equipe();
    private List<Equipe> equipes;

    public void charger() {
        equipes = equipeService.findActives();
    }

    public void sauvegarder() {
        try {
            if (equipe.getId() == null) {
                equipeService.save(equipe);
                addMessage(FacesMessage.SEVERITY_INFO, "Équipe créée");
            } else {
                equipeService.update(equipe);
                addMessage(FacesMessage.SEVERITY_INFO, "Équipe modifiée");
            }
            reinitialiser();
            charger();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void selectionner(Equipe e) { this.equipe = e; }

    public void fermer(Equipe e) {
        try {
            equipeService.fermer(e.getId());
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Équipe fermée");
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + ex.getMessage());
        }
    }

    public void reinitialiser() { equipe = new Equipe(); }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public List<Equipe> getEquipes() {
        if (equipes == null) charger();
        return equipes;
    }
}