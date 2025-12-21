package org.university.service.impl.resident_service_impl;

import org.university.dao.resident_dao.ResidentCrudDao;
import org.university.dao.resident_dao.ResidentMapper;
import org.university.dto.ResidentListDto;
import org.university.dto.ResidentWithDetailsDto;
import org.university.entity.Resident;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.service.contract.resident_service.ResidentCrudService;

import java.util.List;

public class ResidentCrudServiceImpl implements ResidentCrudService {
    private final ResidentCrudDao residentDao = new ResidentCrudDao();
    private final ResidentMapper residentMapper = new ResidentMapper();
    @Override
    public void createResident(Resident resident) throws DAOException, NotFoundException {
        if(resident == null){
            throw new IllegalArgumentException("Resident cannot be null");
        }

        if(resident.getApartment() == null || resident.getApartment().getId() == null){
            throw new IllegalArgumentException("Resident apartment cannot be null");
        }

        residentDao.createResident(resident);
    }

    @Override
    public ResidentWithDetailsDto getResidentById(Long id) throws DAOException, NotFoundException {
        Resident resident = residentDao.getResidentWithDetails(id);
        if(resident == null){
            throw new NotFoundException("Resident with id " + id + " does not exist");
        }
        return residentMapper.toDetailsDto(resident);
    }

    @Override
    public List<ResidentListDto> getAllResidents() {
        return residentDao.getAllResidents()
                .stream()
                .map(residentMapper::toListDto)
                .toList();
    }

    @Override
    public void updateResident(Resident resident) throws DAOException, NotFoundException {
        if(resident == null || resident.getId() == null){
            throw new IllegalArgumentException("Resident cannot be null");
        }

        residentDao.updateResident(resident.getId(), resident);
    }

    @Override
    public void deleteResident(Long id) throws DAOException, NotFoundException {
        ResidentWithDetailsDto resident = getResidentById(id);
        if(resident == null){
            throw new NotFoundException("Resident with id " + id + " does not exist");
        }
        residentDao.deleteResident(id);
    }
}
