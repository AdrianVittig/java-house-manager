package org.university.service.contract.payment_service;

import org.university.dto.PaymentListDto;
import org.university.dto.PaymentWithDetailsDto;
import org.university.entity.Payment;

import java.time.YearMonth;
import java.util.List;

public interface PaymentCrudService {
    void createPayment(Payment payment);
    PaymentWithDetailsDto getPaymentWithDetailsById(Long paymentId);
    PaymentWithDetailsDto getPaymentWithDetailsByInvoiceId(Long invoiceId);
    List<PaymentListDto> getPaymentsByApartmentId(Long apartmentId);
    List<PaymentListDto> getPaymentsByBuildingId(Long buildingId);
    List<PaymentListDto> getPaymentsByBuildingAndMonth(Long buildingId, YearMonth billingDate);
    List<PaymentListDto> getAllPayments();
    void updatePayment(Long id, Payment payment);
    void deletePayment(Long paymentId);
}
