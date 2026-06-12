package ma.lias.app.entity;

import ma.lias.app.enums.StatutMembre;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité centrale représentant un membre du laboratoire LIAS
 * 
 * RÈGLES MÉTIER :
 * - Un membre peut changer de statut (permanent → associé, etc.)
 * - Un membre peut quitter et revenir (historique affiliations)
 * - Un membre peut changer d'équipe
 * - AUCUNE suppression : désactivation uniquement
 * - Le profil est modifiable par le membre (point 3 du cahier des charges)
 */
@Entity
@Table(name = "membres")
@NamedQueries({
    @NamedQuery(name = "Membre.findAll", 
                query = "SELECT m FROM Membre m WHERE m.actif = true ORDER BY m.nom"),
    @NamedQuery(name = "Membre.findByStatut", 
                query = "SELECT m FROM Membre m WHERE m.statut = :statut AND m.actif = true"),
    @NamedQuery(name = "Membre.findByEquipe", 
                query = "SELECT m FROM Membre m WHERE m.equipeActuelle.id = :equipeId AND m.actif = true"),
    @NamedQuery(name = "Membre.findByEmail", 
                query = "SELECT m FROM Membre m WHERE m.email = :email"),
    @NamedQuery(name = "Membre.countByStatut", 
                query = "SELECT COUNT(m) FROM Membre m WHERE m.statut = :statut AND m.actif = true")
})
public class Membre {
    
    // ========== IDENTIFIANT ==========
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ========== INFORMATIONS PERSONNELLES ==========
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false, length = 50)
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false, length = 50)
    private String prenom;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Pattern(regexp = "^[0-9+\\-\\s()]{10,20}$", message = "Numéro de téléphone invalide")
    @Column(length = 20)
    private String telephone;
    
    @Column(length = 255)
    private String photo;
    
    private LocalDate dateNaissance;
    
    @Column(length = 1)
    private String genre;
    
    // ========== STATUT ET AFFILIATION ==========
    
    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutMembre statut;
    
    @Column(nullable = false)
    private LocalDate dateEmbauche;
    
    @NotNull(message = "La date d'affiliation est obligatoire")
    @Column(nullable = false)
    private LocalDate dateAffiliation;
    
    @Column(length = 100)
    private String etablissementOrigine;
    
    @Column(length = 100)
    private String laboratoireOrigine;
    
    @Column(length = 100)
    private String grade;
    
    @Column(length = 100)
    private String specialite;
    
    // ========== PROFIL SCIENTIFIQUE ==========
    
    @Column(length = 3000)
    private String biographie;
    
    @Column(length = 1000)
    private String centresInteret;
    
    @Column(length = 500)
    private String competences;
    
    @Column(length = 255)
    private String siteWebPersonnel;
    
    @Column(length = 100)
    private String orcid;
    
    @Column(length = 100)
    private String googleScholar;
    
    @Column(length = 100)
    private String researchGate;
    
    // ========== ÉQUIPE ACTUELLE ==========
    
    @ManyToOne
    @JoinColumn(name = "equipe_actuelle_id")
    private Equipe equipeActuelle;
    
    // ========== COMPTE UTILISATEUR ==========
    
    @OneToOne(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    private Utilisateur utilisateur;
    
    // ========== HISTORIQUE DES AFFILIATIONS ==========
    
    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateDebut DESC")
    private List<AffiliationHistorique> affiliations = new ArrayList<>();
    
    // ========== ÉTAT DU COMPTE ==========
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    // ========== MÉTADONNÉES ==========
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private java.util.Date dateCreation;
    
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date dateModification;
    
    @Column(length = 500)
    private String notes;
    
    
    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateDebut DESC")
    private List<RoleHistorique> roles = new ArrayList<>();
    
    // ========== CONSTRUCTEURS ==========
    
    public Membre() {
        this.dateCreation = new java.util.Date();
        this.dateModification = new java.util.Date();
        this.actif = true;
    }
    
    public Membre(String nom, String prenom, String email, StatutMembre statut) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.statut = statut;
        this.dateAffiliation = LocalDate.now();
        this.dateEmbauche = LocalDate.now();
    }
    
    // ========== CALLBACKS JPA ==========
    
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = new java.util.Date();
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public StatutMembre getStatut() { return statut; }
    public void setStatut(StatutMembre statut) { this.statut = statut; }
    
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    
    public LocalDate getDateAffiliation() { return dateAffiliation; }
    public void setDateAffiliation(LocalDate dateAffiliation) { this.dateAffiliation = dateAffiliation; }
    
    public String getEtablissementOrigine() { return etablissementOrigine; }
    public void setEtablissementOrigine(String etablissementOrigine) { this.etablissementOrigine = etablissementOrigine; }
    
    public String getLaboratoireOrigine() { return laboratoireOrigine; }
    public void setLaboratoireOrigine(String laboratoireOrigine) { this.laboratoireOrigine = laboratoireOrigine; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    
    public String getBiographie() { return biographie; }
    public void setBiographie(String biographie) { this.biographie = biographie; }
    
    public String getCentresInteret() { return centresInteret; }
    public void setCentresInteret(String centresInteret) { this.centresInteret = centresInteret; }
    
    public String getCompetences() { return competences; }
    public void setCompetences(String competences) { this.competences = competences; }
    
    public String getSiteWebPersonnel() { return siteWebPersonnel; }
    public void setSiteWebPersonnel(String siteWebPersonnel) { this.siteWebPersonnel = siteWebPersonnel; }
    
    public String getOrcid() { return orcid; }
    public void setOrcid(String orcid) { this.orcid = orcid; }
    
    public String getGoogleScholar() { return googleScholar; }
    public void setGoogleScholar(String googleScholar) { this.googleScholar = googleScholar; }
    
    public String getResearchGate() { return researchGate; }
    public void setResearchGate(String researchGate) { this.researchGate = researchGate; }
    
    public Equipe getEquipeActuelle() { return equipeActuelle; }
    public void setEquipeActuelle(Equipe equipeActuelle) { this.equipeActuelle = equipeActuelle; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public List<AffiliationHistorique> getAffiliations() { return affiliations; }
    public void setAffiliations(List<AffiliationHistorique> affiliations) { this.affiliations = affiliations; }
    
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    
    public java.util.Date getDateCreation() { return dateCreation; }
    public void setDateCreation(java.util.Date dateCreation) { this.dateCreation = dateCreation; }
    
    public java.util.Date getDateModification() { return dateModification; }
    public void setDateModification(java.util.Date dateModification) { this.dateModification = dateModification; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<RoleHistorique> getRoles() {
        return roles;
    }
    
    // ========== MÉTHODES MÉTIER ==========
    
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    public String getInitiales() {
        String i = "";
        if (prenom != null && !prenom.isEmpty()) {
            i += prenom.charAt(0);
        }
        if (nom != null && !nom.isEmpty()) {
            i += nom.charAt(0);
        }
        return i.toUpperCase();
    }
    
    public boolean estRetraite() {
        return statut == StatutMembre.RETRAITE;
    }
    
    public boolean estAncienMembre() {
        return statut == StatutMembre.ANCIEN_MEMBRE;
    }
    
    public boolean peutModifierProfil() {
        return actif && statut.peutModifierProfil();
    }
    
    public boolean aAccesModulesInternes() {
        return actif && statut.aAccesModulesInternes();
    }
    
    public Integer getAge() {
        if (dateNaissance == null) return null;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
    
    public Integer getAnciennete() {
        if (dateAffiliation == null) return null;
        return Period.between(dateAffiliation, LocalDate.now()).getYears();
    }
    
    public void desactiver(String motif) {
        this.actif = false;
        this.statut = StatutMembre.ANCIEN_MEMBRE;
        this.notes = (this.notes != null ? this.notes + " | " : "") + 
                     "Désactivé le " + LocalDate.now() + " - Motif: " + motif;
    }
    
    public void mettreEnRetraite() {
        this.statut = StatutMembre.RETRAITE;
        this.actif = false;
        this.notes = (this.notes != null ? this.notes + " | " : "") + 
                     "Mis en retraite le " + LocalDate.now();
    }
    
    public void reactiver(StatutMembre nouveauStatut) {
        if (this.statut == StatutMembre.ANCIEN_MEMBRE) {
            this.actif = true;
            this.statut = nouveauStatut;
            this.notes = (this.notes != null ? this.notes + " | " : "") + 
                         "Réactivé le " + LocalDate.now();
        }
    }
    
    /**
     * Obtenir l'affiliation active (en cours)
     */
    public AffiliationHistorique getAffiliationActive() {
        if (affiliations == null || affiliations.isEmpty()) return null;
        return affiliations.stream()
                .filter(a -> a.getPeriodeActive() != null && a.getPeriodeActive())
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Obtenir le nombre d'affiliations
     */
    public int getNombreAffiliations() {
        return affiliations != null ? affiliations.size() : 0;
    }
    
    /**
     * Vérifier si le membre a déjà quitté puis est revenu
     */
    public boolean aQuittePuisRevenu() {
        if (affiliations == null || affiliations.size() < 2) return false;
        return affiliations.stream().anyMatch(a -> a.estTerminee());
    }
    
    @Override
    public String toString() {
        return getNomComplet() + " (" + statut.getLibelle() + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Membre)) return false;
        Membre membre = (Membre) o;
        return id != null && id.equals(membre.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}