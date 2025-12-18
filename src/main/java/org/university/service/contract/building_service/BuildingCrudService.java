package org.university.service.contract.building_service;

import org.university.dto.BuildingListDto;
import org.university.dto.BuildingWithDetailsDto;
import org.university.entity.Building;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public interface BuildingCrudService {
    void createBuilding(Building building) throws DAOException, NotFoundException;
    BuildingWithDetailsDto getBuildingById(Long id) throws DAOException, NotFoundException;
    List<BuildingListDto> getAllBuildings();
    void updateBuilding(Building building) throws DAOException, NotFoundException;
    void deleteBuilding(Long id) throws DAOException, NotFoundException;
}
