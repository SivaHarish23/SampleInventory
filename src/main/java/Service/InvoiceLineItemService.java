package Service;

import DTO.InvoiceLineItemDTO;
import Model.InvoiceLineItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// Service: InvoiceLineItemService.java
public interface InvoiceLineItemService {
    InvoiceLineItem createInvoiceItem(InvoiceLineItem dto, Connection conn) throws SQLException;
    InvoiceLineItem readInvoiceItemById(Integer id, Connection conn) throws SQLException;
    List<InvoiceLineItem> readAllInvoiceItems(Connection conn) throws SQLException;
    InvoiceLineItem updateInvoiceItem(InvoiceLineItem dto, Connection conn) throws SQLException;
    boolean deleteInvoiceItem(Integer id,Connection conn) throws SQLException;
}
