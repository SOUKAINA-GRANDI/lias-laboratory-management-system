package ma.lias.app.entity;

import ma.lias.app.enums.StatutMembre;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;

/**
 * Entité représentant l'historique des affiliations d'un membre au laboratoire
 * 
 * RÈGLES MÉTIER (Point 4.3 du cahier des charges) :
 * - Un membre peut quitter et revenir au laboratoire
 * - Chaque période d'affiliation est historisée avec date début/fin
 * - Une seule période peut être active à la fois (dateFin = NULL)
 * - Le motif de départ est obligatoire si dateFin != NULL
 */
@Entity
@Table(name = "affiliation_historique")
@NamedQueries({
    @NamedQuery(name = "AffiliationHistorique.findByMembre", 
                query = "SELECT a FROM AffiliationHistorique a WHERE a.membre.id = :membreId ORDER BY a.dateDebut DESC"),
    @NamedQuery(name = "AffiliationHistorique.findActive", 
                query = "SELECT a FROM AffiliationHistorique a WHERE a.membre.id = :membreId AND a.periodeActive = true"),
    @NamedQuery(name = "AffiliationHistorique.findByPeriode", 
                query = "SELECT a FROM AffiliationHistorique a WHERE a.dateDebut >= :dateDebut AND (a.dateFin <= :dateFin OR a.dateFin IS NULL)"),
    @NamedQuery(name = "AffiliationHistorique.countAffiliations", 
                query = "SELECT COUNT(a) FROM AffiliationHistorique a WHERE a.membre.id = :membreId")
})
public class AffiliationHistorique {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ========== RELATION AVEC MEMBRE ==========
    
    @NotNull(message = "Le membre est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;
    
    // ========== PÉRIODE D'AFFILIATION ==========
    
    @NotNull(message = "La date de début est obligatoire")
    @Column(nullable = false)
    private LocalDate dateDebut;
    
    @Column
    private LocalDate dateFin; // NULL si période active
    
    // ========== STATUT PENDANT LA PÉRIODE ==========
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutMembre statutPendantPeriode;
    
    // ========== INFORMATIONS COMPLÉMENTAIRES ==========
    
    @Column(length = 100)
    private String laboratoireOrigine;
    
    @Column(length = 100)
    private String etablissementOrigine;
    
    @Column(length = 500)
    private String motifDepart; // Obligatoire si dateFin != NULL
    
    @Column(nullable = false)
    private Boolean periodeActive = true; // true si dateFin == NULL
    
    // ========== MÉTADONNÉES ==========
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private java.util.Date dateCreation;
    
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date dateModification;
    
    @Column(length = 500)
    private String notes;
    
    // ========== CONSTRUCTEURS ==========
    
    public AffiliationHistorique() {
        this.dateCreation = new java.util.Date();
        this.periodeActive = true;
    }
    
    public AffiliationHistorique(Membre membre, LocalDate dateDebut, StatutMembre statut) {
        this();
        this.membre = membre;
        this.dateDebut = dateDebut;
        this.statutPendantPeriode = statut;
    }
    
    public AffiliationHistorique(Membre membre, LocalDate dateDebut, LocalDate dateFin, 
                                  StatutMembre statut, String motifDepart) {
        this(membre, dateDebut, statut);
        this.dateFin = dateFin;
        this.motifDepart = motifDepart;
        this.periodeActive = false;
    }
    
    
 // ========== CALLBACKS JPA ==========

    @PrePersist
    protected void onCreate() {
        if (this.dateCreation == null) {
            this.dateCreation = new java.util.Date();
        }
        // Validation à la création
        validateDatesAndStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = new java.util.Date();
        // Validation à la mise à jour
        validateDatesAndStatus();
    }

    /**
     * Méthode privée de validation (appelée par les callbacks)
     */
    private void validateDatesAndStatus() {
        // Vérifier cohérence des dates
        if (dateFin != null && dateDebut != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalStateException("La date de fin ne peut pas être avant la date de début");
        }
        
        // Mettre à jour periodeActive automatiquement
        this.periodeActive = (dateFin == null);
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Membre getMembre() {
        return membre;
    }
    
    public void setMembre(Membre membre) {
        this.membre = membre;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
        this.periodeActive = (dateFin == null);
        this.dateModification = new java.util.Date();
    }
    
    public StatutMembre getStatutPendantPeriode() {
        return statutPendantPeriode;
    }
    
    public void setStatutPendantPeriode(StatutMembre statutPendantPeriode) {
        this.statutPendantPeriode = statutPendantPeriode;
    }
    
    public String getLaboratoireOrigine() {
        return laboratoireOrigine;
    }
    
    public void setLaboratoireOrigine(String laboratoireOrigine) {
        this.laboratoireOrigine = laboratoireOrigine;
    }
    
    public String getEtablissementOrigine() {
        return etablissementOrigine;
    }
    
    public void setEtablissementOrigine(String etablissementOrigine) {
        this.etablissementOrigine = etablissementOrigine;
    }
    
    public String getMotifDepart() {
        return motifDepart;
    }
    
    public void setMotifDepart(String motifDepart) {
        this.motifDepart = motifDepart;
    }
    
    public Boolean getPeriodeActive() {
        return periodeActive;
    }
    
    public void setPeriodeActive(Boolean periodeActive) {
        this.periodeActive = periodeActive;
    }
    
    public java.util.Date getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(java.util.Date dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public java.util.Date getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(java.util.Date dateModification) {
        this.dateModification = dateModification;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // ========== MÉTHODES MÉTIER ==========
    
    /**
     * Calculer la durée de l'affiliation en années
     */
    public Integer getDureeEnAnnees() {
        if (dateDebut == null) return null;
        LocalDate fin = dateFin != null ? dateFin : LocalDate.now();
        return Period.between(dateDebut, fin).getYears();
    }
    
    /**
     * Calculer la durée de l'affiliation en mois
     */
    public Integer getDureeEnMois() {
        if (dateDebut == null) return null;
        LocalDate fin = dateFin != null ? dateFin : LocalDate.now();
        Period period = Period.between(dateDebut, fin);
        return period.getYears() * 12 + period.getMonths();
    }
    
    /**
     * Vérifier si la période est en cours
     */
    public boolean estEnCours() {
        return periodeActive && dateFin == null;
    }
    
    /**
     * Vérifier si la période est terminée
     */
    public boolean estTerminee() {
        return !periodeActive && dateFin != null;
    }
    
    /**
     * Clôturer la période d'affiliation
     */
    public void cloturerPeriode(LocalDate dateFin, String motif) {
        if (dateFin.isBefore(this.dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être avant la date de début");
        }
        this.dateFin = dateFin;
        this.motifDepart = motif;
        this.periodeActive = false;
        this.dateModification = new java.util.Date();
    }
    
    /**
     * Réactiver une période (annuler la clôture)
     */
    public void reactiver() {
        this.dateFin = null;
        this.motifDepart = null;
        this.periodeActive = true;
        this.dateModification = new java.util.Date();
    }
    
    /**
     * Vérifier si une date est dans cette période
     */
    public boolean contientDate(LocalDate date) {
        if (date.isBefore(dateDebut)) return false;
        if (dateFin == null) return true; // Période active
        return !date.isAfter(dateFin);
    }
    
    /**
     * Obtenir une description textuelle de la période
     */
    public String getDescriptionPeriode() {
        String desc = dateDebut.toString();
        if (dateFin != null) {
            desc += " → " + dateFin.toString();
        } else {
            desc += " → Présent";
        }
        if (getDureeEnAnnees() != null) {
            desc += " (" + getDureeEnAnnees() + " ans)";
        }
        return desc;
    }
    
    @Override
    public String toString() {
        return "Affiliation[" + 
               (membre != null ? membre.getNomComplet() : "?") + 
               " | " + getDescriptionPeriode() + 
               " | " + (statutPendantPeriode != null ? statutPendantPeriode.getLibelle() : "?") + 
               "]";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AffiliationHistorique)) return false;
        AffiliationHistorique that = (AffiliationHistorique) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}