package Service;

import DTO.PurchaseBillDTO;
import DTO.SalesBillDTO;

import java.sql.SQLException;
import java.util.List;

public interface BillingService {

    int createPurchaseBill(PurchaseBillDTO dto) throws SQLException;
    int createSalesBill(SalesBillDTO dto) throws SQLException;
    boolean updatePurchaseBill(PurchaseBillDTO dto) throws SQLException;
    boolean updateSalesBill(SalesBillDTO dto) throws SQLException;
    boolean deletePurchaseBill(int id) throws SQLException;
    boolean deleteSalesBill(int id) throws SQLException;

    PurchaseBillDTO getPurchaseBill(int billId) throws SQLException;

    List<PurchaseBillDTO> getAllPurchaseBills() throws SQLException;

    SalesBillDTO getSalesBill(int billId) throws SQLException;

    List<SalesBillDTO> getAllSalesBills() throws SQLException;

    boolean deleteBillItemForBill(int billItemId) throws SQLException;

    boolean deleteBillItems(int billId, String billType) throws SQLException;

}