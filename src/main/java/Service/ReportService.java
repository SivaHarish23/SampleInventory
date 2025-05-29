package Service;

import DAO.ReportDAO;
import DTO.ProductTranscationDTO;
import DTO.StockReportDTO;
import DTO.pendingProductsReportDTO;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ReportService {
    private final ReportDAO reportDAO = new ReportDAO();

    public List<StockReportDTO> getStockAvailability() throws SQLException {
        return reportDAO.getStockAvailability();
    }

    public BigDecimal getOverallStockValue() throws SQLException {
        return reportDAO.getOverallStockValue();
    }

    public List<pendingProductsReportDTO> getProductsToReceive() throws SQLException {
        return reportDAO.getProductsToReceive();
    }

    public List<pendingProductsReportDTO> getProductsToDeliver() throws SQLException {
        return reportDAO.getProductsToDeliver();
    }


    public List<ProductTranscationDTO> getProductsReceivedBetween(Date from, Date to) throws SQLException {
        return reportDAO.getProductsReceivedBetweenDates(from, to);
    }

    public List<ProductTranscationDTO> getProductsDeliveredBetween(Date from, Date to) throws SQLException {
        return reportDAO.getProductsDeliveredBetweenDates(from, to);
    }
}
