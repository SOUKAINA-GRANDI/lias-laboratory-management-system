package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.ConventionDAO;
import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.entity.Convention;
import ma.lias.app.entity.Membre;

import java.time.LocalDate;
import java.util.List;

public class ConventionService extends GenericServiceImpl<Convention, Long> {

    private final ConventionDAO conventionDAO = new ConventionDAO();

    @Override
    protected GenericDAOImpl<Convention, Long> getDAO() {
        return conventionDAO;
    }

    public List<Convention> findActives() {
        return conventionDAO.findActives();
    }

    public List<Convention> findByType(Convention.TypeConvention type) {
        return conventionDAO.findByType(type);
    }

    public List<Convention> findByPartenaire(String nom) {
        return conventionDAO.findByPartenaire(nom);
    }

    public List<Convention> findExpirantDans(int jours) {
        return conventionDAO.findExpirantAvant(LocalDate.now().plusDays(jours));
    }

    public void suspendre(Long conventionId) {
        conventionDAO.findById(conventionId).ifPresent(c -> {
            c.suspendre();
            conventionDAO.update(c);
        });
    }

    public void resilier(Long conventionId) {
        conventionDAO.findById(conventionId).ifPresent(c -> {
            c.resilier();
            conventionDAO.update(c);
        });
    }

    public void renouveler(Long conventionId, LocalDate nouvelleDateExpiration) {
        conventionDAO.findById(conventionId).ifPresent(c -> {
            c.renouveler(nouvelleDateExpiration);
            conventionDAO.update(c);
        });
    }

    public void ajouterMembre(Long conventionId, Membre membre) {
        conventionDAO.findById(conventionId).ifPresent(c -> {
            c.ajouterMembre(membre);
            conventionDAO.update(c);
        });
    }
}