package Service;

import DTO.InvoiceLineItemDTO;

import java.sql.Connection;
import java.util.List;

// Service: InvoiceLineItemService.java
public interface InvoiceLineItemService {
    InvoiceLineItemDTO createInvoiceLineItem(InvoiceLineItemDTO dto, Connection conn);
    InvoiceLineItemDTO readInvoiceLineItemById(String id, Connection conn);
    List<InvoiceLineItemDTO> readAllInvoiceLineItem(Connection conn);
    InvoiceLineItemDTO updateInvoiceLineItem(String id, InvoiceLineItemDTO dto, Connection conn);
    Boolean deleteInvoiceLineItem(String id,Connection conn);
}
