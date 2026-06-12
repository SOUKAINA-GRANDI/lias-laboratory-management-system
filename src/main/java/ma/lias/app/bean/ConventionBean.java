package ma.lias.app.bean;

import ma.lias.app.entity.Convention;
import ma.lias.app.entity.Membre;
import ma.lias.app.service.impl.ConventionService;
import ma.lias.app.service.impl.MembreService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named("conventionBean")
@ViewScoped
public class ConventionBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ConventionService conventionService = new ConventionService();
    private final MembreService     membreService     = new MembreService();

    private Convention convention = new Convention();
    private List<Convention> conventions;
    private List<Membre> membres;
    private String motRecherche;
    private LocalDate nouvelleDateExpiration;

    public void charger() {
        conventions = conventionService.findActives();
        membres     = membreService.findActifs();
    }

    public void sauvegarder() {
        try {
            if (convention.getId() == null) {
                conventionService.save(convention);
                addMessage(FacesMessage.SEVERITY_INFO, "Convention créée");
            } else {
                conventionService.update(convention);
                addMessage(FacesMessage.SEVERITY_INFO, "Convention modifiée");
            }
            reinitialiser();
            charger();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void selectionner(Convention c) { this.convention = c; }

    public void suspendre(Convention c) {
        try {
            conventionService.suspendre(c.getId());
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Convention suspendue");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void resilier(Convention c) {
        try {
            conventionService.resilier(c.getId());
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Convention résiliée");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void renouveler(Convention c) {
        try {
            conventionService.renouveler(c.getId(), nouvelleDateExpiration);
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Convention renouvelée");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void rechercherParPartenaire() {
        if (motRecherche != null && !motRecherche.trim().isEmpty()) {
            conventions = conventionService.findByPartenaire(motRecherche.trim());
        } else {
            charger();
        }
    }

    public void alertesExpiration() {
        // Conventions expirant dans 30 jours
        conventions = conventionService.findExpirantDans(30);
    }

    public void reinitialiser() { convention = new Convention(); }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public Convention getConvention() { return convention; }
    public void setConvention(Convention c) { this.convention = c; }

    public List<Convention> getConventions() {
        if (conventions == null) charger();
        return conventions;
    }

    public List<Membre> getMembres() {
        if (membres == null) membres = membreService.findActifs();
        return membres;
    }

    public String getMotRecherche() { return motRecherche; }
    public void setMotRecherche(String m) { this.motRecherche = m; }

    public LocalDate getNouvelleDateExpiration() { return nouvelleDateExpiration; }
    public void setNouvelleDateExpiration(LocalDate d) { this.nouvelleDateExpiration = d; }

    public Convention.TypeConvention[] getTypes() {
        return Convention.TypeConvention.values();
    }

    public Convention.StatutConvention[] getStatuts() {
        return Convention.StatutConvention.values();
    }
}