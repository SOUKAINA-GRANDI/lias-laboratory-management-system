package ma.lias.app.entity;

import ma.lias.app.enums.TypeUtilisateur;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entité représentant un compte utilisateur pour l'authentification
 * 
 * RÈGLES MÉTIER :
 * - Un membre peut avoir un compte utilisateur (relation OneToOne)
 * - Password hashé avec BCrypt (jamais en clair)
 * - Type utilisateur détermine les droits d'accès
 * - Peut être désactivé sans suppression
 */
@Entity
@Table(name = "utilisateurs")
@NamedQueries({
    @NamedQuery(name = "Utilisateur.findAll", 
                query = "SELECT u FROM Utilisateur u WHERE u.compteActif = true"),
    @NamedQuery(name = "Utilisateur.findByUsername", 
                query = "SELECT u FROM Utilisateur u WHERE u.username = :username"),
    @NamedQuery(name = "Utilisateur.findByType", 
                query = "SELECT u FROM Utilisateur u WHERE u.typeUtilisateur = :type AND u.compteActif = true"),
    @NamedQuery(name = "Utilisateur.authenticate", 
                query = "SELECT u FROM Utilisateur u WHERE u.username = :username AND u.compteActif = true")
})
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ========== RELATION AVEC MEMBRE ==========
    
    @OneToOne
    @JoinColumn(name = "membre_id", unique = true)
    private Membre membre;
    
    // ========== IDENTIFIANTS DE CONNEXION ==========
    
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false, length = 255)
    private String password; // Hash BCrypt
    
    // ========== TYPE ET DROITS ==========
    
    @NotNull(message = "Le type d'utilisateur est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeUtilisateur typeUtilisateur;
    
    // ========== ÉTAT DU COMPTE ==========
    
    @Column(nullable = false)
    private Boolean compteActif = true;
    
    @Column(nullable = false)
    private Boolean premiereConnexion = true;
    
    // ========== TRAÇABILITÉ ==========
    
    private LocalDateTime derniereConnexion;
    
    private Integer nombreTentativesEchouees = 0;
    
    private LocalDateTime dateVerrouillage;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    private LocalDateTime dateModification;
    
    @Column(length = 500)
    private String notes; // Notes admin
    
    // ========== CONSTRUCTEURS ==========
    
    public Utilisateur() {
        this.dateCreation = LocalDateTime.now();
        this.compteActif = true;
        this.premiereConnexion = true;
        this.nombreTentativesEchouees = 0;
    }
    
    public Utilisateur(String username, String password, TypeUtilisateur type) {
        this();
        this.username = username;
        this.password = password;
        this.typeUtilisateur = type;
    }
    
    public Utilisateur(Membre membre, String username, String password, TypeUtilisateur type) {
        this(username, password, type);
        this.membre = membre;
    }
    
    // ========== CALLBACKS JPA ==========
    
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public TypeUtilisateur getTypeUtilisateur() {
        return typeUtilisateur;
    }
    
    public void setTypeUtilisateur(TypeUtilisateur typeUtilisateur) {
        this.typeUtilisateur = typeUtilisateur;
    }
    
    public Boolean getCompteActif() {
        return compteActif;
    }
    
    public void setCompteActif(Boolean compteActif) {
        this.compteActif = compteActif;
    }
    
    public Boolean getPremiereConnexion() {
        return premiereConnexion;
    }
    
    public void setPremiereConnexion(Boolean premiereConnexion) {
        this.premiereConnexion = premiereConnexion;
    }
    
    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }
    
    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }
    
    public Integer getNombreTentativesEchouees() {
        return nombreTentativesEchouees;
    }
    
    public void setNombreTentativesEchouees(Integer nombreTentativesEchouees) {
        this.nombreTentativesEchouees = nombreTentativesEchouees;
    }
    
    public LocalDateTime getDateVerrouillage() {
        return dateVerrouillage;
    }
    
    public void setDateVerrouillage(LocalDateTime dateVerrouillage) {
        this.dateVerrouillage = dateVerrouillage;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
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
     * Vérifier si le compte est verrouillé (après 5 tentatives échouées)
     */
    public boolean estVerrouille() {
        return dateVerrouillage != null && 
               LocalDateTime.now().isBefore(dateVerrouillage.plusHours(24));
    }
    
    /**
     * Incrémenter les tentatives échouées
     */
    public void incrementerTentativesEchouees() {
        this.nombreTentativesEchouees++;
        if (this.nombreTentativesEchouees >= 5) {
            this.dateVerrouillage = LocalDateTime.now();
        }
    }
    
    /**
     * Réinitialiser les tentatives après connexion réussie
     */
    public void resetTentativesEchouees() {
        this.nombreTentativesEchouees = 0;
        this.dateVerrouillage = null;
        this.derniereConnexion = LocalDateTime.now();
        if (this.premiereConnexion) {
            this.premiereConnexion = false;
        }
    }
    
    /**
     * Déverrouiller le compte manuellement (admin)
     */
    public void deverrouiller() {
        this.nombreTentativesEchouees = 0;
        this.dateVerrouillage = null;
    }
    
    /**
     * Désactiver le compte
     */
    public void desactiver(String motif) {
        this.compteActif = false;
        this.notes = (this.notes != null ? this.notes + " | " : "") + 
                     "Désactivé le " + LocalDateTime.now() + " - Motif: " + motif;
    }
    
    /**
     * Réactiver le compte
     */
    public void reactiver() {
        this.compteActif = true;
        this.nombreTentativesEchouees = 0;
        this.dateVerrouillage = null;
    }
    
    /**
     * Obtenir le nom complet de l'utilisateur (via le membre)
     */
    public String getNomComplet() {
        return membre != null ? membre.getNomComplet() : username;
    }
    
    /**
     * Vérifier si l'utilisateur est admin
     */
    public boolean estAdmin() {
        return typeUtilisateur == TypeUtilisateur.ADMINISTRATEUR;
    }
    
    /**
     * Vérifier si l'utilisateur est directeur
     */
    public boolean estDirecteur() {
        return typeUtilisateur == TypeUtilisateur.DIRECTEUR;
    }
    
    /**
     * Vérifier si peut valider des demandes
     */
    public boolean peutValider() {
        return typeUtilisateur.peutValider();
    }
    
    @Override
    public String toString() {
        return username + " (" + typeUtilisateur.getLibelle() + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}