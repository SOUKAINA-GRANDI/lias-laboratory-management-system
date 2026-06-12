package ma.lias.app.entity;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entité représentant une équipe de recherche du laboratoire LIAS
 */
@Entity
@Table(name = "equipes")
public class Equipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String nom;
    
    @Column(length = 50)
    private String acronyme;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 1000)
    private String thematiques;
    
    @Column(nullable = false)
    private LocalDate dateCreation;
    
    private LocalDate dateFermeture;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 255)
    private String siteWeb;
    
    // ========== CONSTRUCTEURS ==========
    
    public Equipe() {
        this.dateCreation = LocalDate.now();
        this.active = true;
    }
    
    public Equipe(String nom) {
        this();
        this.nom = nom;
    }
    
    public Equipe(String nom, String description) {
        this();
        this.nom = nom;
        this.description = description;
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getAcronyme() {
        return acronyme;
    }
    
    public void setAcronyme(String acronyme) {
        this.acronyme = acronyme;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getThematiques() {
        return thematiques;
    }
    
    public void setThematiques(String thematiques) {
        this.thematiques = thematiques;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDate getDateFermeture() {
        return dateFermeture;
    }
    
    public void setDateFermeture(LocalDate dateFermeture) {
        this.dateFermeture = dateFermeture;
        if (dateFermeture != null) {
            this.active = false;
        }
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSiteWeb() {
        return siteWeb;
    }
    
    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }
    
    // ========== MÉTHODES MÉTIER ==========
    
    /**
     * Fermer l'équipe
     */
    public void fermer() {
        this.dateFermeture = LocalDate.now();
        this.active = false;
    }
    
    /*Réactiver l'équipe*/
    public void reactiver() {
        this.dateFermeture = null;
        this.active = true;
    }
    
    /**
     * Vérifier si l'équipe est active
     */
    public boolean estActive() {
        return active && dateFermeture == null;
    }
    
    @Override
    public String toString() {
        return nom + (acronyme != null ? " (" + acronyme + ")" : "");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipe)) return false;
        Equipe equipe = (Equipe) o;
        return id != null && id.equals(equipe.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}