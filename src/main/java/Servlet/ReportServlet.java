package Servlet;

import DTO.ProductTranscationDTO;
import DTO.StockReportDTO;
import DTO.pendingProductsReportDTO;
import Service.ReportService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;


//stock
//        stockValue
//toReceive
//        toDeliver
//receiveBetweenDates
//        deliverBetweenDates

@WebServlet("/reports/*")
public class ReportServlet extends HttpServlet {
    private final ReportService reportService  = new ReportService();
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();  // e.g., /stock, /received
        Gson gson = new Gson();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            switch (path) {
                case "/stock" :
                    List<StockReportDTO> data = reportService.getStockAvailability();
                    resp.getWriter().write(gson.toJson(data));
                    break;

                case "/stock/value" :
                    BigDecimal value = reportService.getOverallStockValue();
                    resp.getWriter().write("{\"OverallStockValue\":" + value + "}");
                    break;
                case "/to-receive" :
                    List<pendingProductsReportDTO> toReceive = reportService.getProductsToReceive();
                    resp.getWriter().write(gson.toJson(toReceive));
                    break;
                case "/to-deliver" :
                    List<pendingProductsReportDTO> toDeliver= reportService.getProductsToDeliver();
                    resp.getWriter().write(gson.toJson(toDeliver));
                    break;
                case "/received" :
                    Date from = Date.valueOf(req.getParameter("from"));
                    Date to = Date.valueOf(req.getParameter("to"));
                    List<ProductTranscationDTO> receivedBetween = reportService.getProductsReceivedBetween(from, to);
                    resp.getWriter().write(gson.toJson(receivedBetween));
                    break;
                case "/delivered" :
                    Date deliverdFrom = Date.valueOf(req.getParameter("from"));
                    Date deliverdTto = Date.valueOf(req.getParameter("to"));
                    List<ProductTranscationDTO> deliveredBetween = reportService.getProductsDeliveredBetween(deliverdFrom, deliverdTto);
                    resp.getWriter().write(gson.toJson(deliveredBetween));
                    break;
                default :
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Invalid report endpoint\"}");

            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
