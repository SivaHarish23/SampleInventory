package Service;

import DAO.BillLineItemDAO;
import DTO.BillLineItemDTO;
import Model.BillLineItem;
import Service.BillLineItemService;
import Util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BillLineItemServiceImpl implements BillLineItemService {

    private final BillLineItemDAO billLineItemDAO = new BillLineItemDAO();

    @Override
    public BillLineItemDTO createBillItem(BillLineItemDTO dto, Connection conn) throws SQLException {
        BillLineItem model = new BillLineItem(dto);  // assuming conversion constructor
        return billLineItemDAO.createBillItem(model, conn);

    }

    @Override
    public BillLineItemDTO readBillItemById(String id, Connection conn) throws SQLException {

        return billLineItemDAO.readBillItemById(Integer.parseInt(id), conn);

    }

    public List<BillLineItemDTO> getItemsByBillId(String billId, Connection conn) throws SQLException {
        billId = billId.substring(4);
        return billLineItemDAO.getItemsByBillId(Integer.parseInt(billId), conn);
    }

    @Override
    public List<BillLineItemDTO> readAllBillItems(Connection conn) throws SQLException {

        return billLineItemDAO.readAllBillItems(conn);

    }

    @Override
    public BillLineItemDTO updateBillItem(BillLineItemDTO dto, Connection conn) throws SQLException {
        dto.setId(dto.getId().substring(4));
        dto.setProduct_id(dto.getProduct_id().substring(4));
        return billLineItemDAO.updateBillItem(dto, conn);

    }

    @Override
    public boolean deleteBillItemById(String id, Connection conn) throws SQLException {
        String billId = id.substring(4);
        return billLineItemDAO.deleteBillItem(Integer.parseInt(billId), conn);

    }

    public BigDecimal getBillAmount(String id, Connection conn) throws SQLException {
        String billId = id.substring(4);
        return billLineItemDAO.getBillAmount(Integer.parseInt(billId), conn);
    }

}

