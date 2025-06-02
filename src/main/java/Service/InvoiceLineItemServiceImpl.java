package Service;

import DAO.InvoiceLineItemDAO;
import Model.InvoiceLineItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class InvoiceLineItemServiceImpl implements InvoiceLineItemService {
    private final InvoiceLineItemDAO invoiceLineItemDAO = new InvoiceLineItemDAO();

    @Override
    public InvoiceLineItem createInvoiceItem(InvoiceLineItem model, Connection conn) throws SQLException { // assuming conversion constructor
        return invoiceLineItemDAO.createInvoiceItem(model, conn);

    }

    @Override
    public InvoiceLineItem readInvoiceItemById(Integer id, Connection conn) throws SQLException {
        return invoiceLineItemDAO.readInvoiceItemById(id, conn);

    }

    public List<InvoiceLineItem> getItemsByInvoiceId(Integer invoiceId, Connection conn) throws SQLException {
        return invoiceLineItemDAO.getItemsByInvoiceId(invoiceId, conn);
    }

    @Override
    public List<InvoiceLineItem> readAllInvoiceItems(Connection conn) throws SQLException {
        return invoiceLineItemDAO.readAllInvoiceItems(conn);

    }

    @Override
    public InvoiceLineItem updateInvoiceItem(InvoiceLineItem blm, Connection conn) throws SQLException {
        return invoiceLineItemDAO.updateInvoiceItem(blm, conn);

    }

    @Override
    public boolean deleteInvoiceItem(Integer id, Connection conn) throws SQLException {
        return invoiceLineItemDAO.deleteInvoiceItem(id, conn);
    }

    public BigDecimal getInvoiceAmount(Integer id, Connection conn) throws SQLException {
        return invoiceLineItemDAO.getInvoiceAmount(id, conn);
    }

   
}
