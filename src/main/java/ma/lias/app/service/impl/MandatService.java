package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.dao.impl.MandatDAO;
import ma.lias.app.entity.Mandat;
import ma.lias.app.entity.Membre;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MandatService extends GenericServiceImpl<Mandat, Long> {

    private final MandatDAO mandatDAO = new MandatDAO();

    @Override
    protected GenericDAOImpl<Mandat, Long> getDAO() {
        return mandatDAO;
    }

    public Optional<Mandat> findMandatActif() {
        return mandatDAO.findActif();
    }

    public List<Mandat> findTous() {
        return mandatDAO.findTous();
    }

    /**
     * Créer un nouveau mandat → termine automatiquement l'ancien
     */
    public void creerMandat(Membre directeur, Membre viceDirecteur,
                            LocalDate dateDebut) {
        // Terminer le mandat actif
        mandatDAO.findActif().ifPresent(actif -> {
            actif.setDateFin(dateDebut.minusDays(1));
            mandatDAO.update(actif);
        });

        // Créer le nouveau
        Mandat nouveau = new Mandat();
        nouveau.setDirecteur(directeur);
        nouveau.setViceDirecteur(viceDirecteur);
        nouveau.setDateDebut(dateDebut);
        mandatDAO.save(nouveau);
    }

    public void terminerMandat(Long mandatId) {
        mandatDAO.findById(mandatId).ifPresent(m -> {
            m.setDateFin(LocalDate.now());
            mandatDAO.update(m);
        });
    }
}