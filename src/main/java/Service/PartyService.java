package Service;

import DAO.PartyDAO;
import Model.Party;
import Validators.PartyValidator;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PartyService<T extends Party, D extends PartyDAO<T>> {
    protected final D dao;
    private final PartyValidator<T, D> validator;

    public PartyService(D dao) {
        this.dao = dao;
        this.validator = new PartyValidator<>(dao); // Pass DAO to validator
    }

    public Map<String, String> validate(T party) throws SQLException {
        return validator.validate(party);
    }

    public T createParty(T party) throws SQLException {
        return dao.insertParty(party);
    }

    public List<T> getAll() throws SQLException {
        return dao.getAllRows();
    }

    public T getPartyById(Integer id) throws SQLException {
        return dao.getPartyById(id);
    }

    public T updateParty(T party) throws SQLException {
        return dao.updateParty(party);
    }

    public boolean deleteParty(Integer id) throws SQLException {
        return dao.deleteParty(id);
    }


    public boolean exists(int id) throws SQLException {
        return (dao.getPartyById(id)!=null);
    }
}