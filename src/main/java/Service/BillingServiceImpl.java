package Service;

import DAO.BillItemDAO;
import DAO.PurchaseBillDAO;
import DAO.SalesBillDAO;
import DTO.BillItemDTO;
import DTO.PurchaseBillDTO;
import DTO.SalesBillDTO;
import Model.BillItem;
import Model.PurchaseBill;
import Model.SalesBill;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillingServiceImpl implements BillingService {
    private final PurchaseBillDAO purchaseBillDAO;
    private final SalesBillDAO salesBillDAO;
    private final BillItemDAO billItemDAO;

    public BillingServiceImpl(PurchaseBillDAO purchaseBillDAO, SalesBillDAO salesBillDAO, BillItemDAO billItemDAO) {
        this.purchaseBillDAO = purchaseBillDAO;
        this.salesBillDAO = salesBillDAO;
        this.billItemDAO = billItemDAO;
    }

    @Override
    public int createPurchaseBill(PurchaseBillDTO dto) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Build and insert purchase bill
            PurchaseBill pb = new PurchaseBill.Builder()
                    .billDate(LocalDate.parse(dto.getBill_date()))
                    .vendorId(dto.getVendor_id())
                    .amount(dto.getAmount())
                    .status(dto.getStatus())
                    .createdAt(dto.getCreated_at())
                    .updatedAt(dto.getUpdated_at())
                    .build();

            int purchaseBillId = purchaseBillDAO.createPurchaseBill(pb, conn); // Note: pass connection

            // Insert all bill items
            for (BillItemDTO item : dto.getItems()) {
                BillItem billItem = new BillItem.Builder()
                        .bill_id(purchaseBillId)
                        .bill_type("PURCHASE")
                        .product_id(item.getProduct_id())
                        .quantity(item.getQuantity())
                        .rate(item.getRate())
                        .build();


                billItemDAO.insertBillItems(billItem, conn); // Note: pass connection

            }

            conn.commit(); // All good, commit
            return purchaseBillId;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Something failed, rollback all
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw ex; // Re-throw the original exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restore auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int createSalesBill(SalesBillDTO dto) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Build and insert purchase bill
            SalesBill salesBill = new SalesBill.Builder()
                    .billDate(LocalDate.parse(dto.getBill_date()))
                    .customerId(dto.getCustomer_id())
                    .amount(dto.getAmount())
                    .status(dto.getStatus())
                    .createdAt(dto.getCreated_at())
                    .updatedAt(dto.getUpdated_at())
                    .build();

            int salesBillId = salesBillDAO.createSalesBill(salesBill, conn); // Note: pass connection

            // Insert all bill items
            for (BillItemDTO item : dto.getItems()) {
                BillItem billItem = new BillItem.Builder()
                        .bill_id(salesBillId)
                        .bill_type("SALES")
                        .product_id(item.getProduct_id())
                        .quantity(item.getQuantity())
                        .rate(item.getRate())
                        .build();

                billItemDAO.insertBillItems(billItem, conn); // Note: pass connection

            }

            conn.commit(); // All good, commit
            return salesBillId;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Something failed, rollback all
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw ex; // Re-throw the original exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restore auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean updatePurchaseBill(PurchaseBillDTO dto) throws SQLException {
        List<BillItemDTO> billItems = dto.getItems();
        Connection conn = DBConnection.getInstance().getConnection();
        boolean result = false;
        try {
            conn.setAutoCommit(false);
            if (billItems != null) {
                for (BillItemDTO billItem : billItems) {
                    billItem.setBill_id(dto.getId());
                    billItemDAO.updateBillItem(billItem, conn);
                }
            }
            result = purchaseBillDAO.updatePurchaseBill(dto, conn);

            conn.commit();
        } catch (Exception e) {
            System.out.println(e);
            conn.rollback();
            throw e;
        }
        return result;
    }

    @Override
    public boolean updateSalesBill(SalesBillDTO dto) throws SQLException {
        List<BillItemDTO> billItems = dto.getItems();
        Connection conn = DBConnection.getInstance().getConnection();
        Boolean result = false;
        try {
            conn.setAutoCommit(false);

            if (billItems != null) {
                for (BillItemDTO billItem : billItems) {
                    billItem.setBill_id(dto.getId());
                    billItemDAO.updateBillItem(billItem, conn);
                }
            }
            result = salesBillDAO.updateSalesBill(dto, conn);

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
        return result;
    }

    @Override
    public boolean deletePurchaseBill(int id) throws SQLException {
        deleteBillItems(id, "PURCHASE");
        return purchaseBillDAO.deletePurchaseBillById(id);
    }

    @Override
    public boolean deleteSalesBill(int id) throws SQLException {
        deleteBillItems(id, "SALES");
        return salesBillDAO.deleteSalesBillById(id);
    }

    @Override
    public boolean deleteBillItemForBill(int id) throws SQLException {
        return billItemDAO.deleteBillItemById(id);
    }

    @Override
    public boolean deleteBillItems(int billId, String billType) throws SQLException {
        return billItemDAO.deleteBillItemsByBillId(billId, billType);
    }

    @Override
    public PurchaseBillDTO getPurchaseBill(int billId) throws SQLException {
        List<BillItemDTO> items = billItemDAO.getBillItemsByBillId(billId, "PURCHASE", DBConnection.getInstance().getConnection());
        PurchaseBillDTO dto = purchaseBillDAO.getPurchaseBillById(billId);
        dto.setItems(items);
        return dto;
    }

    @Override
    public List<PurchaseBillDTO> getAllPurchaseBills() throws SQLException {
        List<PurchaseBillDTO> bills = purchaseBillDAO.getAllPurchaseBills();

        for (PurchaseBillDTO bill : bills)
            bill.setItems(billItemDAO.getBillItemsByBillId(bill.getId(), "PURCHASE", DBConnection.getInstance().getConnection()));

        return bills;
    }

    @Override
    public SalesBillDTO getSalesBill(int billId) throws SQLException {
        List<BillItemDTO> items = billItemDAO.getBillItemsByBillId(billId, "SALES", DBConnection.getInstance().getConnection());
        SalesBillDTO dto = salesBillDAO.getSalesBillById(billId);
        dto.setItems(items);
        return dto;
    }

    @Override
    public List<SalesBillDTO> getAllSalesBills() throws SQLException {
        List<SalesBillDTO> bills = salesBillDAO.getAllSalesBills();

        for (SalesBillDTO bill : bills)
            bill.setItems(billItemDAO.getBillItemsByBillId(bill.getId(), "SALES", DBConnection.getInstance().getConnection()));

        return bills;
    }


}
