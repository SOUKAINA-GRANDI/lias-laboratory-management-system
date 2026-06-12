package ma.lias.app.entity;

import ma.lias.app.enums.RoleMembre;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "role_historique")
@NamedQueries({
    @NamedQuery(name = "RoleHistorique.findByMembre",
            query = "SELECT r FROM RoleHistorique r WHERE r.membre.id = :membreId ORDER BY r.dateDebut DESC")
})
public class RoleHistorique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== RELATION AVEC MEMBRE ==========
    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    // ========== RÔLE ==========
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoleMembre role;

    // ========== PÉRIODE ==========
    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin; // null = rôle actif

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

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public RoleMembre getRole() { return role; }
    public void setRole(RoleMembre role) { this.role = role; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return role + " (" + dateDebut + " → " +
                (dateFin == null ? "Présent" : dateFin) + ")";
    }
}