package Service;

import Model.SalesInvoice;
import java.sql.SQLException;
import java.util.List;

public interface SalesInvoiceService {
    SalesInvoice createInvoice(SalesInvoice dto) throws SQLException;
    SalesInvoice getInvoiceById(Integer id) throws SQLException;
    List<SalesInvoice> getAllInvoices() throws SQLException;
    SalesInvoice updateInvoice(SalesInvoice dto) throws SQLException;
    boolean deleteInvoice(Integer id) throws SQLException;

}