package ma.lias.app.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Publication scientifique du laboratoire LIAS
 *
 * RÈGLES MÉTIER :
 * - Ajoutée par membres permanents, associés et doctorants
 * - Classement par auteur, année, équipe
 * - Incluse dans le rapport annuel automatique
 */
@Entity
@Table(name = "publications")
@NamedQueries({
    @NamedQuery(name = "Publication.findAll",
                query = "SELECT p FROM Publication p ORDER BY p.annee DESC"),
    @NamedQuery(name = "Publication.findByAnnee",
                query = "SELECT p FROM Publication p WHERE p.annee = :annee ORDER BY p.datePublication DESC"),
    @NamedQuery(name = "Publication.findByMembre",
                query = "SELECT p FROM Publication p JOIN p.auteurs a WHERE a.id = :membreId ORDER BY p.annee DESC"),
    @NamedQuery(name = "Publication.findByEquipe",
                query = "SELECT p FROM Publication p WHERE p.equipe.id = :equipeId ORDER BY p.annee DESC"),
    @NamedQuery(name = "Publication.findByType",
                query = "SELECT p FROM Publication p WHERE p.type = :type ORDER BY p.annee DESC")
})
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== INFORMATIONS PRINCIPALES ==========

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false, length = 500)
    private String titre;

    @NotNull(message = "Le type est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypePublication type; // enum interne

    @NotNull(message = "L'année est obligatoire")
    @Column(nullable = false)
    private Integer annee;

    private LocalDate datePublication;

    @Column(length = 200)
    private String revue;           // nom de la revue / conférence

    @Column(length = 100)
    private String editeur;

    @Column(length = 50)
    private String volume;

    @Column(length = 50)
    private String numero;

    @Column(length = 50)
    private String pages;

    @Column(length = 100)
    private String doi;

    @Column(length = 255)
    private String urlPublication;

    @Column(length = 255)
    private String cheminFichier;   // PDF uploadé

    @Column(length = 2000)
    private String resume;

    @Column(length = 500)
    private String motsCles;

    // ========== AUTEURS ==========

    // Auteurs membres du labo (relation)
    @ManyToMany
    @JoinTable(
        name = "publication_auteurs",
        joinColumns = @JoinColumn(name = "publication_id"),
        inverseJoinColumns = @JoinColumn(name = "membre_id")
    )
    @OrderBy("nom ASC")
    private List<Membre> auteurs = new ArrayList<>();

    // Auteurs externes (texte libre)
    @Column(length = 500)
    private String auteursExternes;

    // ========== ÉQUIPE ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    // ========== AJOUTÉE PAR ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ajoute_par_id")
    private Membre ajoutePar;

    @Column(nullable = false)
    private LocalDateTime dateAjout;

    private LocalDateTime dateModification;

    // ========== CONSTRUCTEURS ==========

    public Publication() {
        this.dateAjout = LocalDateTime.now();
        this.annee = LocalDate.now().getYear();
        this.auteurs = new ArrayList<>();
    }

    public Publication(String titre, TypePublication type, Integer annee) {
        this();
        this.titre = titre;
        this.type  = type;
        this.annee = annee;
    }

    // ========== CALLBACK JPA ==========

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // ========== ENUM INTERNE ==========

    public enum TypePublication {
        ARTICLE_JOURNAL("Article de Journal"),
        ARTICLE_CONFERENCE("Article de Conférence"),
        CHAPITRE_LIVRE("Chapitre de Livre"),
        LIVRE("Livre"),
        THESE("Thèse"),
        RAPPORT_TECHNIQUE("Rapport Technique"),
        BREVET("Brevet"),
        AUTRE("Autre");

        private final String libelle;
        TypePublication(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    // ========== MÉTHODES MÉTIER ==========

    public void ajouterAuteur(Membre membre) {
        if (!auteurs.contains(membre)) {
            auteurs.add(membre);
        }
    }

    public void retirerAuteur(Membre membre) {
        auteurs.remove(membre);
    }

    public boolean estRecente() {
        return annee >= LocalDate.now().getYear() - 2;
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypePublication getType() { return type; }
    public void setType(TypePublication type) { this.type = type; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate d) { this.datePublication = d; }

    public String getRevue() { return revue; }
    public void setRevue(String revue) { this.revue = revue; }

    public String getEditeur() { return editeur; }
    public void setEditeur(String editeur) { this.editeur = editeur; }

    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getPages() { return pages; }
    public void setPages(String pages) { this.pages = pages; }

    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }

    public String getUrlPublication() { return urlPublication; }
    public void setUrlPublication(String u) { this.urlPublication = u; }

    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String c) { this.cheminFichier = c; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public String getMotsCles() { return motsCles; }
    public void setMotsCles(String motsCles) { this.motsCles = motsCles; }

    public List<Membre> getAuteurs() { return auteurs; }
    public void setAuteurs(List<Membre> auteurs) { this.auteurs = auteurs; }

    public String getAuteursExternes() { return auteursExternes; }
    public void setAuteursExternes(String a) { this.auteursExternes = a; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public Membre getAjoutePar() { return ajoutePar; }
    public void setAjoutePar(Membre ajoutePar) { this.ajoutePar = ajoutePar; }

    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime d) { this.dateAjout = d; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime d) { this.dateModification = d; }

    // ========== equals / hashCode / toString ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publication)) return false;
        Publication that = (Publication) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return titre + " (" + annee + ") - " + type.getLibelle();
    }
}