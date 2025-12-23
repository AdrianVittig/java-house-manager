package org.university.service.impl.payment_service_impl;

import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.dao.payment_dao.PaymentCrudDao;
import org.university.dao.payment_dao.PaymentMapper;
import org.university.dto.FileDto;
import org.university.dto.PaymentWithDetailsDto;
import org.university.entity.Invoice;
import org.university.entity.Payment;
import org.university.service.contract.file_manage_service.FileService;
import org.university.service.contract.payment_service.FeeCollectionService;
import org.university.service.impl.file_manage_service_impl.FileServiceImpl;
import org.university.service.impl.invoice_service_impl.InvoiceCrudServiceImpl;
import org.university.util.PaymentStatus;

import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

public class FeeCollectionServiceImpl implements FeeCollectionService {
    private final PaymentCrudDao paymentDao = new PaymentCrudDao();
    private final InvoiceCrudDao invoiceDao = new InvoiceCrudDao();
    private final PaymentMapper paymentMapper = new PaymentMapper();
    private final InvoiceCrudServiceImpl invoiceCrudService = new InvoiceCrudServiceImpl();
    private final FileService fileService = new FileServiceImpl();
    @Override
    public PaymentWithDetailsDto payInvoice(Long invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice id cannot be null");
        }

        Payment paymentToCreate = new Payment();
        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        paymentToCreate.setInvoice(invoice);

        paymentDao.createPayment(paymentToCreate);

        Payment p = paymentDao.getPaymentByInvoiceId(invoiceId);
        if(p == null){
            throw new IllegalArgumentException("Payment cannot be null");
        }

        FileDto dto = new FileDto(
                p.getInvoice().getId(),
                p.getInvoice().getBillingMonth(),
                p.getInvoice().getApartment().getBuilding().getId(),
                p.getInvoice().getApartment().getBuilding().getEmployee().getCompany().getName(),
                p.getInvoice().getApartment().getBuilding().getEmployee().getFirstName(),
                p.getInvoice().getApartment().getBuilding().getEmployee().getLastName(),
                p.getInvoice().getApartment().getBuilding().getName(),
                p.getInvoice().getApartment().getBuilding().getAddress(),
                p.getInvoice().getApartment().getNumber(),
                p.getAmount(),
                p.getPaidAt()
        );

        fileService.saveToFile(dto);

        return paymentMapper.toDetailsDto(p);

    }

    @Override
    public void collectFeesForBuilding(long buildingId, YearMonth billingMonth) {
        if(billingMonth == null){
            throw new IllegalArgumentException("Billing month cannot be null");
        }

        invoiceCrudService.createInvoicesForBuilding(buildingId, billingMonth);

        List<Invoice> invoiceList = invoiceDao.getInvoicesByBuildingAndMonth(buildingId, billingMonth);

        for(Invoice invoice : invoiceList){
            if(invoice == null || invoice.getId() == null){
                continue;
            }

            if(invoice.getPaymentStatus() != PaymentStatus.PAID){
                payInvoice(invoice.getId());
            }
        }
    }
}
