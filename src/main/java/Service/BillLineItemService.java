package Service;

import DTO.BillLineItemDTO;
import Model.BillLineItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BillLineItemService {
    BillLineItem createBillItem(BillLineItem dto, Connection conn) throws SQLException;
    BillLineItem readBillItemById(Integer id, Connection conn) throws SQLException;
    List<BillLineItem> readAllBillItems(Connection conn) throws SQLException;
    BillLineItem updateBillItem(BillLineItem dto, Connection conn) throws SQLException;
    boolean deleteBillItem(Integer id, Connection conn) throws SQLException;
}
