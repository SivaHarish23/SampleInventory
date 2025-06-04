package Validators;

import DAO.PartyDAO;
import Model.Party;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class PartyValidator<T extends Party, D extends PartyDAO<T>> {

    protected final D dao;

    public PartyValidator(D dao) {
        this.dao = dao;
    }

    public Map<String, String> validate(T party) throws SQLException {
        Map<String, String> errors = new HashMap<>();

        // Validate name (required & unique)
        if (party.getName() == null || party.getName().trim().isEmpty()) {
            errors.put("name", "Party name is required.");
        } else if (dao.findByName(party.getName()) != null) {
            errors.put("name", "Party name already exists.");
        }

        // Validate phone number (optional, but must be valid if provided)
        String phone = party.getPhoneNumber();
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("\\d{10}")) {
                errors.put("phone_number", "Phone number must be a valid 10-digit number.");
            }
        }

        // Validate location (optional, but must not be blank if provided)
        String location = party.getLocation();
        if (location != null && location.trim().isEmpty()) {
            errors.put("location", "Location must not be blank if provided.");
        }

        return errors;
    }
}
