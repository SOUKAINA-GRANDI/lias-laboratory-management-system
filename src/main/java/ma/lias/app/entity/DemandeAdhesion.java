package ma.lias.app.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import ma.lias.app.enums.StatutDemande;
import ma.lias.app.enums.StatutMembre;

/**
 * Demande d'adhésion au laboratoire LIAS
 *
 * RÈGLES MÉTIER :
 * - Soumission en ligne par un candidat
 * - Décision uniquement par le directeur en mandat actif
 * - Acceptée → création automatique du compte Membre
 * - Refusée → notification avec motif
 * - Historique complet conservé
 */
@Entity
@Table(name = "demandes_adhesion")
@NamedQueries({
    @NamedQuery(name = "DemandeAdhesion.findAll",
                query = "SELECT d FROM DemandeAdhesion d ORDER BY d.dateSoumission DESC"),
    @NamedQuery(name = "DemandeAdhesion.findEnAttente",
                query = "SELECT d FROM DemandeAdhesion d WHERE d.statut = ma.lias.app.enums.StatutDemande.EN_ATTENTE ORDER BY d.dateSoumission ASC"),
    @NamedQuery(name = "DemandeAdhesion.findByStatut",
                query = "SELECT d FROM DemandeAdhesion d WHERE d.statut = :statut ORDER BY d.dateSoumission DESC"),
    @NamedQuery(name = "DemandeAdhesion.findByEmail",
                query = "SELECT d FROM DemandeAdhesion d WHERE d.emailCandidat = :email ORDER BY d.dateSoumission DESC")
})
public class DemandeAdhesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== INFORMATIONS DU CANDIDAT ==========

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 50)
    private String nomCandidat;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false, length = 50)
    private String prenomCandidat;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(nullable = false, length = 100)
    private String emailCandidat;

    @Column(length = 20)
    private String telephoneCandidat;

    @NotNull(message = "Le statut demandé est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutMembre statutDemande; // PERMANENT, ASSOCIE, DOCTORANT

    @Column(length = 100)
    private String etablissementOrigine;

    @Column(length = 100)
    private String laboratoireOrigine;

    @Column(length = 100)
    private String grade;

    @Column(length = 100)
    private String specialite;

    // ========== DOCUMENTS DE CANDIDATURE ==========

    @Column(length = 1000)
    private String motivationLettre;    // texte de motivation

    @Column(length = 255)
    private String cheminCV;            // chemin fichier CV uploadé

    @Column(length = 255)
    private String cheminDocuments;     // autres documents joints

    // ========== STATUT DE LA DEMANDE ==========

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(nullable = false)
    private LocalDateTime dateSoumission;

    private LocalDateTime dateDecision;  // null = pas encore traitée

    @Column(length = 500)
    private String motifDecision;        // motif refus ou commentaire acceptation

    // ========== DÉCIDEUR ==========

    // Le directeur qui a traité la demande
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decideur_id")
    private Membre decideur;             // null = pas encore décidée

    // Le mandat actif au moment de la décision
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mandat_id")
    private Mandat mandat;

    // ========== RÉSULTAT ==========

    // Membre créé après acceptation (null si refusée/en attente)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_cree_id", unique = true)
    private Membre membreCree;

    // ========== CONSTRUCTEURS ==========

    public DemandeAdhesion() {
        this.dateSoumission = LocalDateTime.now();
        this.statut = StatutDemande.EN_ATTENTE;
    }

    public DemandeAdhesion(String nom, String prenom,
                           String email, StatutMembre statutDemande) {
        this();
        this.nomCandidat = nom;
        this.prenomCandidat = prenom;
        this.emailCandidat = email;
        this.statutDemande = statutDemande;
    }

    // ========== MÉTHODES MÉTIER ==========

    /**
     * Accepter la demande → lier le membre créé
     */
    public void accepter(Membre decideur, Mandat mandat, Membre membreCree) {
        this.statut       = StatutDemande.ACCEPTEE;
        this.dateDecision = LocalDateTime.now();
        this.decideur     = decideur;
        this.mandat       = mandat;
        this.membreCree   = membreCree;
    }

    /**
     * Refuser la demande avec motif obligatoire
     */
    public void refuser(Membre decideur, Mandat mandat, String motif) {
        this.statut        = StatutDemande.REFUSEE;
        this.dateDecision  = LocalDateTime.now();
        this.decideur      = decideur;
        this.mandat        = mandat;
        this.motifDecision = motif;
    }

    /**
     * Annuler par le candidat lui-même
     */
    public void annuler() {
        if (this.statut.peutEtreModifiee()) {
            this.statut       = StatutDemande.ANNULEE;
            this.dateDecision = LocalDateTime.now();
        }
    }

    /** Passer en cours d'examen */
    public void mettreEnCours() {
        if (this.statut == StatutDemande.EN_ATTENTE) {
            this.statut = StatutDemande.EN_COURS;
        }
    }

    public boolean estEnAttente() {
        return statut == StatutDemande.EN_ATTENTE;
    }

    public boolean estAcceptee() {
        return statut == StatutDemande.ACCEPTEE;
    }

    public String getNomCompletCandidat() {
        return prenomCandidat + " " + nomCandidat;
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public String getNomCandidat() { return nomCandidat; }
    public void setNomCandidat(String nomCandidat) { this.nomCandidat = nomCandidat; }

    public String getPrenomCandidat() { return prenomCandidat; }
    public void setPrenomCandidat(String prenomCandidat) { this.prenomCandidat = prenomCandidat; }

    public String getEmailCandidat() { return emailCandidat; }
    public void setEmailCandidat(String emailCandidat) { this.emailCandidat = emailCandidat; }

    public String getTelephoneCandidat() { return telephoneCandidat; }
    public void setTelephoneCandidat(String t) { this.telephoneCandidat = t; }

    public StatutMembre getStatutDemande() { return statutDemande; }
    public void setStatutDemande(StatutMembre statutDemande) { this.statutDemande = statutDemande; }

    public String getEtablissementOrigine() { return etablissementOrigine; }
    public void setEtablissementOrigine(String e) { this.etablissementOrigine = e; }

    public String getLaboratoireOrigine() { return laboratoireOrigine; }
    public void setLaboratoireOrigine(String l) { this.laboratoireOrigine = l; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getMotivationLettre() { return motivationLettre; }
    public void setMotivationLettre(String m) { this.motivationLettre = m; }

    public String getCheminCV() { return cheminCV; }
    public void setCheminCV(String cheminCV) { this.cheminCV = cheminCV; }

    public String getCheminDocuments() { return cheminDocuments; }
    public void setCheminDocuments(String c) { this.cheminDocuments = c; }

    public StatutDemande getStatut() { return statut; }
    public void setStatut(StatutDemande statut) { this.statut = statut; }

    public LocalDateTime getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDateTime d) { this.dateSoumission = d; }

    public LocalDateTime getDateDecision() { return dateDecision; }
    public void setDateDecision(LocalDateTime d) { this.dateDecision = d; }

    public String getMotifDecision() { return motifDecision; }
    public void setMotifDecision(String motifDecision) { this.motifDecision = motifDecision; }

    public Membre getDecideur() { return decideur; }
    public void setDecideur(Membre decideur) { this.decideur = decideur; }

    public Mandat getMandat() { return mandat; }
    public void setMandat(Mandat mandat) { this.mandat = mandat; }

    public Membre getMembreCree() { return membreCree; }
    public void setMembreCree(Membre membreCree) { this.membreCree = membreCree; }

    // ========== equals / hashCode / toString ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DemandeAdhesion)) return false;
        DemandeAdhesion that = (DemandeAdhesion) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "DemandeAdhesion{" +
               "candidat=" + getNomCompletCandidat() +
               ", statut=" + statut.getLibelle() +
               ", date=" + dateSoumission.toLocalDate() +
               '}';
    }
}