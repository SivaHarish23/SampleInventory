package Service;

import DTO.PartyDTO;
import DAO.PartyDAO;
import Model.Party;

import java.sql.SQLException;
import java.util.List;

public class PartyService<T extends Party, D extends PartyDAO<T>> {
    protected final D dao;

    public PartyService(D dao) {
        this.dao = dao;
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

}