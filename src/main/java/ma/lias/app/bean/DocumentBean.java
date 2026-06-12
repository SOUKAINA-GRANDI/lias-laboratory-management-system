package ma.lias.app.bean;

import ma.lias.app.entity.Document;
import ma.lias.app.service.impl.DocumentService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

@Named("documentBean")
@ViewScoped
public class DocumentBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final DocumentService documentService = new DocumentService();

    private Document document = new Document();
    private List<Document> documents;
    private String motRecherche;

    public void charger() {
        documents = documentService.findAll();
    }

    public void sauvegarder() {
        try {
            if (document.getId() == null) {
                documentService.save(document);
                addMessage(FacesMessage.SEVERITY_INFO, "Document ajouté");
            } else {
                documentService.update(document);
                addMessage(FacesMessage.SEVERITY_INFO, "Document modifié");
            }
            reinitialiser();
            charger();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void selectionner(Document d) { this.document = d; }

    public void supprimer(Document d) {
        try {
            documentService.delete(d);
            charger();
            addMessage(FacesMessage.SEVERITY_INFO, "Document supprimé");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void rechercher() {
        if (motRecherche != null && !motRecherche.trim().isEmpty()) {
            documents = documentService.rechercher(motRecherche.trim());
        } else {
            charger();
        }
    }

    public void filtrerParType(Document.TypeDocument type) {
        documents = documentService.findByType(type);
    }

    public void reinitialiser() { document = new Document(); }

    private void addMessage(FacesMessage.Severity s, String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(s, msg, null));
    }

    public Document getDocument() { return document; }
    public void setDocument(Document d) { this.document = d; }

    public List<Document> getDocuments() {
        if (documents == null) charger();
        return documents;
    }

    public String getMotRecherche() { return motRecherche; }
    public void setMotRecherche(String m) { this.motRecherche = m; }

    public Document.TypeDocument[] getTypes() {
        return Document.TypeDocument.values();
    }
}