package Service;

import DTO.BillLineItemDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BillLineItemService {
    BillLineItemDTO createBillItem(BillLineItemDTO dto, Connection conn) throws SQLException;
    BillLineItemDTO readBillItemById(String id, Connection conn) throws SQLException;
    List<BillLineItemDTO> readAllBillItems(Connection conn) throws SQLException;
    BillLineItemDTO updateBillItem(BillLineItemDTO dto, Connection conn) throws SQLException;
    boolean deleteBillItemById(String id, Connection conn) throws SQLException;
}
