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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Lie un chef d'équipe (Membre) à une Equipe pour un Mandat donné.
 * Permet l'historisation complète des chefs d'équipe.
 */
@Entity
@Table(name = "chef_equipe_mandats", uniqueConstraints = {
    @UniqueConstraint(
        name = "uq_chef_equipe_mandat",
        columnNames = {"equipe_id", "mandat_id"}
        // Une seule entrée active par équipe par mandat
    )
})
@NamedQueries({
    @NamedQuery(
        name = "ChefEquipeMandat.findActifs",
        query = "SELECT c FROM ChefEquipeMandat c WHERE c.dateFin IS NULL"
    ),
    @NamedQuery(
        name = "ChefEquipeMandat.findByEquipe",
        query = "SELECT c FROM ChefEquipeMandat c WHERE c.equipe = :equipe ORDER BY c.dateDebut DESC"
    ),
    @NamedQuery(
        name = "ChefEquipeMandat.findByMandat",
        query = "SELECT c FROM ChefEquipeMandat c WHERE c.mandat = :mandat"
    ),
    @NamedQuery(
        name = "ChefEquipeMandat.findByMembre",
        query = "SELECT c FROM ChefEquipeMandat c WHERE c.membre = :membre ORDER BY c.dateDebut DESC"
    )
})
public class ChefEquipeMandat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== RELATIONS ==========

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;          // le chef d'équipe

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mandat_id", nullable = false)
    private Mandat mandat;

    // ========== PÉRIODE ==========

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;      // null = toujours actif

    // ========== CONSTRUCTEURS ==========

    public ChefEquipeMandat() {}

    public ChefEquipeMandat(Membre membre, Equipe equipe,
                            Mandat mandat, LocalDate dateDebut) {
        this.membre    = membre;
        this.equipe    = equipe;
        this.mandat    = mandat;
        this.dateDebut = dateDebut;
    }

    // ========== MÉTHODES MÉTIER ==========

    /** true si cette affectation est actuellement active */
    public boolean estActif() {
        return dateFin == null;
    }

    /** Terminer l'affectation aujourd'hui */
    public void terminer() {
        this.dateFin = LocalDate.now();
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public Mandat getMandat() { return mandat; }
    public void setMandat(Mandat mandat) { this.mandat = mandat; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    // ========== equals / hashCode / toString ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChefEquipeMandat)) return false;
        ChefEquipeMandat that = (ChefEquipeMandat) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ChefEquipeMandat{" +
               "membre=" + membre.getNomComplet() +
               ", equipe=" + equipe.getNom() +
               ", mandat=" + mandat.getDateDebut() + " → " +
                   (mandat.getDateFin() == null ? "Présent" : mandat.getDateFin()) +
               ", dateDebut=" + dateDebut +
               ", dateFin=" + (dateFin == null ? "Présent" : dateFin) +
               '}';
    }
}