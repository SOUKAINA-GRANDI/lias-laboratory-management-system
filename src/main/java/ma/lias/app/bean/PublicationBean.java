package ma.lias.app.bean;

import ma.lias.app.entity.Publication;
import ma.lias.app.service.impl.PublicationService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;


@Named( "publicationBean")
@ViewScoped
public class PublicationBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final PublicationService publicationService = new PublicationService();

    private Publication publication = new Publication();
    private List<Publication> publications;
    private String motRecherche;
    private Integer anneeFiltre;

    public void charger() {
        publications = publicationService.findAll();
    }

    public void sauvegarder() {
        try {
            if (publication.getId() == null) {
                publicationService.save(publication);
                addMessage(FacesMessage.SEVERITY_INFO, "Publication ajoutée");
            } else {
                publicationService.update(publication);
                addMessage(FacesMessage.SEVERITY_INFO, "Publication modifiée");
            }
            reinitialiser();
            charger();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void selectionner(Publication p) { this.publication = p; }

    public void supprimer(Publication p) {
        try {
            publicationService.delete(p);
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Publication supprimée");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void rechercher() {
        if (motRecherche != null && !motRecherche.trim().isEmpty()) {
            publications = publicationService.rechercher(motRecherche.trim());
        } else {
            charger();
        }
    }

    public void filtrerParAnnee() {
        if (anneeFiltre != null) {
            publications = publicationService.findByAnnee(anneeFiltre);
        } else {
            charger();
        }
    }

    public void reinitialiser() { publication = new Publication(); }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public Publication getPublication() { return publication; }
    public void setPublication(Publication p) { this.publication = p; }

    public List<Publication> getPublications() {
        if (publications == null) charger();
        return publications;
    }

    public String getMotRecherche() { return motRecherche; }
    public void setMotRecherche(String m) { this.motRecherche = m; }

    public Integer getAnneeFiltre() { return anneeFiltre; }
    public void setAnneeFiltre(Integer a) { this.anneeFiltre = a; }

    public Publication.TypePublication[] getTypes() {
        return Publication.TypePublication.values();
    }
}