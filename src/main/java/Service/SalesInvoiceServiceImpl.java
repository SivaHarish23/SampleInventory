package Service;

import DAO.CustomerDAO;
import DAO.InvoiceLineItemDAO;
import DAO.ProductDAO;
import DAO.SalesInvoiceDAO;
import DTO.InsufficientStockDTO;
import DTO.SalesInvoiceDTO;
import Model.InvoiceLineItem;
import Model.SalesInvoice;
import Model.StockAvailablityReport;
import Util.DBConnection;
import Validators.SalesInvoiceValidator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class SalesInvoiceServiceImpl implements SalesInvoiceService{

    private final SalesInvoiceDAO salesinvoiceDAO = new SalesInvoiceDAO();
    private final InvoiceLineItemServiceImpl invoiceLineItemService = new InvoiceLineItemServiceImpl();
    private final StockService stockService = new StockService();
    private final SalesInvoiceValidator validator = new SalesInvoiceValidator(new CustomerDAO(),new ProductDAO(),new InvoiceLineItemDAO());

    public Map<String, List<String>> validate(SalesInvoiceDTO salesInvoiceDTO, boolean isUpdate) throws SQLException {
        return validator.validate(salesInvoiceDTO , isUpdate);
    }

    public List<InsufficientStockDTO> validateStockAvailability(List<InvoiceLineItem> items) throws SQLException {
        try{
            List<InsufficientStockDTO> list = new ArrayList<>();
            for (InvoiceLineItem item : items) {
                StockAvailablityReport report = stockService.getStockAvailability(item.getProduct_id());
                if (report.getStock_on_hand() < item.getQuantity()) {
                    InsufficientStockDTO isd = new InsufficientStockDTO(
                            "PRO-" + report.getProduct_id(),
                            report.getProduct_name(),
                            item.getQuantity(),
                            report.getStock_on_hand());
                    list.add(isd);
                }
            }
            return list;
        }catch (Exception e){
            System.out.println("SalesInvoiceServiceImpl : validateStockAvailability : " +e);
            throw e;
        }
    }
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

    public boolean isDelivered(Integer invoiceId) throws SQLException {
        SalesInvoice invoice = getInvoiceById(invoiceId);
        return invoice != null && invoice.getStatus() == 1;
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

            // Step 1: Calculate deltas
            Map<Integer, Integer> quantityDeltas = calculateQuantityDeltas(oldItems, invoice.getInvoice_line_items());

            // Step 2: Validate stock for deltas
            List<InsufficientStockDTO> insufficientStock = validateStockWithDeltas(quantityDeltas);
            if (!insufficientStock.isEmpty()) {
                System.out.println(insufficientStock);
                throw new SQLException(insufficientStock+"");
            }
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

    public List<InvoiceLineItem> findDuplicateLineItems(List<InvoiceLineItem> items) {
        Map<Integer, List<InvoiceLineItem>> itemMap = new HashMap<>();
        List<InvoiceLineItem> duplicates = new ArrayList<>();

        for (InvoiceLineItem item : items) {
            int productId = item.getProduct_id();
            itemMap.putIfAbsent(productId, new ArrayList<>());
            itemMap.get(productId).add(item);
        }

        for (Map.Entry<Integer, List<InvoiceLineItem>> entry : itemMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicates.addAll(entry.getValue());
            }
        }

        return duplicates; // Empty list means no duplicates
    }

    public boolean exists(int id) throws SQLException {
        return salesinvoiceDAO.getInvoiceById(id,DBConnection.getInstance().getConnection())!=null;
    }


    private Map<Integer, Integer> calculateQuantityDeltas(List<InvoiceLineItem> oldItems, List<InvoiceLineItem> newItems) {
        Map<Integer, Integer> oldQuantities = new HashMap<>();
        Map<Integer, Integer> newQuantities = new HashMap<>();
        Map<Integer, Integer> deltas = new HashMap<>();

        // Aggregate old quantities by productId
        for (InvoiceLineItem item : oldItems) {
            oldQuantities.put(item.getProduct_id(), oldQuantities.getOrDefault(item.getProduct_id(), 0) + item.getQuantity());
        }

        // Aggregate new quantities by productId
        for (InvoiceLineItem item : newItems) {
            newQuantities.put(item.getProduct_id(), newQuantities.getOrDefault(item.getId(), 0) + item.getQuantity());
        }

        // Calculate delta for each product involved
        Set<Integer> allProductIds = new HashSet<>();
        allProductIds.addAll(oldQuantities.keySet());
        allProductIds.addAll(newQuantities.keySet());

        for (Integer productId : allProductIds) {
            int oldQty = oldQuantities.getOrDefault(productId, 0);
            int newQty = newQuantities.getOrDefault(productId, 0);
            int delta = newQty - oldQty;
            deltas.put(productId, delta);
        }

        return deltas;
    }

    private List<InsufficientStockDTO> validateStockWithDeltas(Map<Integer, Integer> deltas) throws SQLException {
        List<InsufficientStockDTO> insufficientList = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : deltas.entrySet()) {
            int productId = entry.getKey();
            int deltaQty = entry.getValue();

            if (deltaQty > 0) { // only check if stock reduces (delta positive means increase in ordered qty)
                int currentStock = stockService.getStockAvailability(productId).getStock_on_hand();
                if (currentStock < deltaQty) {
                    InsufficientStockDTO dto = new InsufficientStockDTO("PRO-"+productId, null ,currentStock, deltaQty);
                    insufficientList.add(dto);
                }
            }
        }
        return insufficientList;
    }

}
