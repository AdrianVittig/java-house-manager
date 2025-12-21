package org.university.dao.invoice_dao;

import org.university.dto.EmployeeWithDetailsDto;
import org.university.dto.InvoiceListDto;
import org.university.dto.InvoiceWithDetailsDto;
import org.university.entity.BaseEntity;
import org.university.entity.Contract;
import org.university.entity.Employee;
import org.university.entity.Invoice;

public class InvoiceMapper {
    public InvoiceListDto toListDto(Invoice invoice){
        InvoiceListDto invoiceListDto = new InvoiceListDto();
        invoiceListDto.setId(invoice.getId());
        invoiceListDto.setTotalAmount(invoice.getTotalAmount());
        invoiceListDto.setPaymentStatus(invoice.getPaymentStatus());
        invoiceListDto.setBillingMonth(invoice.getBillingMonth());
        invoiceListDto.setDueDate(invoice.getDueDate());
        return invoiceListDto;
    }

    public InvoiceWithDetailsDto toDetailsDto(Invoice invoice){
        InvoiceWithDetailsDto invoiceWithDetailsDto = new InvoiceWithDetailsDto();
        invoiceWithDetailsDto.setId(invoice.getId());

        invoiceWithDetailsDto.setTotalAmount(invoice.getTotalAmount());
        invoiceWithDetailsDto.setPaymentStatus(invoice.getPaymentStatus());
        invoiceWithDetailsDto.setBillingMonth(invoice.getBillingMonth());
        invoiceWithDetailsDto.setDueDate(invoice.getDueDate());

        if(invoice.getApartment() != null){
            invoiceWithDetailsDto.setApartmentId(invoice.getApartment().getId());
        }
        if(invoice.getPayment() != null){
            invoiceWithDetailsDto.setPaymentId(invoice.getPayment().getId());
        }
        return invoiceWithDetailsDto;
    }
}
