package org.university.dao.building_dao;

import org.university.dto.BuildingListDto;
import org.university.dto.BuildingWithDetailsDto;
import org.university.entity.Building;

public class BuildingMapper {
    public BuildingListDto toListDto(Building building){
        BuildingListDto buildingListDto = new BuildingListDto();
        buildingListDto.setId(building.getId());
        buildingListDto.setName(building.getName());
        buildingListDto.setAddress(building.getAddress());
        buildingListDto.setBuiltUpArea(building.getBuiltUpArea());
        buildingListDto.setCommonAreasPercentageOfBuiltUpArea(building.getCommonAreasPercentageOfBuiltUpArea());
        buildingListDto.setCountOfFloors(building.getCountOfFloors());
        buildingListDto.setApartmentsPerFloor(building.getApartmentsPerFloor());
        buildingListDto.setBuiltDate(building.getBuiltDate());
        return buildingListDto;
    }

    public BuildingWithDetailsDto toDetailsDto(Building building){
        BuildingWithDetailsDto buildingWithDetailsDto = new BuildingWithDetailsDto();
        buildingWithDetailsDto.setId(building.getId());
        buildingWithDetailsDto.setName(building.getName());
        buildingWithDetailsDto.setAddress(building.getAddress());
        buildingWithDetailsDto.setBuiltUpArea(building.getBuiltUpArea());
        buildingWithDetailsDto.setCommonAreasPercentageOfBuiltUpArea(building.getCommonAreasPercentageOfBuiltUpArea());
        buildingWithDetailsDto.setCountOfFloors(building.getCountOfFloors());
        buildingWithDetailsDto.setApartmentsPerFloor(building.getApartmentsPerFloor());
        buildingWithDetailsDto.setBuiltDate(building.getBuiltDate());

        if(building.getEmployee() != null){
            buildingWithDetailsDto.setEmployeeId(building.getEmployee().getId());
        }

        if(building.getApartmentList() != null){
            buildingWithDetailsDto.setApartmentIdsList(building.getApartmentList().stream().map(apartment -> apartment.getId()).toList());
        }

        if(building.getContract() != null){
            buildingWithDetailsDto.setContractId(building.getContract().getId());
        }
        return buildingWithDetailsDto;
    }
}
