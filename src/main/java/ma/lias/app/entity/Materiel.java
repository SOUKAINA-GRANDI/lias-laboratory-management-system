package ma.lias.app.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Matériel du laboratoire LIAS
 *
 * RÈGLES MÉTIER :
 * - Suivi des arrivages
 * - Distribution équitable entre membres
 * - Historique complet (qui a reçu quoi)
 * - Suivi de qui n'a pas encore reçu
 */
@Entity
@Table(name = "materiels")
@NamedQueries({
    @NamedQuery(name = "Materiel.findAll",
                query = "SELECT m FROM Materiel m ORDER BY m.dateArrivage DESC"),
    @NamedQuery(name = "Materiel.findDisponibles",
                query = "SELECT m FROM Materiel m WHERE m.statut = :statut ORDER BY m.dateArrivage DESC"),
    @NamedQuery(name = "Materiel.findByType",
                query = "SELECT m FROM Materiel m WHERE m.type = :type ORDER BY m.dateArrivage DESC"),
    @NamedQuery(name = "Materiel.findByMembre",
                query = "SELECT m FROM Materiel m JOIN m.attributions a WHERE a.membre.id = :membreId ORDER BY m.dateArrivage DESC")
})
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== INFORMATIONS PRINCIPALES ==========

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 200)
    private String nom;

    @NotNull(message = "Le type est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeMateriel type;

    @Column(length = 1000)
    private String description;

    @Column(length = 100)
    private String marque;

    @Column(length = 100)
    private String modele;

    @Column(length = 100)
    private String numeroSerie;

    // ========== ARRIVAGE ==========

    @NotNull(message = "La date d'arrivage est obligatoire")
    @Column(nullable = false)
    private LocalDate dateArrivage;

    @Column(length = 200)
    private String fournisseur;

    private Double prixAchat;

    @Column(length = 255)
    private String cheminFacture;       // facture uploadée

    // ========== STOCK ==========

    @Column(nullable = false)
    private Integer quantiteTotale = 1;

    @Column(nullable = false)
    private Integer quantiteDisponible = 1;

    // ========== ÉTAT ==========

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutMateriel statut = StatutMateriel.DISPONIBLE;

    // ========== ATTRIBUTIONS ==========

    @OneToMany(mappedBy = "materiel",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    @OrderBy("dateAttribution DESC")
    private List<AttributionMateriel> attributions = new ArrayList<>();

    // ========== TRAÇABILITÉ ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ajoute_par_id")
    private Membre ajoutePar;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    @Column(length = 500)
    private String notes;

    // ========== CONSTRUCTEURS ==========

    public Materiel() {
        this.dateCreation       = LocalDateTime.now();
        this.statut             = StatutMateriel.DISPONIBLE;
        this.quantiteTotale     = 1;
        this.quantiteDisponible = 1;
        this.attributions       = new ArrayList<>();
    }

    public Materiel(String nom, TypeMateriel type, LocalDate dateArrivage) {
        this();
        this.nom          = nom;
        this.type         = type;
        this.dateArrivage = dateArrivage;
    }

    // ========== CALLBACK JPA ==========

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // ========== ENUMS INTERNES ==========

    public enum TypeMateriel {
        ORDINATEUR("Ordinateur"),
        IMPRIMANTE("Imprimante"),
        SERVEUR("Serveur"),
        RESEAU("Équipement Réseau"),
        MOBILIER("Mobilier"),
        PERIPHERIQUE("Périphérique"),
        LOGICIEL("Logiciel"),
        AUTRE("Autre");

        private final String libelle;
        TypeMateriel(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    public enum StatutMateriel {
        DISPONIBLE("Disponible"),
        ATTRIBUE("Attribué"),
        EN_MAINTENANCE("En Maintenance"),
        HORS_SERVICE("Hors Service");

        private final String libelle;
        StatutMateriel(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    // ========== MÉTHODES MÉTIER ==========

    /**
     * Attribuer le matériel à un membre
     */
    public boolean attribuerA(Membre membre, Membre validePar) {
        if (quantiteDisponible <= 0) return false;

        AttributionMateriel attribution = new AttributionMateriel(this, membre, validePar);
        this.attributions.add(attribution);
        this.quantiteDisponible--;

        if (quantiteDisponible == 0) {
            this.statut = StatutMateriel.ATTRIBUE;
        }
        return true;
    }

    /**
     * Récupérer le matériel d'un membre
     */
    public void recupererDe(Membre membre) {
        attributions.stream()
            .filter(a -> a.getMembre().equals(membre) && a.estActive())
            .findFirst()
            .ifPresent(a -> {
                a.terminer();
                this.quantiteDisponible++;
                if (this.statut == StatutMateriel.ATTRIBUE) {
                    this.statut = StatutMateriel.DISPONIBLE;
                }
            });
    }

    public boolean estDisponible() {
        return quantiteDisponible > 0;
    }

    public int getNombreAttributionsActives() {
        if (attributions == null) return 0;
        return (int) attributions.stream()
                .filter(AttributionMateriel::estActive)
                .count();
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeMateriel getType() { return type; }
    public void setType(TypeMateriel type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public LocalDate getDateArrivage() { return dateArrivage; }
    public void setDateArrivage(LocalDate dateArrivage) { this.dateArrivage = dateArrivage; }

    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }

    public Double getPrixAchat() { return prixAchat; }
    public void setPrixAchat(Double prixAchat) { this.prixAchat = prixAchat; }

    public String getCheminFacture() { return cheminFacture; }
    public void setCheminFacture(String cheminFacture) { this.cheminFacture = cheminFacture; }

    public Integer getQuantiteTotale() { return quantiteTotale; }
    public void setQuantiteTotale(Integer quantiteTotale) { this.quantiteTotale = quantiteTotale; }

    public Integer getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(Integer quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }

    public StatutMateriel getStatut() { return statut; }
    public void setStatut(StatutMateriel statut) { this.statut = statut; }

    public List<AttributionMateriel> getAttributions() { return attributions; }
    public void setAttributions(List<AttributionMateriel> attributions) { this.attributions = attributions; }

    public Membre getAjoutePar() { return ajoutePar; }
    public void setAjoutePar(Membre ajoutePar) { this.ajoutePar = ajoutePar; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime d) { this.dateModification = d; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // ========== equals / hashCode / toString ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Materiel)) return false;
        Materiel that = (Materiel) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return nom + " (" + type.getLibelle() + ") - " +
               statut.getLibelle() + " [" +
               quantiteDisponible + "/" + quantiteTotale + "]";
    }
}