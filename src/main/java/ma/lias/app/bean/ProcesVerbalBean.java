package ma.lias.app.bean;

import ma.lias.app.entity.Membre;
import ma.lias.app.entity.ProcesVerbal;
import ma.lias.app.service.impl.MembreService;
import ma.lias.app.service.impl.ProcesVerbalService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Named("procesVerbalBean")
@ViewScoped
public class ProcesVerbalBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ProcesVerbalService pvService     = new ProcesVerbalService();
    private final MembreService       membreService = new MembreService();

    private ProcesVerbal procesVerbal = new ProcesVerbal();
    private List<ProcesVerbal> procesVerbaux;
    private List<Membre> membres;

    // upload PV
    private String cheminFichierPV;
    private String nomFichierOriginal;

    public void charger() {
        procesVerbaux = pvService.findAll();
        membres       = membreService.findActifs();
    }

    public void sauvegarder() {
        try {
            if (procesVerbal.getId() == null) {
                pvService.save(procesVerbal);
                addMessage(FacesMessage.SEVERITY_INFO, "Réunion créée");
            } else {
                pvService.update(procesVerbal);
                addMessage(FacesMessage.SEVERITY_INFO, "Réunion modifiée");
            }
            reinitialiser();
            charger();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void selectionner(ProcesVerbal pv) { this.procesVerbal = pv; }

    public void marquerCommeTenu(ProcesVerbal pv) {
        try {
            pvService.marquerCommeTenu(pv.getId());
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Réunion marquée comme tenue");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void uploaderPV(ProcesVerbal pv) {
        try {
            Membre redacteur = getMembreConnecte();
            pvService.uploaderPV(
                    pv.getId(),
                    cheminFichierPV,
                    nomFichierOriginal,
                    redacteur);
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "PV uploadé avec succès");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void filtrerSansDocument() {
        procesVerbaux = pvService.findSansDocument();
    }

    public void filtrerParStatut(ProcesVerbal.StatutPV statut) {
        procesVerbaux = pvService.findByStatut(statut);
    }

    public void reinitialiser() { procesVerbal = new ProcesVerbal(); }

    private Membre getMembreConnecte() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        return session != null ? (Membre) session.getAttribute("membreConnecte") : null;
    }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public ProcesVerbal getProcesVerbal() { return procesVerbal; }
    public void setProcesVerbal(ProcesVerbal pv) { this.procesVerbal = pv; }

    public List<ProcesVerbal> getProcesVerbaux() {
        if (procesVerbaux == null) charger();
        return procesVerbaux;
    }

    public List<Membre> getMembres() {
        if (membres == null) membres = membreService.findActifs();
        return membres;
    }

    public String getCheminFichierPV() { return cheminFichierPV; }
    public void setCheminFichierPV(String c) { this.cheminFichierPV = c; }

    public String getNomFichierOriginal() { return nomFichierOriginal; }
    public void setNomFichierOriginal(String n) { this.nomFichierOriginal = n; }

    public ProcesVerbal.TypeReunion[] getTypes() {
        return ProcesVerbal.TypeReunion.values();
    }

    public ProcesVerbal.StatutPV[] getStatuts() {
        return ProcesVerbal.StatutPV.values();
    }
}