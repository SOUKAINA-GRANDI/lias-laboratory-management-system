package ma.lias.app.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Convention / Collaboration du laboratoire LIAS
 *
 * RÈGLES MÉTIER :
 * - Gestion des partenaires externes
 * - Création et archivage des conventions
 * - Suivi des activités associées
 * - Documents liés archivés
 */
@Entity
@Table(name = "conventions")
@NamedQueries({
    @NamedQuery(name = "Convention.findAll",
                query = "SELECT c FROM Convention c ORDER BY c.dateSignature DESC"),
    @NamedQuery(name = "Convention.findActives",
                query = "SELECT c FROM Convention c WHERE c.statut = :statut ORDER BY c.dateSignature DESC"),
    @NamedQuery(name = "Convention.findByType",
                query = "SELECT c FROM Convention c WHERE c.type = :type ORDER BY c.dateSignature DESC"),
    @NamedQuery(name = "Convention.findByPartenaire",
                query = "SELECT c FROM Convention c WHERE LOWER(c.nomPartenaire) LIKE :nom ORDER BY c.dateSignature DESC"),
    @NamedQuery(name = "Convention.findExpirantAvant",
                query = "SELECT c FROM Convention c WHERE c.dateExpiration BETWEEN :debut AND :fin ORDER BY c.dateExpiration ASC")
})
public class Convention {

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
    private TypeConvention type;

    @Column(length = 2000)
    private String description;

    // ========== PARTENAIRE ==========

    @NotBlank(message = "Le nom du partenaire est obligatoire")
    @Column(nullable = false, length = 200)
    private String nomPartenaire;

    @Column(length = 200)
    private String paysPartenaire;

    @Column(length = 200)
    private String villePartenaire;

    @Column(length = 200)
    private String contactPartenaire;   // nom du responsable côté partenaire

    @Column(length = 100)
    private String emailPartenaire;

    @Column(length = 255)
    private String siteWebPartenaire;

    // ========== PÉRIODE ==========

    @NotNull(message = "La date de signature est obligatoire")
    @Column(nullable = false)
    private LocalDate dateSignature;

    private LocalDate dateExpiration;   // null = pas de date limite

    // ========== ÉTAT ==========

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutConvention statut = StatutConvention.ACTIVE;

    // ========== RESPONSABLE LIAS ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Membre responsable;         // membre LIAS responsable de la convention

    // ========== MEMBRES IMPLIQUÉS ==========

    @ManyToMany
    @JoinTable(
        name = "convention_membres",
        joinColumns = @JoinColumn(name = "convention_id"),
        inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    @OrderBy("nom ASC")
    private List<Membre> membresImpliques = new ArrayList<>();

    // ========== DOCUMENTS LIÉS ==========

    @OneToMany(mappedBy = "convention", cascade = CascadeType.ALL)
    @OrderBy("dateUpload DESC")
    private List<Document> documents = new ArrayList<>();

    // ========== ACTIVITÉS ASSOCIÉES ==========

    @Column(length = 3000)
    private String activitesAssociees;  // description des activités

    @Column(length = 500)
    private String objectifs;

    @Column(length = 500)
    private String resultatsAtteints;

    // ========== TRAÇABILITÉ ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cree_par_id")
    private Membre creePar;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    @Column(length = 500)
    private String notes;

    // ========== CONSTRUCTEURS ==========

    public Convention() {
        this.dateCreation     = LocalDateTime.now();
        this.statut           = StatutConvention.ACTIVE;
        this.membresImpliques = new ArrayList<>();
        this.documents        = new ArrayList<>();
    }

    public Convention(String titre, TypeConvention type,
                      String nomPartenaire, LocalDate dateSignature) {
        this();
        this.titre          = titre;
        this.type           = type;
        this.nomPartenaire  = nomPartenaire;
        this.dateSignature  = dateSignature;
    }

    // ========== CALLBACK JPA ==========

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // ========== ENUMS INTERNES ==========

    public enum TypeConvention {
        PARTENARIAT("Partenariat"),
        COOPERATION("Coopération"),
        COTUTELLE("Cotutelle de Thèse"),
        MOBILITE("Mobilité"),
        RECHERCHE("Recherche Conjointe"),
        FORMATION("Formation"),
        AUTRE("Autre");

        private final String libelle;
        TypeConvention(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    public enum StatutConvention {
        ACTIVE("Active"),
        EXPIREE("Expirée"),
        SUSPENDUE("Suspendue"),
        RESILIEE("Résiliée"),
        EN_COURS_RENOUVELLEMENT("En Cours de Renouvellement");

        private final String libelle;
        StatutConvention(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    // ========== MÉTHODES MÉTIER ==========

    public void ajouterMembre(Membre membre) {
        if (!membresImpliques.contains(membre)) {
            membresImpliques.add(membre);
        }
    }

    public void retirerMembre(Membre membre) {
        membresImpliques.remove(membre);
    }

    public void suspendre() {
        this.statut = StatutConvention.SUSPENDUE;
    }

    public void resilier() {
        this.statut = StatutConvention.RESILIEE;
    }

    public void renouveler(LocalDate nouvelleDateExpiration) {
        this.dateExpiration = nouvelleDateExpiration;
        this.statut         = StatutConvention.ACTIVE;
    }

    public boolean estActive() {
        return statut == StatutConvention.ACTIVE;
    }

    public boolean estExpiree() {
        if (dateExpiration == null) return false;
        return LocalDate.now().isAfter(dateExpiration);
    }

    public boolean expireBientot(int joursAvantExpiration) {
        if (dateExpiration == null) return false;
        LocalDate limite = LocalDate.now().plusDays(joursAvantExpiration);
        return !LocalDate.now().isAfter(dateExpiration) &&
                dateExpiration.isBefore(limite);
    }

    public int getNombreDocuments() {
        return documents != null ? documents.size() : 0;
    }

    public int getNombreMembres() {
        return membresImpliques != null ? membresImpliques.size() : 0;
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypeConvention getType() { return type; }
    public void setType(TypeConvention type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNomPartenaire() { return nomPartenaire; }
    public void setNomPartenaire(String nomPartenaire) { this.nomPartenaire = nomPartenaire; }

    public String getPaysPartenaire() { return paysPartenaire; }
    public void setPaysPartenaire(String paysPartenaire) { this.paysPartenaire = paysPartenaire; }

    public String getVillePartenaire() { return villePartenaire; }
    public void setVillePartenaire(String villePartenaire) { this.villePartenaire = villePartenaire; }

    public String getContactPartenaire() { return contactPartenaire; }
    public void setContactPartenaire(String c) { this.contactPartenaire = c; }

    public String getEmailPartenaire() { return emailPartenaire; }
    public void setEmailPartenaire(String emailPartenaire) { this.emailPartenaire = emailPartenaire; }

    public String getSiteWebPartenaire() { return siteWebPartenaire; }
    public void setSiteWebPartenaire(String s) { this.siteWebPartenaire = s; }

    public LocalDate getDateSignature() { return dateSignature; }
    public void setDateSignature(LocalDate dateSignature) { this.dateSignature = dateSignature; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public StatutConvention getStatut() { return statut; }
    public void setStatut(StatutConvention statut) { this.statut = statut; }

    public Membre getResponsable() { return responsable; }
    public void setResponsable(Membre responsable) { this.responsable = responsable; }

    public List<Membre> getMembresImpliques() { return membresImpliques; }
    public void setMembresImpliques(List<Membre> m) { this.membresImpliques = m; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }

    public String getActivitesAssociees() { return activitesAssociees; }
    public void setActivitesAssociees(String a) { this.activitesAssociees = a; }

    public String getObjectifs() { return objectifs; }
    public void setObjectifs(String objectifs) { this.objectifs = objectifs; }

    public String getResultatsAtteints() { return resultatsAtteints; }
    public void setResultatsAtteints(String r) { this.resultatsAtteints = r; }

    public Membre getCreePar() { return creePar; }
    public void setCreePar(Membre creePar) { this.creePar = creePar; }

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
        if (!(o instanceof Convention)) return false;
        Convention that = (Convention) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return titre + " - " + nomPartenaire +
               " (" + type.getLibelle() + " - " +
               statut.getLibelle() + ")";
    }
}