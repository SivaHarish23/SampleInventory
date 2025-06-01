package Service;

import DTO.SalesInvoiceDTO;

import java.util.List;

public interface SalesInvoiceService {
    SalesInvoiceDTO createInvoice(SalesInvoiceDTO dto);
    SalesInvoiceDTO getInvoiceById(String id);
    List<SalesInvoiceDTO> getAllBills();
    SalesInvoiceDTO updateInvoice(String id, SalesInvoiceDTO dto);
    void deleteInvoice(String id);

}