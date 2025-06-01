package Service;

import DTO.PurchaseBillDTO;

import java.sql.SQLException;
import java.util.List;

// Service: PurchaseBillService.java
public interface PurchaseBillService {
    PurchaseBillDTO createPurchaseBill(PurchaseBillDTO Dto) throws SQLException;
    PurchaseBillDTO getPurchaseBillById(String id) throws SQLException;
    List<PurchaseBillDTO> getAllPurchaseBills() throws SQLException;
    PurchaseBillDTO updatePurchaseBill(PurchaseBillDTO billdto) throws SQLException;
    boolean deletePurchaseBill(String id) throws SQLException;
}