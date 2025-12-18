package org.university.service.impl.building_service_impl;

import org.university.dao.apartment_dao.ApartmentCrudDao;
import org.university.dao.building_dao.BuildingCrudDao;
import org.university.dao.building_dao.BuildingMapper;
import org.university.dto.BuildingWithDetailsDto;
import org.university.entity.Apartment;
import org.university.service.contract.building_service.BuildingCrudService;
import org.university.service.contract.building_service.CalculateFeeService;

import java.math.BigDecimal;

public class CalculateFeeServiceImpl implements CalculateFeeService {
    private final BuildingCrudService buildingCrudService = new BuildingCrudServiceImpl();
    private final BuildingCrudDao buildingDao = new BuildingCrudDao();
    private final BuildingMapper buildingMapper = new BuildingMapper();
    private final ApartmentCrudDao apartmentDao = new ApartmentCrudDao();
    @Override
    public BuildingWithDetailsDto getBuildingWithDetails(Long buildingId) {
        return buildingCrudService.getBuildingById(buildingId);
    }

    @Override
    public BigDecimal calculateFee(Long buildingId) {
        BuildingWithDetailsDto building = getBuildingWithDetails(buildingId);
        int apsCount = building.getApartmentIdsList().size();
        return BigDecimal.ZERO;
    }
}
