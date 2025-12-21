package org.university.service.contract.resident_service;

import org.university.dto.ResidentListDto;
import org.university.dto.ResidentWithDetailsDto;
import org.university.entity.Resident;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public interface ResidentCrudService {
    void createResident(Resident resident) throws DAOException, NotFoundException;
    ResidentWithDetailsDto getResidentById(Long id) throws DAOException, NotFoundException;
    List<ResidentListDto> getAllResidents();
    void updateResident(Resident resident) throws DAOException, NotFoundException;
    void deleteResident(Long id) throws DAOException, NotFoundException;
}
