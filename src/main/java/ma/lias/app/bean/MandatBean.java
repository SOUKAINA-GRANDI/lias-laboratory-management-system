package ma.lias.app.bean;

import ma.lias.app.entity.Mandat;
import ma.lias.app.entity.Membre;
import ma.lias.app.service.impl.MandatService;
import ma.lias.app.service.impl.MembreService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Named("mandatBean")
@ViewScoped
public class MandatBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final MandatService mandatService   = new MandatService();
    private final MembreService membreService   = new MembreService();

    private Mandat mandatActif;
    private List<Mandat> mandats;
    private List<Membre> membres;
    private Long directeurId;
    private Long viceDirecteurId;
    private LocalDate dateDebut = LocalDate.now();

    public void charger() {
        mandats  = mandatService.findTous();
        membres  = membreService.findActifs();
        Optional<Mandat> opt = mandatService.findMandatActif();
        mandatActif = opt.orElse(null);
    }

    public void creerMandat() {
        try {
            Membre directeur = membreService.findById(directeurId)
                    .orElseThrow(() -> new RuntimeException("Directeur introuvable"));
            Membre vice = viceDirecteurId != null
                    ? membreService.findById(viceDirecteurId).orElse(null)
                    : null;

            mandatService.creerMandat(directeur, vice, dateDebut);
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Mandat créé avec succès");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void terminerMandat() {
        try {
            if (mandatActif != null) {
                mandatService.terminerMandat(mandatActif.getId());
                charger();
                addMessage(FacesMessage.SEVERITY_INFO, "Mandat terminé");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public Mandat getMandatActif() { return mandatActif; }
    public List<Mandat> getMandats() {
        if (mandats == null) charger();
        return mandats;
    }
    public List<Membre> getMembres() {
        if (membres == null) membres = membreService.findActifs();
        return membres;
    }
    public Long getDirecteurId() { return directeurId; }
    public void setDirecteurId(Long directeurId) { this.directeurId = directeurId; }
    public Long getViceDirecteurId() { return viceDirecteurId; }
    public void setViceDirecteurId(Long v) { this.viceDirecteurId = v; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
}