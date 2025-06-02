package Service;

import DAO.SalesInvoiceDAO;
import Model.InvoiceLineItem;
import Model.SalesInvoice;
import Util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SalesInvoiceServiceImpl implements SalesInvoiceService{

    private final SalesInvoiceDAO salesinvoiceDAO = new SalesInvoiceDAO();
    private final InvoiceLineItemServiceImpl invoiceLineItemService = new InvoiceLineItemServiceImpl();

    @Override
    public SalesInvoice createInvoice(SalesInvoice invoice) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            BigDecimal amount = BigDecimal.valueOf(0);
            Integer invoiceId = null;
            SalesInvoice createdInvoice = new SalesInvoice();
            List<InvoiceLineItem> createdItems = new ArrayList<>();
            try {
                invoice.setAmount(amount);
                SalesInvoice insertInvoice = salesinvoiceDAO.createInvoice(invoice, conn);

                if (insertInvoice == null) {
                    System.out.println("Error, empty createdInvoice");
                    conn.rollback();
                    System.out.println("ERROR in creating sales invoice , sales invoice not created");
                    return null;
                }
                invoiceId = insertInvoice.getId();
            }catch (SQLException e){
                System.out.println("SalesInvoiceServiceImpl : createSalesInvoice : Generating key part : " +e);
                conn.rollback();
                throw e;
            }
            try{
                List<InvoiceLineItem> invoiceItems = invoice.getInvoice_line_items();
                if (invoiceItems != null) {
                    for(InvoiceLineItem invoiceLineItem : invoiceItems){
                        invoiceLineItem.setInvoice_id(invoiceId);
                        InvoiceLineItem createdItem = invoiceLineItemService.createInvoiceItem(invoiceLineItem,conn);
                        if(createdItem == null){
                            System.out.println("SalesInvoiceServiceImpl : createSalesInvoice : inserting invoice item : " );
                            conn.rollback(); return null;
                        }
                        createdItems.add(createdItem);
                    }
                }
            }catch (SQLException e){
                System.out.println("SalesInvoiceServiceImpl : createSalesInvoice : inserting invoice item : " +e);
                conn.rollback();
                throw e;
            }
            try{
                amount = invoiceLineItemService.getInvoiceAmount(invoiceId,conn);
            }catch (SQLException e){
                System.out.println("SalesInvoiceServiceImpl : createSalesInvoice : fetching amount : " +e);
                conn.rollback();
                throw e;
            }
            try{
                SalesInvoice m = new SalesInvoice.Builder().setId(invoiceId).setAmount(amount).build();
                createdInvoice = salesinvoiceDAO.updateSalesInvoice(m , conn);
                createdInvoice.setInvoice_line_items(createdItems);
            }catch (SQLException e){
                System.out.println("SalesInvoiceServiceImpl : createSalesInvoice : updating amount : " +e);
                conn.rollback();
                throw e;
            }
            conn.commit();
            return createdInvoice;
        }catch (Exception e){
            System.out.println("SalesInvoiceServiceImpl : createSalesInvoice : overall exception : " +e);
            throw e;
        }
    }

    @Override
    public SalesInvoice getInvoiceById(Integer id) throws SQLException {
        try(Connection conn = DBConnection.getInstance().getConnection()){
            SalesInvoice salesinvoice = salesinvoiceDAO.getInvoiceById(id, conn);
            if(salesinvoice != null){
                salesinvoice.setInvoice_line_items(invoiceLineItemService.getItemsByInvoiceId(id,conn));
            }
            return salesinvoice;
        }catch (SQLException e){
            System.out.println("SalesInvoiceServiceImpl : getSalesInvoiceById " +e);
            throw e;
        }
    }

    @Override
    public List<SalesInvoice> getAllInvoices() throws SQLException {
        List<SalesInvoice> invoices = new ArrayList<>();
        try(Connection conn = DBConnection.getInstance().getConnection()){
            invoices = salesinvoiceDAO.getAllInvoices(conn);
        }catch (SQLException e){
            System.out.println("SalesInvoiceServiceImpl : while getting sales invoices " +e);
            throw e;
        }
        if(invoices != null){
            try(Connection conn = DBConnection.getInstance().getConnection()){
                for(SalesInvoice pb : invoices){
                    List<InvoiceLineItem> invoiceItems = new ArrayList<>();
                    invoiceItems = invoiceLineItemService.getItemsByInvoiceId(pb.getId(),conn);
                    pb.setInvoice_line_items(invoiceItems);
                }
            }catch (SQLException e){
                System.out.println("SalesInvoiceServiceImpl : while invoice line items " +e);
                throw e;
            }
        }
        return invoices;
    }

    @Override
    public SalesInvoice updateInvoice(SalesInvoice invoice) throws SQLException {
        Integer invoiceId = invoice.getId();
        List<InvoiceLineItem> oldItems = new ArrayList<>();
        List<InvoiceLineItem> updatedItems = new ArrayList<>();
        try(Connection conn = DBConnection.getInstance().getConnection()){
            oldItems = invoiceLineItemService.getItemsByInvoiceId(invoiceId,conn);
        }catch (SQLException e){
            System.out.println("SalesInvoiceServiceImpl: updateSalesInvoice : getting old items " +e);
            throw e;
        }
        try(Connection conn = DBConnection.getInstance().getConnection()){
            conn.setAutoCommit(false);
            for(InvoiceLineItem item : invoice.getInvoice_line_items()){
                InvoiceLineItem updatedItem = (item.getId() == null) ? invoiceLineItemService.createInvoiceItem(item,conn) : invoiceLineItemService.updateInvoiceItem(item,conn);
                if(updatedItem != null) updatedItems.add(updatedItem);
            }

            Set<Integer> updatedIds = updatedItems.stream()
                    .map(InvoiceLineItem::getId)
                    .collect(Collectors.toSet());

            for (InvoiceLineItem oldItem : oldItems) {
                if (oldItem.getId() != null && !updatedIds.contains(oldItem.getId())) {
                    invoiceLineItemService.deleteInvoiceItem(oldItem.getId(), conn);
                }
            }

            invoice.setAmount(invoiceLineItemService.getInvoiceAmount(invoiceId,conn));

            SalesInvoice updatedInvoice = salesinvoiceDAO.updateSalesInvoice(invoice,conn);
            updatedInvoice.setInvoice_line_items(invoiceLineItemService.getItemsByInvoiceId(invoiceId,conn));
            conn.commit();
            return updatedInvoice;
        }catch (SQLException e){
            System.out.println("SalesInvoiceServiceImpl: updateSalesInvoice : getting old items " +e);
            throw e;
        }
    }

    @Override
    public boolean deleteInvoice(Integer id) throws SQLException {
        return salesinvoiceDAO.deleteSalesInvoice(id,DBConnection.getInstance().getConnection());
    }
}
