package ma.lias.app.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mandats")
@NamedQueries({
    @NamedQuery(name = "Mandat.findActif",
            query = "SELECT m FROM Mandat m WHERE m.dateFin IS NULL"),
    @NamedQuery(name = "Mandat.findAll",
            query = "SELECT m FROM Mandat m ORDER BY m.dateDebut DESC")
})
public class Mandat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== PÉRIODE ==========
    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin; // null = mandat en cours

    // ========== GOUVERNANCE ==========
    @ManyToOne
    @JoinColumn(name = "directeur_id", nullable = false)
    private Membre directeur;

    @ManyToOne
    @JoinColumn(name = "vice_directeur_id")
    private Membre viceDirecteur;

    @Column(length = 500)
    private String description;

    // ========== MÉTHODES MÉTIER ==========

    public boolean estActif() {
        return dateFin == null;
    }

    public boolean estTermine() {
        return dateFin != null;
    }

    // ========== GETTERS & SETTERS ==========

    public Long getId() { return id; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Membre getDirecteur() { return directeur; }
    public void setDirecteur(Membre directeur) { this.directeur = directeur; }

    public Membre getViceDirecteur() { return viceDirecteur; }
    public void setViceDirecteur(Membre viceDirecteur) { this.viceDirecteur = viceDirecteur; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Mandat " + dateDebut + " → " +
                (dateFin == null ? "Présent" : dateFin) +
                " | Directeur: " + directeur.getNomComplet();
    }
}