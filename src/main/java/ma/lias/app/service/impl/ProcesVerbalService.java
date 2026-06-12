package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.dao.impl.ProcesVerbalDAO;
import ma.lias.app.entity.Membre;
import ma.lias.app.entity.ProcesVerbal;

import java.time.LocalDate;
import java.util.List;

public class ProcesVerbalService extends GenericServiceImpl<ProcesVerbal, Long> {

    private final ProcesVerbalDAO pvDAO = new ProcesVerbalDAO();

    @Override
    protected GenericDAOImpl<ProcesVerbal, Long> getDAO() {
        return pvDAO;
    }

    public List<ProcesVerbal> findByType(ProcesVerbal.TypeReunion type) {
        return pvDAO.findByType(type);
    }

    public List<ProcesVerbal> findByStatut(ProcesVerbal.StatutPV statut) {
        return pvDAO.findByStatut(statut);
    }

    public List<ProcesVerbal> findSansDocument() {
        return pvDAO.findSansDocument();
    }

    public List<ProcesVerbal> findByPeriode(LocalDate debut, LocalDate fin) {
        return pvDAO.findByPeriode(debut, fin);
    }

    public void marquerCommeTenu(Long pvId) {
        pvDAO.findById(pvId).ifPresent(p -> {
            p.marquerCommeTenu();
            pvDAO.update(p);
        });
    }

    public void uploaderPV(Long pvId, String cheminFichier,
                           String nomOriginal, Membre redacteur) {
        pvDAO.findById(pvId).ifPresent(p -> {
            p.uploaderPV(cheminFichier, nomOriginal, redacteur);
            pvDAO.update(p);
        });
    }

    public void ajouterParticipant(Long pvId, Membre membre) {
        pvDAO.findById(pvId).ifPresent(p -> {
            p.ajouterParticipant(membre);
            pvDAO.update(p);
        });
    }
}