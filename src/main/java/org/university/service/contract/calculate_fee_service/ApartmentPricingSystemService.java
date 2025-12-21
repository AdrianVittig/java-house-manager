package org.university.service.contract.calculate_fee_service;

import org.university.entity.Apartment;
import org.university.exception.NotFoundException;

import java.math.BigDecimal;

public interface ApartmentPricingSystemService {
    BigDecimal calculateFee(Apartment apartment) throws NotFoundException;
}
