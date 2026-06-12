package ma.lias.app.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Attribution d'un matériel à un membre
 * Permet l'historique complet : qui a reçu quoi et quand
 */
@Entity
@Table(name = "attributions_materiel")
public class AttributionMateriel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valide_par_id")
    private Membre validePar;           // directeur/admin qui a validé

    @Column(nullable = false)
    private LocalDate dateAttribution;

    private LocalDate dateRetour;       // null = toujours en possession

    @Column(length = 500)
    private String notes;

    // ========== CONSTRUCTEURS ==========

    public AttributionMateriel() {}

    public AttributionMateriel(Materiel materiel, Membre membre, Membre validePar) {
        this.materiel        = materiel;
        this.membre          = membre;
        this.validePar       = validePar;
        this.dateAttribution = LocalDate.now();
    }

    // ========== MÉTHODES MÉTIER ==========

    public void terminer() {
        this.dateRetour = LocalDate.now();
    }

    public boolean estActive() {
        return dateRetour == null;
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) { this.materiel = materiel; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public Membre getValidePar() { return validePar; }
    public void setValidePar(Membre validePar) { this.validePar = validePar; }

    public LocalDate getDateAttribution() { return dateAttribution; }
    public void setDateAttribution(LocalDate dateAttribution) { this.dateAttribution = dateAttribution; }

    public LocalDate getDateRetour() { return dateRetour; }
    public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributionMateriel)) return false;
        AttributionMateriel that = (AttributionMateriel) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public String toString() {
        return materiel.getNom() + " → " +
               membre.getNomComplet() +
               " (" + dateAttribution + ")" +
               (estActive() ? " ✓" : " retourné " + dateRetour);
    }
}