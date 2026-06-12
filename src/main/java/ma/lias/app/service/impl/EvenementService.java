package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.EvenementDAO;
import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.entity.Evenement;
import ma.lias.app.entity.Membre;

import java.util.List;

public class EvenementService extends GenericServiceImpl<Evenement, Long> {

    private final EvenementDAO evenementDAO = new EvenementDAO();

    @Override
    protected GenericDAOImpl<Evenement, Long> getDAO() {
        return evenementDAO;
    }

    public List<Evenement> findAVenir() {
        return evenementDAO.findAVenir();
    }

    public List<Evenement> findByType(Evenement.TypeEvenement type) {
        return evenementDAO.findByType(type);
    }

    public List<Evenement> findByAnnee(int annee) {
        return evenementDAO.findByAnnee(annee);
    }

    public List<Evenement> findByOrganisateur(Long membreId) {
        return evenementDAO.findByOrganisateur(membreId);
    }

    public void terminer(Long evenementId) {
        evenementDAO.findById(evenementId).ifPresent(e -> {
            e.terminer();
            evenementDAO.update(e);
        });
    }

    public void annuler(Long evenementId) {
        evenementDAO.findById(evenementId).ifPresent(e -> {
            e.annuler();
            evenementDAO.update(e);
        });
    }

    public void ajouterOrganisateur(Long evenementId, Membre membre) {
        evenementDAO.findById(evenementId).ifPresent(e -> {
            e.ajouterOrganisateur(membre);
            evenementDAO.update(e);
        });
    }
}