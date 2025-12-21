package org.university.service.impl.payment_service_impl;

import org.university.dao.payment_dao.PaymentCrudDao;
import org.university.dao.payment_dao.PaymentMapper;
import org.university.dto.PaymentListDto;
import org.university.dto.PaymentWithDetailsDto;
import org.university.entity.Payment;
import org.university.service.contract.payment_service.PaymentCrudService;
import java.time.YearMonth;
import java.util.List;

public class PaymentCrudServiceImpl implements PaymentCrudService {
    private final PaymentCrudDao paymentDao = new PaymentCrudDao();
    private final PaymentMapper paymentMapper = new PaymentMapper();
    @Override
    public void createPayment(Payment payment) {
        if(payment == null){
            throw new IllegalArgumentException("Payment cannot be null");
        }
        if(payment.getInvoice() == null || payment.getInvoice().getId() == null){
            throw new IllegalArgumentException("Invoice cannot be null");
        }

        paymentDao.createPayment(payment);
    }

    @Override
    public PaymentWithDetailsDto getPaymentWithDetailsById(Long paymentId) {
        if(paymentId == null){
            throw new IllegalArgumentException("Payment id cannot be null");
        }
        Payment payment = paymentDao.getPaymentById(paymentId);
        if(payment == null){
            throw new IllegalArgumentException("Payment does not exist");
        }
        return paymentMapper.toDetailsDto(payment);
    }

    @Override
    public PaymentWithDetailsDto getPaymentWithDetailsByInvoiceId(Long invoiceId) {
        if(invoiceId == null){
            throw new IllegalArgumentException("Invoice id cannot be null");
        }
        Payment payment = paymentDao.getPaymentByInvoiceId(invoiceId);
        if(payment == null){
            throw new IllegalArgumentException("Payment does not exist");
        }

        return paymentMapper.toDetailsDto(payment);
    }

    @Override
    public List<PaymentListDto> getPaymentsByApartmentId(Long apartmentId) {
        if(apartmentId == null){
            throw new IllegalArgumentException("Apartment id cannot be null");
        }
        return paymentDao.getPaymentsByApartmentId(apartmentId)
                .stream()
                .map(paymentMapper::toListDto)
                .toList();
    }

    @Override
    public List<PaymentListDto> getPaymentsByBuildingId(Long buildingId) {
        if(buildingId == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }
        return paymentDao.getPaymentsByBuildingId(buildingId)
                .stream()
                .map(paymentMapper::toListDto)
                .toList();
    }

    @Override
    public List<PaymentListDto> getPaymentsByBuildingAndMonth(Long buildingId, YearMonth billingDate) {
       if(buildingId == null){
           throw new IllegalArgumentException("Building id cannot be null");
       }
       if(billingDate == null){
           throw new IllegalArgumentException("Billing date cannot be null");
       }

       return paymentDao.getPaymentsByBuildingAndMonth(buildingId, billingDate)
                   .stream()
                   .map(paymentMapper::toListDto)
                   .toList();
    }

    @Override
    public List<PaymentListDto> getAllPayments() {
        return paymentDao.getAllPayments()
                .stream()
                .map(paymentMapper::toListDto)
                .toList();
    }

    @Override
    public void updatePayment(Long id, Payment payment) {
        if(id == null){
            throw new IllegalArgumentException("Payment id cannot be null");
        }
        if(payment == null || payment.getId() == null){
            throw new IllegalArgumentException("Payment cannot be null");
        }


        paymentDao.updatePayment(id, payment);
    }

    @Override
    public void deletePayment(Long paymentId) {
        if(paymentId == null){
            throw new IllegalArgumentException("Payment id cannot be null");
        }
        Payment payment = paymentDao.getPaymentById(paymentId);
        if(payment == null){
            throw new IllegalArgumentException("Payment does not exist");
        }
        paymentDao.deletePayment(paymentId);
    }
}
