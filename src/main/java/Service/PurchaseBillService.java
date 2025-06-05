package Service;

import Model.PurchaseBill;
import java.sql.SQLException;
import java.util.List;

public interface PurchaseBillService {
    PurchaseBill createPurchaseBill(PurchaseBill bill) throws SQLException;
    PurchaseBill getPurchaseBillById(Integer id) throws SQLException;
    List<PurchaseBill> getAllPurchaseBills() throws SQLException;
    PurchaseBill updatePurchaseBill(PurchaseBill bill) throws SQLException;
    boolean deletePurchaseBill(Integer id) throws SQLException;
}