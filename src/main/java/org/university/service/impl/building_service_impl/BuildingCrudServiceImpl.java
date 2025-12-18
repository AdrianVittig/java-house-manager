package org.university.service.impl.building_service_impl;

import org.university.dao.apartment_dao.ApartmentCrudDao;
import org.university.dao.building_dao.BuildingCrudDao;
import org.university.dao.building_dao.BuildingMapper;
import org.university.dto.BuildingListDto;
import org.university.dto.BuildingWithDetailsDto;
import org.university.entity.Building;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.service.contract.building_service.BuildingCrudService;

import java.util.List;

public class BuildingCrudServiceImpl implements BuildingCrudService {
    private final BuildingCrudDao buildingDao = new BuildingCrudDao();
    private final ApartmentCrudDao apartmentDao = new ApartmentCrudDao();
    private final BuildingMapper buildingMapper = new BuildingMapper();
    @Override
    public void createBuilding(Building building) throws DAOException, NotFoundException {
        if(building == null){
            throw new IllegalArgumentException("Building cannot be null");
        }

        if(building.getAddress() == null || building.getAddress().isEmpty()){
            throw new IllegalArgumentException("Building address cannot be null or empty");
        }

        if(building.getApartmentsPerFloor() <= 0){
            throw new IllegalArgumentException("Building apartments per floor cannot be less than or equal to 0");
        }

        if(building.getCountOfFloors() <= 0){
            throw new IllegalArgumentException("Building count of floors cannot be less than or equal to 0");
        }

        if(building.getBuiltDate() == null){
            throw new IllegalArgumentException("Building built date cannot be null");
        }

        buildingDao.createBuilding(building);
    }

    @Override
    public BuildingWithDetailsDto getBuildingById(Long id) throws DAOException, NotFoundException {
        Building building = buildingDao.getBuildingById(id);
        if(building == null){
            throw new NotFoundException("Building with id " + id + " does not exist");
        }
        return buildingMapper.toDetailsDto(building);
    }

    @Override
    public List<BuildingListDto> getAllBuildings() {
        return buildingDao.getAllBuildings()
                .stream()
                .map(buildingMapper::toListDto)
                .toList();
    }

    @Override
    public void updateBuilding(Building building) throws DAOException, NotFoundException {
        if(building == null || building.getId() == null){
            throw new IllegalArgumentException("Building cannot be null");
        }
        buildingDao.updateBuilding(building.getId(), building);
    }

    @Override
    public void deleteBuilding(Long id) throws DAOException, NotFoundException {
        BuildingWithDetailsDto building = getBuildingById(id);
        if(building == null){
            throw new NotFoundException("Building with id " + id + " does not exist");
        }

        int apsCount = (int) apartmentDao.getCountOfApartmentsByBuildingId(id);
        if(apsCount > 0){
            throw new IllegalArgumentException("Cannot delete building with id " + id + " because it has apartments");
        }
        buildingDao.deleteBuilding(id);
    }
}
