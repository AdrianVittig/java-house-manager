package org.university.dao.building_dao;

import org.university.dto.BuildingListDto;
import org.university.dto.BuildingsWithDetailsDto;
import org.university.entity.Building;

public class BuildingMapper {
    public BuildingListDto toListDto(Building building){
        BuildingListDto buildingListDto = new BuildingListDto();
        buildingListDto.setId(building.getId());
        buildingListDto.setName(building.getName());
        buildingListDto.setCountOfFloors(building.getCountOfFloors());
        buildingListDto.setApartmentsPerFloor(building.getApartmentsPerFloor());
        buildingListDto.setBuiltDate(building.getBuiltDate());
        return buildingListDto;
    }

    public BuildingsWithDetailsDto toDetailsDto(Building building){
        BuildingsWithDetailsDto buildingsWithDetailsDto = new BuildingsWithDetailsDto();
        buildingsWithDetailsDto.setId(building.getId());
        buildingsWithDetailsDto.setName(building.getName());
        buildingsWithDetailsDto.setCountOfFloors(building.getCountOfFloors());
        buildingsWithDetailsDto.setApartmentsPerFloor(building.getApartmentsPerFloor());
        buildingsWithDetailsDto.setBuiltDate(building.getBuiltDate());

        if(building.getEmployee() != null){
            buildingsWithDetailsDto.setEmployeeId(building.getEmployee().getId());
        }

        if(building.getApartmentList() != null){
            buildingsWithDetailsDto.setApartmentIdsList(building.getApartmentList().stream().map(apartment -> apartment.getId()).toList());
        }

        if(building.getContract() != null){
            buildingsWithDetailsDto.setContractId(building.getContract().getId());
        }
        return buildingsWithDetailsDto;
    }
}
