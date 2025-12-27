package org.university.service.impl.apartment_service_impl;

import org.university.dao.apartment_dao.ApartmentCrudDao;
import org.university.dao.apartment_dao.ApartmentMapper;
import org.university.dao.building_dao.BuildingCrudDao;
import org.university.dto.ApartmentListDto;
import org.university.dto.ApartmentWithDetailsDto;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.service.contract.apartment_service.ApartmentCrudService;

import java.util.List;

public class ApartmentCrudServiceImpl implements ApartmentCrudService {

    private final ApartmentCrudDao apartmentDao = new ApartmentCrudDao();
    private final BuildingCrudDao buildingDao = new BuildingCrudDao();
    private final ApartmentMapper apartmentMapper = new ApartmentMapper();

    @Override
    public void createApartment(Apartment apartment) {
        if (apartment == null) {
            throw new IllegalArgumentException("Apartment cannot be null");
        }

        if (apartment.getBuilding() == null || apartment.getBuilding().getId() == null) {
            throw new IllegalArgumentException("Building cannot be null");
        }

        if (apartment.getArea() == null) {
            throw new IllegalArgumentException("Apartment area cannot be null");
        }

        Long buildingId = apartment.getBuilding().getId();
        Building building = buildingDao.getBuildingById(buildingId);

        if (building == null) {
            throw new NotFoundException("Building with id " + buildingId + " does not exist");
        }

        if (building.getApartmentsPerFloor() <= 0) {
            throw new IllegalArgumentException("Invalid apartments per floor");
        }

        if (building.getCountOfFloors() <= 0) {
            throw new IllegalArgumentException("Invalid floors count");
        }

        long count = apartmentDao.getCountOfApartmentsByBuildingId(buildingId);
        int floor = (int) (count / building.getApartmentsPerFloor()) + 1;

        if (floor > building.getCountOfFloors()) {
            throw new IllegalArgumentException("No free apartments");
        }

        int index = (int) (count % building.getApartmentsPerFloor()) + 1;
        apartment.setNumber("Room: " + (floor * 1000 + index));

        apartment.setBuilding(building);
        apartmentDao.createApartment(apartment);
    }

    @Override
    public ApartmentWithDetailsDto getApartmentById(Long id) {
        Apartment apartment = apartmentDao.getApartmentById(id);
        if (apartment == null) {
            throw new NotFoundException("Apartment not found");
        }
        return apartmentMapper.toDetailsDto(apartment);
    }

    @Override
    public List<ApartmentListDto> getAllApartments() {
        return apartmentDao.getAllApartments()
                .stream()
                .map(apartmentMapper::toListDto)
                .toList();
    }

    @Override
    public void updateApartment(Apartment apartment) {
        if (apartment == null || apartment.getId() == null) {
            throw new IllegalArgumentException("Apartment cannot be null");
        }
        apartmentDao.updateApartment(apartment.getId(), apartment);
    }

    @Override
    public void deleteApartment(Long id) {
        Apartment apartment = apartmentDao.getApartmentById(id);
        if (apartment == null) {
            throw new NotFoundException("Apartment not found");
        }
        apartmentDao.deleteApartment(id);
    }
}
