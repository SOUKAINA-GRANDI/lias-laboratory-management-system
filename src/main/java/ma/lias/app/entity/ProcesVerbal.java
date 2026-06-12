package ma.lias.app.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Procès-Verbal d'une réunion du laboratoire LIAS
 *
 * RÈGLES MÉTIER :
 * - Création de la réunion avec ordre du jour
 * - Upload du PV après la réunion
 * - Archivage complet
 * - Accessible selon droits
 */
@Entity
@Table(name = "proces_verbaux")
@NamedQueries({
    @NamedQuery(name = "ProcesVerbal.findAll",
                query = "SELECT p FROM ProcesVerbal p ORDER BY p.dateReunion DESC"),
    @NamedQuery(name = "ProcesVerbal.findByAnnee",
                query = "SELECT p FROM ProcesVerbal p WHERE p.dateReunion BETWEEN :debut AND :fin ORDER BY p.dateReunion DESC"),
    @NamedQuery(name = "ProcesVerbal.findByType",
                query = "SELECT p FROM ProcesVerbal p WHERE p.type = :type ORDER BY p.dateReunion DESC"),
    @NamedQuery(name = "ProcesVerbal.findSansDocument",
                query = "SELECT p FROM ProcesVerbal p WHERE p.cheminPV IS NULL ORDER BY p.dateReunion DESC")
})
public class ProcesVerbal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== INFORMATIONS DE LA RÉUNION ==========

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false, length = 300)
    private String titre;

    @NotNull(message = "Le type est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeReunion type;

    @NotNull(message = "La date de réunion est obligatoire")
    @Column(nullable = false)
    private LocalDate dateReunion;

    @Column(length = 200)
    private String lieu;

    @Column(length = 3000)
    private String ordreJour;           // ordre du jour de la réunion

    @Column(length = 3000)
    private String resumeDecisions;     // résumé des décisions prises

    // ========== PARTICIPANTS ==========

    @ManyToMany
    @JoinTable(
        name = "pv_participants",
        joinColumns = @JoinColumn(name = "pv_id"),
        inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    @OrderBy("nom ASC")
    private List<Membre> participants = new ArrayList<>();

    // ========== DOCUMENT PV ==========

    // Chemin du fichier PV uploadé après la réunion
    @Column(length = 500)
    private String cheminPV;            // null = PV pas encore uploadé

    @Column(length = 255)
    private String nomFichierOriginal;

    private LocalDateTime dateUploadPV; // date d'upload du fichier PV

    // ========== ÉTAT ==========

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutPV statut = StatutPV.PLANIFIE;

    // ========== TRAÇABILITÉ ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cree_par_id")
    private Membre creePar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redige_par_id")
    private Membre redigePar;           // membre qui a rédigé le PV

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    // ========== CONSTRUCTEURS ==========

    public ProcesVerbal() {
        this.dateCreation = LocalDateTime.now();
        this.statut       = StatutPV.PLANIFIE;
        this.participants = new ArrayList<>();
    }

    public ProcesVerbal(String titre, TypeReunion type, LocalDate dateReunion) {
        this();
        this.titre       = titre;
        this.type        = type;
        this.dateReunion = dateReunion;
    }

    // ========== CALLBACK JPA ==========

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // ========== ENUMS INTERNES ==========

    public enum TypeReunion {
        CONSEIL_LABORATOIRE("Conseil de Laboratoire"),
        REUNION_EQUIPE("Réunion d'Équipe"),
        REUNION_DIRECTION("Réunion de Direction"),
        ASSEMBLEE_GENERALE("Assemblée Générale"),
        AUTRE("Autre");

        private final String libelle;
        TypeReunion(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    public enum StatutPV {
        PLANIFIE("Planifié"),       // réunion pas encore tenue
        TENU("Tenu"),               // réunion tenue, PV pas encore uploadé
        ARCHIVE("Archivé");         // PV uploadé et archivé

        private final String libelle;
        StatutPV(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    // ========== MÉTHODES MÉTIER ==========

    public void ajouterParticipant(Membre membre) {
        if (!participants.contains(membre)) {
            participants.add(membre);
        }
    }

    public void retirerParticipant(Membre membre) {
        participants.remove(membre);
    }

    /**
     * Marquer la réunion comme tenue
     */
    public void marquerCommeTenu() {
        this.statut = StatutPV.TENU;
    }

    /**
     * Uploader le PV après la réunion
     */
    public void uploaderPV(String cheminFichier, String nomOriginal, Membre redacteur) {
        this.cheminPV          = cheminFichier;
        this.nomFichierOriginal = nomOriginal;
        this.dateUploadPV      = LocalDateTime.now();
        this.redigePar         = redacteur;
        this.statut            = StatutPV.ARCHIVE;
    }

    public boolean aPVUploade() {
        return cheminPV != null && !cheminPV.isEmpty();
    }

    public boolean estPlanifie() {
        return statut == StatutPV.PLANIFIE;
    }

    public boolean estArchive() {
        return statut == StatutPV.ARCHIVE;
    }

    public int getNombreParticipants() {
        return participants != null ? participants.size() : 0;
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypeReunion getType() { return type; }
    public void setType(TypeReunion type) { this.type = type; }

    public LocalDate getDateReunion() { return dateReunion; }
    public void setDateReunion(LocalDate dateReunion) { this.dateReunion = dateReunion; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getOrdreJour() { return ordreJour; }
    public void setOrdreJour(String ordreJour) { this.ordreJour = ordreJour; }

    public String getResumeDecisions() { return resumeDecisions; }
    public void setResumeDecisions(String resumeDecisions) { this.resumeDecisions = resumeDecisions; }

    public List<Membre> getParticipants() { return participants; }
    public void setParticipants(List<Membre> participants) { this.participants = participants; }

    public String getCheminPV() { return cheminPV; }
    public void setCheminPV(String cheminPV) { this.cheminPV = cheminPV; }

    public String getNomFichierOriginal() { return nomFichierOriginal; }
    public void setNomFichierOriginal(String n) { this.nomFichierOriginal = n; }

    public LocalDateTime getDateUploadPV() { return dateUploadPV; }
    public void setDateUploadPV(LocalDateTime dateUploadPV) { this.dateUploadPV = dateUploadPV; }

    public StatutPV getStatut() { return statut; }
    public void setStatut(StatutPV statut) { this.statut = statut; }

    public Membre getCreePar() { return creePar; }
    public void setCreePar(Membre creePar) { this.creePar = creePar; }

    public Membre getRedigePar() { return redigePar; }
    public void setRedigePar(Membre redigePar) { this.redigePar = redigePar; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime d) { this.dateModification = d; }

    // ========== equals / hashCode / toString ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcesVerbal)) return false;
        ProcesVerbal that = (ProcesVerbal) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return titre + " (" + type.getLibelle() +
               " - " + dateReunion + ")" +
               (aPVUploade() ? " ✓" : " ⏳");
    }
}