package ma.lias.app.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Document archivé du laboratoire LIAS
 *
 * RÈGLES MÉTIER :
 * - Structuré par événement et par date
 * - Types : demandes financement, programmes, attestations, rapports, admin
 * - Fonctionnalités : upload, téléchargement, recherche, historisation
 * - Accessible selon droits utilisateur
 */
@Entity
@Table(name = "documents")
@NamedQueries({
    @NamedQuery(name = "Document.findAll",
                query = "SELECT d FROM Document d ORDER BY d.dateUpload DESC"),
    @NamedQuery(name = "Document.findByType",
                query = "SELECT d FROM Document d WHERE d.type = :type ORDER BY d.dateUpload DESC"),
    @NamedQuery(name = "Document.findByEvenement",
                query = "SELECT d FROM Document d WHERE d.evenement.id = :evenementId ORDER BY d.dateUpload DESC"),
    @NamedQuery(name = "Document.findByMembre",
                query = "SELECT d FROM Document d WHERE d.uploadePar.id = :membreId ORDER BY d.dateUpload DESC"),
    @NamedQuery(name = "Document.findByAnnee",
                query = "SELECT d FROM Document d WHERE d.dateUpload BETWEEN :debut AND :fin ORDER BY d.dateUpload DESC"),
    @NamedQuery(name = "Document.recherche",
                query = "SELECT d FROM Document d WHERE LOWER(d.nom) LIKE :mot OR LOWER(d.description) LIKE :mot ORDER BY d.dateUpload DESC")
})
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== INFORMATIONS PRINCIPALES ==========

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 255)
    private String nom;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "Le type est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeDocument type;

    // ========== FICHIER ==========

    @NotBlank(message = "Le chemin du fichier est obligatoire")
    @Column(nullable = false, length = 500)
    private String cheminFichier;       // chemin physique sur serveur

    @Column(length = 20)
    private String formatFichier;       // PDF, DOCX, XLSX...

    private Long tailleFichier;         // en octets

    @Column(length = 255)
    private String nomFichierOriginal;  // nom original lors de l'upload

    // ========== ASSOCIATIONS ==========

    // Lié à un événement (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    // Lié à une convention (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convention_id")
    private Convention convention;

    // Lié à un PV (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pv_id")
    private ProcesVerbal procesVerbal;

    // ========== TRAÇABILITÉ ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploade_par_id")
    private Membre uploadePar;

    @Column(nullable = false)
    private LocalDateTime dateUpload;

    private LocalDateTime dateModification;

    @Column(length = 500)
    private String notes;

    // ========== VISIBILITÉ ==========

    @Column(nullable = false)
    private Boolean publicVisible = false; // visible par les visiteurs ?

    // ========== CONSTRUCTEURS ==========

    public Document() {
        this.dateUpload   = LocalDateTime.now();
        this.publicVisible = false;
    }

    public Document(String nom, TypeDocument type, String cheminFichier) {
        this();
        this.nom          = nom;
        this.type         = type;
        this.cheminFichier = cheminFichier;
    }

    // ========== CALLBACK JPA ==========

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // ========== ENUM INTERNE ==========

    public enum TypeDocument {
        DEMANDE_FINANCEMENT("Demande de Financement"),
        PROGRAMME("Programme"),
        ATTESTATION("Attestation"),
        RAPPORT("Rapport"),
        ADMINISTRATIF("Document Administratif"),
        PROCES_VERBAL("Procès-Verbal"),
        CONVENTION("Convention"),
        PUBLICATION("Publication"),
        AUTRE("Autre");

        private final String libelle;
        TypeDocument(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    // ========== MÉTHODES MÉTIER ==========

    public String getTailleFormatee() {
        if (tailleFichier == null) return "Inconnue";
        if (tailleFichier < 1024)
            return tailleFichier + " o";
        if (tailleFichier < 1024 * 1024)
            return String.format("%.1f Ko", tailleFichier / 1024.0);
        return String.format("%.1f Mo", tailleFichier / (1024.0 * 1024));
    }

    public boolean estLieAEvenement() {
        return evenement != null;
    }

    public boolean estLieAConvention() {
        return convention != null;
    }

    public boolean estLieAPV() {
        return procesVerbal != null;
    }

    public boolean estPublic() {
        return publicVisible != null && publicVisible;
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TypeDocument getType() { return type; }
    public void setType(TypeDocument type) { this.type = type; }

    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }

    public String getFormatFichier() { return formatFichier; }
    public void setFormatFichier(String formatFichier) { this.formatFichier = formatFichier; }

    public Long getTailleFichier() { return tailleFichier; }
    public void setTailleFichier(Long tailleFichier) { this.tailleFichier = tailleFichier; }

    public String getNomFichierOriginal() { return nomFichierOriginal; }
    public void setNomFichierOriginal(String n) { this.nomFichierOriginal = n; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    public Convention getConvention() { return convention; }
    public void setConvention(Convention convention) { this.convention = convention; }

    public ProcesVerbal getProcesVerbal() { return procesVerbal; }
    public void setProcesVerbal(ProcesVerbal procesVerbal) { this.procesVerbal = procesVerbal; }

    public Membre getUploadePar() { return uploadePar; }
    public void setUploadePar(Membre uploadePar) { this.uploadePar = uploadePar; }

    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime d) { this.dateModification = d; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getPublicVisible() { return publicVisible; }
    public void setPublicVisible(Boolean publicVisible) { this.publicVisible = publicVisible; }

    // ========== equals / hashCode / toString ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document that = (Document) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return nom + " (" + type.getLibelle() + " - " +
               (formatFichier != null ? formatFichier : "?") + ")";
    }
}