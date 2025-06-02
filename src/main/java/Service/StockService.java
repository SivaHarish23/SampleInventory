package Service;

import DAO.ProductDAO;
import DAO.PurchaseBillDAO;
import DAO.SalesInvoiceDAO;
import DAO.StockReportDAO;
import DTO.PurchaseEntryDTO;
import DTO.StockValueDTO;
import Model.Product;
import Model.StockAvailablityReport;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockService {

    private final ProductDAO productDAO = new ProductDAO();

    private final StockReportDAO stockReportDAO = new StockReportDAO();
    private final PurchaseBillDAO purchaseBillDAO = new PurchaseBillDAO();
    private final SalesInvoiceDAO salesInvoiceDAO = new SalesInvoiceDAO();

    public List<StockAvailablityReport> getOverallStockAvailability () throws SQLException{
        return stockReportDAO.getOverallStockAvailablity();
    }

    public StockAvailablityReport getStockAvailability (Integer pid) throws SQLException{
        return stockReportDAO.getStockAvailablity(pid);
    }

    public BigDecimal calculateStockValueFIFO(int productId) throws SQLException {
        List<PurchaseEntryDTO> purchases = purchaseBillDAO.getPurchaseEntryById(productId); // sorted by date asc
        int quantitySold = salesInvoiceDAO.totalQuantitySold(productId);

        BigDecimal stockValue = BigDecimal.ZERO;

        for (PurchaseEntryDTO entry : purchases) {
            if (quantitySold >= entry.getQuantity()) {
                quantitySold -= entry.getQuantity();
            } else {
                int remainingQty = entry.getQuantity() - quantitySold;
                stockValue = stockValue.add(entry.getRate().multiply(BigDecimal.valueOf(remainingQty)));
                break;
            }
        }
        return stockValue;
    }

    public StockValueDTO getStockValue(int pid) throws SQLException, NullPointerException {
        return new StockValueDTO(pid, productDAO.findProduct(pid).getName(), calculateStockValueFIFO(pid));
    }

    public List<StockValueDTO> getOverallStockValue() throws SQLException {
        List<Product> products = productDAO.getAllRows();

        List<StockValueDTO> result = new ArrayList<>();
        if(products!=null){
            for(Product p : products) {
                StockValueDTO stock = getStockValue(p.getId());
                if(p.getOpening_stock() > 0)
                    stock.setStock_value(
                            stock.getStock_value().add(
                                    p.getCost_price().multiply(BigDecimal.valueOf(p.getOpening_stock()))));
                result.add(stock);
            }
        }else{
            throw new NullPointerException("No products!!!!");
        }
        return result;
    }

}
