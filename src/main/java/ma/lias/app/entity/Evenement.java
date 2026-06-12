package ma.lias.app.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Événement scientifique du laboratoire LIAS
 *
 * RÈGLES MÉTIER :
 * - Types : Conférence, Séminaire, Workshop
 * - Archivage par édition
 * - Affectation d'organisateurs
 * - Inclus dans le rapport annuel
 * - Visible publiquement (visiteurs)
 */
@Entity
@Table(name = "evenements")
@NamedQueries({
    @NamedQuery(name = "Evenement.findAll",
                query = "SELECT e FROM Evenement e ORDER BY e.dateDebut DESC"),
    @NamedQuery(name = "Evenement.findAVenir",
                query = "SELECT e FROM Evenement e WHERE e.dateDebut >= :today ORDER BY e.dateDebut ASC"),
    @NamedQuery(name = "Evenement.findByType",
                query = "SELECT e FROM Evenement e WHERE e.type = :type ORDER BY e.dateDebut DESC"),
    @NamedQuery(name = "Evenement.findByAnnee",
                query = "SELECT e FROM Evenement e WHERE e.dateDebut BETWEEN :debut AND :fin ORDER BY e.dateDebut DESC"),
    @NamedQuery(name = "Evenement.findByOrganisateur",
                query = "SELECT e FROM Evenement e JOIN e.organisateurs o WHERE o.id = :membreId ORDER BY e.dateDebut DESC"),
    @NamedQuery(name = "Evenement.findByStatut",
                query = "SELECT e FROM Evenement e WHERE e.statut = :statut ORDER BY e.dateDebut DESC")
})
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== INFORMATIONS PRINCIPALES ==========

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false, length = 300)
    private String titre;

    @NotNull(message = "Le type est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeEvenement type;

    @Column(length = 2000)
    private String description;

    // ========== DATES ==========

    @NotNull(message = "La date de début est obligatoire")
    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;          // null = événement sur 1 seul jour

    // ========== LIEU ==========

    @Column(length = 200)
    private String lieu;

    @Column(length = 200)
    private String villesPays;

    @Column(length = 255)
    private String lienVisio;          // lien Teams/Zoom si distanciel

    // ========== ARCHIVAGE PAR ÉDITION ==========

    @Column(length = 10)
    private String edition;            // ex: "1ère", "2ème", "2024"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_parent_id")
    private Evenement evenementParent;  // édition précédente (historique)

    @OneToMany(mappedBy = "evenementParent", cascade = CascadeType.ALL)
    @OrderBy("dateDebut DESC")
    private List<Evenement> editions = new ArrayList<>();

    // ========== ORGANISATEURS ==========

    @ManyToMany
    @JoinTable(
        name = "evenement_organisateurs",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    private List<Membre> organisateurs = new ArrayList<>();

    // ========== ÉTAT ==========

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEvenement statut = StatutEvenement.PLANIFIE;

    // ========== MÉTADONNÉES ==========

    @Column(length = 255)
    private String siteWeb;

    @Column(length = 255)
    private String urlProgramme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cree_par_id")
    private Membre creePar;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    // ========== CONSTRUCTEURS ==========

    public Evenement() {
        this.dateCreation = LocalDateTime.now();
        this.statut = StatutEvenement.PLANIFIE;
        this.organisateurs = new ArrayList<>();
        this.editions = new ArrayList<>();
    }

    public Evenement(String titre, TypeEvenement type, LocalDate dateDebut) {
        this();
        this.titre     = titre;
        this.type      = type;
        this.dateDebut = dateDebut;
    }

    // ========== CALLBACK JPA ==========

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // ========== ENUMS INTERNES ==========

    public enum TypeEvenement {
        CONFERENCE("Conférence"),
        SEMINAIRE("Séminaire"),
        WORKSHOP("Workshop"),
        JOURNEE_ETUDE("Journée d'Étude"),
        AUTRE("Autre");

        private final String libelle;
        TypeEvenement(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    public enum StatutEvenement {
        PLANIFIE("Planifié"),
        EN_COURS("En Cours"),
        TERMINE("Terminé"),
        ANNULE("Annulé");

        private final String libelle;
        StatutEvenement(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    // ========== MÉTHODES MÉTIER ==========

    public void ajouterOrganisateur(Membre membre) {
        if (!organisateurs.contains(membre)) {
            organisateurs.add(membre);
        }
    }

    public void retirerOrganisateur(Membre membre) {
        organisateurs.remove(membre);
    }

    public void terminer() {
        this.statut = StatutEvenement.TERMINE;
    }

    public void annuler() {
        this.statut = StatutEvenement.ANNULE;
    }

    public boolean estAVenir() {
        return LocalDate.now().isBefore(dateDebut);
    }

    public boolean estEnCours() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(dateDebut) &&
               (dateFin == null || !today.isAfter(dateFin));
    }

    public boolean estTermine() {
        return statut == StatutEvenement.TERMINE ||
               (dateFin != null && LocalDate.now().isAfter(dateFin));
    }

    public int getDureeJours() {
        if (dateFin == null) return 1;
        return (int) (dateDebut.until(dateFin).getDays() + 1);
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypeEvenement getType() { return type; }
    public void setType(TypeEvenement type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getVillesPays() { return villesPays; }
    public void setVillesPays(String villesPays) { this.villesPays = villesPays; }

    public String getLienVisio() { return lienVisio; }
    public void setLienVisio(String lienVisio) { this.lienVisio = lienVisio; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public Evenement getEvenementParent() { return evenementParent; }
    public void setEvenementParent(Evenement evenementParent) { this.evenementParent = evenementParent; }

    public List<Evenement> getEditions() { return editions; }
    public void setEditions(List<Evenement> editions) { this.editions = editions; }

    public List<Membre> getOrganisateurs() { return organisateurs; }
    public void setOrganisateurs(List<Membre> organisateurs) { this.organisateurs = organisateurs; }

    public StatutEvenement getStatut() { return statut; }
    public void setStatut(StatutEvenement statut) { this.statut = statut; }

    public String getSiteWeb() { return siteWeb; }
    public void setSiteWeb(String siteWeb) { this.siteWeb = siteWeb; }

    public String getUrlProgramme() { return urlProgramme; }
    public void setUrlProgramme(String urlProgramme) { this.urlProgramme = urlProgramme; }

    public Membre getCreePar() { return creePar; }
    public void setCreePar(Membre creePar) { this.creePar = creePar; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    // ========== equals / hashCode / toString ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evenement)) return false;
        Evenement that = (Evenement) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return titre + " (" + type.getLibelle() + " - " + dateDebut + ")";
    }
}