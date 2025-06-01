package Service;

import DAO.PurchaseBillDAO;
import DTO.BillLineItemDTO;
import DTO.PurchaseBillDTO;
import Model.PurchaseBill;
import Model.PurchaseBillStatus;
import Service.BillLineItemServiceImpl;
import Util.DBConnection;
import Util.TimeUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PurchaseBillServiceImpl implements PurchaseBillService {
    private final PurchaseBillDAO purchaseBillDAO = new PurchaseBillDAO();
    //    private final BillLineItemDAO billLineItemDAO = new BillLineItemDAO();
    private final BillLineItemServiceImpl billLineItemService = new BillLineItemServiceImpl();

    @Override
    public PurchaseBillDTO createPurchaseBill(PurchaseBillDTO dto) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                BigDecimal amount = BigDecimal.valueOf(0);
                dto.setAmount(amount);
                String dateNow = TimeUtil.epochToString(Instant.now().getEpochSecond());
                dto.setCreated_at(dateNow);
                dto.setUpdated_at(dateNow);

                PurchaseBill purchaseBill = new PurchaseBill(dto);
                PurchaseBillDTO createdBill = purchaseBillDAO.createBill(purchaseBill, conn);
                if (createdBill == null) {
                    System.out.println("ERROR in creating purchase bill , purchase bill not created");
                    conn.rollback();
                    return null;
                }

                List<BillLineItemDTO> createdItems = new ArrayList<>();
                List<BillLineItemDTO> getItems = dto.getBillLineItems();
//                if(getItems!=null){
                    for (BillLineItemDTO item : getItems) {
                        item.setBill_id(createdBill.getId());
                        BillLineItemDTO createdItem = billLineItemService.createBillItem(item, conn);
                        if (createdItem == null) {
                            System.out.println("ERROR in creating bill line item, item not created");
                            conn.rollback();
                            return null;
                        }
                        createdItems.add(createdItem);
                        amount = amount.add(createdItem.getAmount());
                    }
//                }

                PurchaseBillDTO updateAmountDto = new PurchaseBillDTO();
                updateAmountDto.setId(createdBill.getId().substring(4));
                updateAmountDto.setAmount(amount);

                createdBill = purchaseBillDAO.updatePurchaseBill(updateAmountDto, conn);

                createdBill.setBillLineItems(createdItems);
                conn.commit();
                return createdBill;

            } catch (Exception e) {
                conn.rollback();
                System.out.println(e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public PurchaseBillDTO getPurchaseBillById(String id) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            int intId = Integer.parseInt(id.substring(4));
            PurchaseBillDTO dto = purchaseBillDAO.getBillById(intId, conn);

            if (dto != null) {
                List<BillLineItemDTO> items = billLineItemService.getItemsByBillId(id, conn);
                dto.setBillLineItems(items);
            }
            return dto;
        }
    }

    @Override
    public List<PurchaseBillDTO> getAllPurchaseBills() throws SQLException {
        List<PurchaseBillDTO> result = new ArrayList<>();
        List<PurchaseBillDTO> bills = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection()) {
             bills= purchaseBillDAO.getAllBills(conn);

        }
        if(bills != null){
            try(Connection conn = DBConnection.getInstance().getConnection()){
                for (PurchaseBillDTO bill : bills) {
                    int intId = Integer.parseInt(bill.getId().replace("BIL-", ""));
                    List<BillLineItemDTO> items = billLineItemService.getItemsByBillId(bill.getId(), conn);
                    bill.setBillLineItems(items);
                    result.add(bill);
                }
            }
        }

        return result;
    }

    @Override
    public PurchaseBillDTO updatePurchaseBill(PurchaseBillDTO billdto) throws SQLException {
        int billId = Integer.parseInt(billdto.getId().substring(4));

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<BillLineItemDTO> oldItems = billLineItemService.getItemsByBillId(billdto.getId(), conn);
                List<BillLineItemDTO> updatedItems = new ArrayList<>();

                for (BillLineItemDTO lineItem : billdto.getBillLineItems()) {
                    BillLineItemDTO updatedItem = new BillLineItemDTO();

                    //add new item
                    if (lineItem.getId() == null) {
                        lineItem.setBill_id(billdto.getId());
                        updatedItem = billLineItemService.createBillItem(lineItem, conn);
                    } else { // update item
                        updatedItem = billLineItemService.updateBillItem(lineItem, conn);
                    }
                    if (updatedItem != null) updatedItems.add(updatedItem);
                }

                //remove
                for(BillLineItemDTO item : oldItems){
                    if(!updatedItems.contains(item))
                        billLineItemService.deleteBillItemById(item.getId(),conn);
                }


                billdto.setAmount(billLineItemService.getBillAmount(billdto.getId(), conn));

                billdto.setBill_date(TimeUtil.stringToEpoch(billdto.getBill_date())+"");
                billdto.setVendor_id(billdto.getVendor_id().substring(4));
                billdto.setStatus(PurchaseBillStatus.fromString(billdto.getStatus())+"");

                PurchaseBillDTO updated = purchaseBillDAO.updatePurchaseBill(billdto, conn);

                conn.commit();
                return updated;
            } catch (Exception e) {
                conn.rollback();
                System.out.println(e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deletePurchaseBill(String billId) throws SQLException {
        int purchaseBillId = Integer.parseInt(billId.substring(4));
         return purchaseBillDAO.deletePurchaseBill(purchaseBillId,DBConnection.getInstance().getConnection());
    }
}
