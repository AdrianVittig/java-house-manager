package org.university.service.contract.apartment_service;

import org.university.dto.ApartmentListDto;
import org.university.dto.ApartmentWithDetailsDto;
import org.university.entity.Apartment;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public interface ApartmentCrudService {
    void createApartment(Apartment apartment) throws DAOException, NotFoundException;
    ApartmentWithDetailsDto getApartmentById(Long id) throws DAOException, NotFoundException;
    List<ApartmentListDto> getAllApartments();
    void updateApartment(Apartment apartment) throws DAOException, NotFoundException;
    void deleteApartment(Long id) throws DAOException, NotFoundException;
}
