package org.university.dao.resident_dao;

import org.university.dto.ResidentListDto;
import org.university.dto.ResidentWithDetailsDto;
import org.university.entity.Resident;

public class ResidentMapper {
    public ResidentListDto toListDto(Resident resident){
        ResidentListDto residentListDto = new ResidentListDto();
        residentListDto.setId(resident.getId());
        residentListDto.setFirstName(resident.getFirstName());
        residentListDto.setLastName(resident.getLastName());
        residentListDto.setAge(resident.getAge());
        residentListDto.setRole(resident.getRole());
        residentListDto.setUsesElevator(resident.isUsesElevator());
        return residentListDto;
    }

    public ResidentWithDetailsDto toDetailsDto(Resident resident){
        ResidentWithDetailsDto residentWithDetailsDto = new ResidentWithDetailsDto();
        residentWithDetailsDto.setId(resident.getId());
        residentWithDetailsDto.setFirstName(resident.getFirstName());
        residentWithDetailsDto.setLastName(resident.getLastName());
        residentWithDetailsDto.setAge(resident.getAge());
        residentWithDetailsDto.setRole(resident.getRole());
        residentWithDetailsDto.setUsesElevator(resident.isUsesElevator());
        if(resident.getApartment() != null){
            residentWithDetailsDto.setApartmentId(resident.getApartment().getId());
        }
        return residentWithDetailsDto;
    }
}
