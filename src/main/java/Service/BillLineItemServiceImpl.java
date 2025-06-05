package Service;

import DAO.BillLineItemDAO;
import Model.BillLineItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BillLineItemServiceImpl implements BillLineItemService {

    private final BillLineItemDAO billLineItemDAO = new BillLineItemDAO();

    @Override
    public BillLineItem createBillItem(BillLineItem model, Connection conn) throws SQLException { // assuming conversion constructor
        return billLineItemDAO.createBillItem(model, conn);

    }

    @Override
    public BillLineItem readBillItemById(Integer id, Connection conn) throws SQLException {
        return billLineItemDAO.readBillItemById(id, conn);

    }

    public List<BillLineItem> getItemsByBillId(Integer billId, Connection conn) throws SQLException {
        return billLineItemDAO.getItemsByBillId(billId, conn);
    }

    @Override
    public List<BillLineItem> readAllBillItems(Connection conn) throws SQLException {
        return billLineItemDAO.readAllBillItems(conn);

    }

    @Override
    public BillLineItem updateBillItem(BillLineItem blm, Connection conn) throws SQLException {
        return billLineItemDAO.updateBillItem(blm, conn);

    }

    @Override
    public boolean deleteBillItem(Integer id, Connection conn) throws SQLException {
        return billLineItemDAO.deleteBillItem(id, conn);

    }

    public BigDecimal getBillAmount(Integer id, Connection conn) throws SQLException {
        return billLineItemDAO.getBillAmount(id, conn);
    }

}

