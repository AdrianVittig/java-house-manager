package org.university.dao.apartment_dao;

import org.university.dto.ApartmentListDto;
import org.university.dto.ApartmentWithDetailsDto;
import org.university.entity.Apartment;
import org.university.entity.Resident;

public class ApartmentMapper {

    public ApartmentListDto toListDto(Apartment apartment){
        ApartmentListDto apartmentListDto = new ApartmentListDto();
        apartmentListDto.setId(apartment.getId());
        apartmentListDto.setNumber(apartment.getNumber());
        apartmentListDto.setArea(apartment.getArea());
        apartmentListDto.setHasPet(apartment.isHasPet());
        return apartmentListDto;
    }

    public ApartmentWithDetailsDto toDetailsDto(Apartment apartment){
        ApartmentWithDetailsDto apartmentWithDetailsDto = new ApartmentWithDetailsDto();
        apartmentWithDetailsDto.setId(apartment.getId());
        apartmentWithDetailsDto.setNumber(apartment.getNumber());
        apartmentWithDetailsDto.setArea(apartment.getArea());
        apartmentWithDetailsDto.setHasPet(apartment.isHasPet());
        if(apartment.getBuilding() != null){
            apartmentWithDetailsDto.setBuildingId(apartment.getBuilding().getId());
        }

        if(apartment.getResidentList() != null){
            apartmentWithDetailsDto.setResidentIdsList(apartment.getResidentList().stream().map(Resident::getId).toList());
        }

        return apartmentWithDetailsDto;
    }
}
