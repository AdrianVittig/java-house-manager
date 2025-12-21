package org.university.dao.payment_dao;

import org.university.dto.InvoiceListDto;
import org.university.dto.PaymentListDto;
import org.university.dto.PaymentWithDetailsDto;
import org.university.entity.Invoice;
import org.university.entity.Payment;

public class PaymentMapper {
    public PaymentListDto toListDto(Payment payment){
        PaymentListDto paymentListDto = new PaymentListDto();
        paymentListDto.setId(payment.getId());
        paymentListDto.setAmount(payment.getAmount());
        paymentListDto.setPaymentStatus(payment.getPaymentStatus());
        paymentListDto.setPaidAt(payment.getPaidAt());
        return paymentListDto;
    }

    public PaymentWithDetailsDto toDetailsDto(Payment payment){
        PaymentWithDetailsDto paymentWithDetailsDto = new PaymentWithDetailsDto();
        paymentWithDetailsDto.setId(payment.getId());
        paymentWithDetailsDto.setAmount(payment.getAmount());
        paymentWithDetailsDto.setPaymentStatus(payment.getPaymentStatus());
        paymentWithDetailsDto.setPaidAt(payment.getPaidAt());

        if(payment.getInvoice() != null){
            paymentWithDetailsDto.setInvoiceId(payment.getInvoice().getId());
        }
        return paymentWithDetailsDto;
    }


}
