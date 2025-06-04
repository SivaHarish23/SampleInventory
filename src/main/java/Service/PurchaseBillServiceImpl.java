package Service;

import DAO.BillLineItemDAO;
import DAO.ProductDAO;
import DAO.PurchaseBillDAO;
import DAO.VendorDAO;
import DTO.PurchaseBillDTO;
import Model.BillLineItem;
import Model.PurchaseBill;
import Model.SalesInvoice;
import Util.DBConnection;
import Validators.PurchaseBillValidator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class PurchaseBillServiceImpl implements PurchaseBillService {
    private final PurchaseBillDAO purchaseBillDAO = new PurchaseBillDAO();
    private final BillLineItemServiceImpl billLineItemService = new BillLineItemServiceImpl();
    private final PurchaseBillValidator validator = new PurchaseBillValidator();

    public Map<String, List<String>> validate(PurchaseBillDTO purchaseBillDTO, boolean isUpdate) throws SQLException {
        return validator.validate(purchaseBillDTO, isUpdate);
    }
    public boolean isReceived(Integer billId) throws SQLException {
        PurchaseBill bill = getPurchaseBillById(billId);
        return bill != null && bill.getStatus() == 1;
    }

    @Override
    public PurchaseBill createPurchaseBill(PurchaseBill bill) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            BigDecimal amount = BigDecimal.valueOf(0);
            Integer billId = null;
            PurchaseBill createdBill = new PurchaseBill();
            List<BillLineItem> createdItems = new ArrayList<>();
            try {
                bill.setAmount(amount);
                PurchaseBill insertBill = purchaseBillDAO.createBill(bill, conn);

                if (insertBill == null) {
                    System.out.println("Error, empty createdBill");
                    conn.rollback();
                    System.out.println("ERROR in creating purchase bill , purchase bill not created");
                    return null;
                }
                billId = insertBill.getId();
            }catch (SQLException e){
                System.out.println("PurchaseBillServiceImpl : createPurchaseBill : Generating key part : " +e);
                conn.rollback();
                throw e;
            }
            try{
                List<BillLineItem> billItems = bill.getBill_line_items();
                if (billItems != null) {
                    for(BillLineItem billLineItem : billItems){
                        billLineItem.setBill_id(billId);
                        BillLineItem createdItem = billLineItemService.createBillItem(billLineItem,conn);
                        if(createdItem == null){
                            System.out.println("PurchaseBillServiceImpl : createPurchaseBill : inserting bill item : " );
                            conn.rollback(); return null;
                        }
                        createdItems.add(createdItem);
                    }
                }
            }catch (SQLException e){
                System.out.println("PurchaseBillServiceImpl : createPurchaseBill : inserting bill item : " +e);
                conn.rollback();
                throw e;
            }
            try{
                amount = billLineItemService.getBillAmount(billId,conn);
            }catch (SQLException e){
                System.out.println("PurchaseBillServiceImpl : createPurchaseBill : fetching amount : " +e);
                conn.rollback();
                throw e;
            }
            try{
                PurchaseBill m = new PurchaseBill.Builder().setId(billId).setAmount(amount).build();
                createdBill = purchaseBillDAO.updatePurchaseBill(m , conn);
                createdBill.setBill_line_items(createdItems);
            }catch (SQLException e){
                System.out.println("PurchaseBillServiceImpl : createPurchaseBill : updating amount : " +e);
                conn.rollback();
                throw e;
            }
            conn.commit();
            return createdBill;
        }catch (Exception e){
            System.out.println("PurchaseBillServiceImpl : createPurchaseBill : overall exception : " +e);
            throw e;
        }
    }

    @Override
    public PurchaseBill getPurchaseBillById(Integer id) throws SQLException {
        try(Connection conn = DBConnection.getInstance().getConnection()){
            PurchaseBill purchaseBill = purchaseBillDAO.getBillById(id, conn);
            if(purchaseBill != null){
                purchaseBill.setBill_line_items(billLineItemService.getItemsByBillId(id,conn));
            }
            return purchaseBill;
        }catch (SQLException e){
            System.out.println("PurchaseBillServiceImpl : getPurchaseBillById " +e);
            throw e;
        }
    }

    @Override
    public List<PurchaseBill> getAllPurchaseBills() throws SQLException {
        List<PurchaseBill> bills = new ArrayList<>();
        try(Connection conn = DBConnection.getInstance().getConnection()){
             bills = purchaseBillDAO.getAllBills(conn);
        }catch (SQLException e){
            System.out.println("PurchaseBillServiceImpl : while getting purchase bills " +e);
            throw e;
        }
        if(bills != null){
            try(Connection conn = DBConnection.getInstance().getConnection()){
                for(PurchaseBill pb : bills){
                    List<BillLineItem> billItems = new ArrayList<>();
                     billItems = billLineItemService.getItemsByBillId(pb.getId(),conn);
                     pb.setBill_line_items(billItems);
                }
            }catch (SQLException e){
                System.out.println("PurchaseBillServiceImpl : while bill line items " +e);
                throw e;
            }
        }
        return bills;
    }

    @Override
    public PurchaseBill updatePurchaseBill(PurchaseBill bill) throws SQLException {
        Integer billId = bill.getId();
        List<BillLineItem> oldItems = new ArrayList<>();
        List<BillLineItem> updatedItems = new ArrayList<>();
        try(Connection conn = DBConnection.getInstance().getConnection()){
            oldItems = billLineItemService.getItemsByBillId(billId,conn);
        }catch (SQLException e){
            System.out.println("PurchaseBillServiceImpl: updatePurchaseBill : getting old items " +e);
            throw e;
        }
        try(Connection conn = DBConnection.getInstance().getConnection()){
            conn.setAutoCommit(false);
            for(BillLineItem item : bill.getBill_line_items()){
                BillLineItem updatedItem = (item.getId() == null) ? billLineItemService.createBillItem(item,conn) : billLineItemService.updateBillItem(item,conn);
                if(updatedItem != null) updatedItems.add(updatedItem);
            }

            Set<Integer> updatedIds = updatedItems.stream()
                    .map(BillLineItem::getId)
                    .collect(Collectors.toSet());

            for (BillLineItem oldItem : oldItems) {
                if (oldItem.getId() != null && !updatedIds.contains(oldItem.getId())) {
                    billLineItemService.deleteBillItem(oldItem.getId(), conn);
                }
            }

            bill.setAmount(billLineItemService.getBillAmount(billId,conn));

            PurchaseBill updatedBill = purchaseBillDAO.updatePurchaseBill(bill,conn);
            updatedBill.setBill_line_items(billLineItemService.getItemsByBillId(billId,conn));
            conn.commit();
            return updatedBill;
        }catch (SQLException e){
            System.out.println("PurchaseBillServiceImpl: updatePurchaseBill : getting old items " +e);
            throw e;
        }
    }

    @Override
    public boolean deletePurchaseBill(Integer id) throws SQLException {
        return purchaseBillDAO.deletePurchaseBill(id,DBConnection.getInstance().getConnection());
    }

    public List<BillLineItem> findDuplicateLineItems(List<BillLineItem> items) {
        Map<Integer, List<BillLineItem>> itemMap = new HashMap<>();
        List<BillLineItem> duplicates = new ArrayList<>();

        for (BillLineItem item : items) {
            int productId = item.getProduct_id();
            itemMap.putIfAbsent(productId, new ArrayList<>());
            itemMap.get(productId).add(item);
        }

        for (Map.Entry<Integer, List<BillLineItem>> entry : itemMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicates.addAll(entry.getValue());
            }
        }

        return duplicates; // Empty list means no duplicates
    }

    public boolean exists(int id) throws SQLException {
        return purchaseBillDAO.getBillById(id,DBConnection.getInstance().getConnection())!=null;
    }
}
