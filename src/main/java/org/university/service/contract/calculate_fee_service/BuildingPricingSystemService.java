package org.university.service.contract.calculate_fee_service;

import org.university.entity.Building;

import java.math.BigDecimal;

public interface BuildingPricingSystemService {
    BigDecimal calculateFeeForBuilding(Building building);
}
