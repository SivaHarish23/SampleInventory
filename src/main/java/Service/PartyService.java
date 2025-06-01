package Service;

import DTO.PartyDTO;
import DAO.PartyDAO;

import java.sql.SQLException;
import java.util.List;

public class PartyService<T extends PartyDTO, D extends PartyDAO<T>> {
    protected final D dao;

    public PartyService(D dao) {
        this.dao = dao;
    }

    public T createParty(T partyDTO) throws SQLException {
        return dao.insertParty(partyDTO);
    }

    public List<T> getAll() throws SQLException {
        return dao.getAllRows();
    }

    public T getPartyById(String id) throws SQLException {
        id = id.substring(4);
        return dao.getPartyById(Integer.parseInt(id));
    }

    public T updateParty(T dto) throws SQLException {
        dto.setId(dto.getId().substring(4));
        return dao.updateParty(dto);
    }

    public boolean deleteParty(String id) throws SQLException {
        id = id.substring(4);
        return dao.deleteParty(Integer.parseInt(id));
    }

//    private PartyDTO convertToDTO(T party) {
//        return new PartyDTO.Builder()
//                .id(party.getId())
//                .name(party.getName())
//                .location(party.getLocation())
//                .type(party instanceof Model.Customer ? PartyDTO.Type.CUSTOMER : PartyDTO.Type.VENDOR)
//                .build();
//    }
}