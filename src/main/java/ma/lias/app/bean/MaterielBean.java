package ma.lias.app.bean;

import ma.lias.app.entity.Materiel;
import ma.lias.app.entity.Membre;
import ma.lias.app.service.impl.MaterielService;
import ma.lias.app.service.impl.MembreService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Named("materielBean")
@ViewScoped
public class MaterielBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final MaterielService materielService = new MaterielService();
    private final MembreService   membreService   = new MembreService();

    private Materiel materiel = new Materiel();
    private List<Materiel> materiels;
    private List<Membre> membres;
    private Long membreAttributionId;

    public void charger() {
        materiels = materielService.findAll();
        membres   = membreService.findActifs();
    }

    public void sauvegarder() {
        try {
            if (materiel.getId() == null) {
                materielService.save(materiel);
                addMessage(FacesMessage.SEVERITY_INFO, "Matériel ajouté");
            } else {
                materielService.update(materiel);
                addMessage(FacesMessage.SEVERITY_INFO, "Matériel modifié");
            }
            reinitialiser();
            charger();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void selectionner(Materiel m) { this.materiel = m; }

    public void attribuer(Materiel m) {
        try {
            Membre beneficiaire = membreService.findById(membreAttributionId)
                    .orElseThrow(() -> new RuntimeException("Membre introuvable"));
            Membre validePar = getMembreConnecte();

            boolean ok = materielService.attribuer(m.getId(), beneficiaire, validePar);
            if (ok) {
                charger();
                addMessage(FacesMessage.SEVERITY_INFO,
                        "Matériel attribué à " + beneficiaire.getNomComplet());
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "Matériel non disponible");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void recuperer(Materiel m) {
        try {
            Membre membre = membreService.findById(membreAttributionId)
                    .orElseThrow(() -> new RuntimeException("Membre introuvable"));
            materielService.recuperer(m.getId(), membre);
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Matériel récupéré");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void filtrerDisponibles() {
        materiels = materielService.findDisponibles();
    }

    public void filtrerParType(Materiel.TypeMateriel type) {
        materiels = materielService.findByType(type);
    }

    public void reinitialiser() { materiel = new Materiel(); }

    private Membre getMembreConnecte() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        return session != null ? (Membre) session.getAttribute("membreConnecte") : null;
    }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel m) { this.materiel = m; }

    public List<Materiel> getMateriels() {
        if (materiels == null) charger();
        return materiels;
    }

    public List<Membre> getMembres() {
        if (membres == null) membres = membreService.findActifs();
        return membres;
    }

    public Long getMembreAttributionId() { return membreAttributionId; }
    public void setMembreAttributionId(Long id) { this.membreAttributionId = id; }

    public Materiel.TypeMateriel[] getTypes() {
        return Materiel.TypeMateriel.values();
    }
}