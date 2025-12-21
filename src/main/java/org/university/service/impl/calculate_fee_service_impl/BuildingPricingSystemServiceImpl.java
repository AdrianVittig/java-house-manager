package org.university.service.impl.calculate_fee_service_impl;

import org.university.dao.building_dao.BuildingCrudDao;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.service.contract.calculate_fee_service.ApartmentPricingSystemService;
import org.university.service.contract.calculate_fee_service.BuildingPricingSystemService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BuildingPricingSystemServiceImpl implements BuildingPricingSystemService {
    private final BuildingCrudDao buildingDao = new BuildingCrudDao();
    private final ApartmentPricingSystemService apartmentPricingSystemService = new ApartmentPricingSystemServiceImpl();
    @Override
    public BigDecimal calculateFeeForBuilding(Building building) {
        if(building == null || building.getId() == null){
            throw new IllegalArgumentException("Building cannot be null");
        }

        Building managed = buildingDao.getBuildingWithApartmentsAndResidents(building.getId());
        if(managed == null){
            throw new IllegalArgumentException("Building with id does not exist");
        }

        if(managed.getApartmentList() == null){
            throw new IllegalArgumentException("Building apartment list cannot be null");
        }

        BigDecimal totalFee = BigDecimal.ZERO;

        for(Apartment apartment : managed.getApartmentList()){
            if(apartment == null) continue;
            BigDecimal apFee = apartmentPricingSystemService.calculateFee(apartment);
            if(apFee != null){
                totalFee = totalFee.add(apFee);
            }
        }

        return totalFee.setScale(2, RoundingMode.HALF_UP);
    }
}
