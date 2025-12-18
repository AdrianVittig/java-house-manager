package org.university.service.contract.building_service;

import org.university.dto.BuildingWithDetailsDto;

import java.math.BigDecimal;

public interface CalculateFeeService {
    BuildingWithDetailsDto getBuildingWithDetails(Long buildingId);
    BigDecimal calculateFee(Long buildingId);
}
