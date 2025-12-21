package org.university.service.contract.payment_service;

import org.university.dto.PaymentWithDetailsDto;

import java.time.YearMonth;

public interface FeeCollectionService {
    PaymentWithDetailsDto payInvoice(Long invoiceId);
    void collectFeesForBuilding(long BuildingId, YearMonth billingMonth);
}
