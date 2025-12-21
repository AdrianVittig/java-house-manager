package org.university.service.impl.calculate_fee_service_impl;

import org.university.dao.apartment_dao.ApartmentCrudDao;
import org.university.entity.Apartment;
import org.university.entity.Resident;
import org.university.exception.NotFoundException;
import org.university.service.contract.calculate_fee_service.ApartmentPricingSystemService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ApartmentPricingSystemServiceImpl implements ApartmentPricingSystemService {
    private final ApartmentCrudDao apartmentDao = new ApartmentCrudDao();
    @Override
    public BigDecimal calculateFee(Apartment apartment) throws NotFoundException {


        if(apartment == null || apartment.getId() == null){
            throw new IllegalArgumentException("Apartment cannot be null");
        }

        Apartment managedApartment = apartmentDao.getApartmentWithResidents(apartment.getId());
        if(managedApartment == null){
            throw new NotFoundException("Apartment does not exist");
        }

        BigDecimal baseFee = managedApartment.getArea();
        if(baseFee == null){
            throw new IllegalArgumentException("Apartment area cannot be null");
        }

        int elevatorUsers = 0;
        if(managedApartment.getResidentList() != null){
            for(Resident resident : managedApartment.getResidentList()){
                if(resident == null) continue;
                if(resident.getAge() > 7 && resident.isUsesElevator()){
                    elevatorUsers++;
                }
            }
        }

        BigDecimal fee = baseFee;

        if(elevatorUsers > 0){
            BigDecimal elevatorPercent = new BigDecimal("0.15");
            fee = fee.add(baseFee.multiply(elevatorPercent)
                    .multiply(BigDecimal.valueOf(elevatorUsers)));

        }

        if(managedApartment.isHasPet()){
            BigDecimal petPercent = new BigDecimal("0.075");
            fee = fee.add(baseFee.multiply(petPercent));
        }

        return fee.setScale(2, RoundingMode.HALF_UP);
    }
}
