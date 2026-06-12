package ma.lias.app.service.impl;

import ma.lias.app.dao.impl.EquipeDAO;
import ma.lias.app.dao.impl.GenericDAOImpl;
import ma.lias.app.entity.Equipe;

import java.util.List;
import java.util.Optional;

public class EquipeService extends GenericServiceImpl<Equipe, Long> {

    private final EquipeDAO equipeDAO = new EquipeDAO();

    @Override
    protected GenericDAOImpl<Equipe, Long> getDAO() {
        return equipeDAO;
    }

    public Optional<Equipe> findByNom(String nom) {
        return equipeDAO.findByNom(nom);
    }

    public List<Equipe> findActives() {
        return equipeDAO.findActives();
    }

    public List<Equipe> findInactives() {
        return equipeDAO.findInactives();
    }

    public void fermer(Long equipeId) {
        equipeDAO.findById(equipeId).ifPresent(e -> {
            e.fermer();
            equipeDAO.update(e);
        });
    }

    public void reactiver(Long equipeId) {
        equipeDAO.findById(equipeId).ifPresent(e -> {
            e.reactiver();
            equipeDAO.update(e);
        });
    }
}