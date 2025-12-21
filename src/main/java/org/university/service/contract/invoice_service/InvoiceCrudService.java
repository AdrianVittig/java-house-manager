package org.university.service.contract.invoice_service;

import org.university.dto.InvoiceListDto;
import org.university.dto.InvoiceWithDetailsDto;
import org.university.entity.Invoice;

import java.time.YearMonth;
import java.util.List;

public interface InvoiceCrudService {
    void createInvoiceForApartment(Long apartmentId, YearMonth billingMonth);
    void createInvoicesForBuilding(Long buildingId, YearMonth billingMonth);
    InvoiceWithDetailsDto getInvoiceById(Long invoiceId);
    InvoiceWithDetailsDto getInvoiceByApartmentAndMonth(Long apartmentId, YearMonth billingMonth);
    List<InvoiceListDto> getInvoicesByApartment(Long apartmentId);
    List<InvoiceListDto> getInvoicesByBuilding(Long buildingId);
    List<InvoiceListDto> getAllInvoices();
    void updateInvoice(Long invoiceId, Invoice invoice);
    void deleteInvoice(Long invoiceId);

}
