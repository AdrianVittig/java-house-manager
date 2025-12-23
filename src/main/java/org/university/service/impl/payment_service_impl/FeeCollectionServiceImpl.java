package org.university.service.impl.payment_service_impl;

import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.dao.payment_dao.PaymentCrudDao;
import org.university.dao.payment_dao.PaymentMapper;
import org.university.dto.PaymentWithDetailsDto;
import org.university.entity.Invoice;
import org.university.entity.Payment;
import org.university.service.contract.payment_service.FeeCollectionService;
import org.university.service.impl.invoice_service_impl.InvoiceCrudServiceImpl;
import org.university.util.PaymentStatus;

import java.time.YearMonth;
import java.util.List;

public class FeeCollectionServiceImpl implements FeeCollectionService {
    private final PaymentCrudDao paymentDao = new PaymentCrudDao();
    private final InvoiceCrudDao invoiceDao = new InvoiceCrudDao();
    private final PaymentMapper paymentMapper = new PaymentMapper();
    private final InvoiceCrudServiceImpl invoiceCrudService = new InvoiceCrudServiceImpl();

    @Override
    public PaymentWithDetailsDto payInvoice(Long invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice id cannot be null");
        }

        Payment payment = new Payment();
        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        payment.setInvoice(invoice);
        paymentDao.createPayment(payment);

        return paymentMapper.toDetailsDto(paymentDao.getPaymentByInvoiceId(invoiceId));



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
