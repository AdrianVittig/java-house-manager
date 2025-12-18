package org.university.service.contract.company_service;

import org.university.dto.CompanyListDto;
import org.university.dto.CompanyWithDetailsDto;
import org.university.entity.Company;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public interface CompanyCrudService {
    void createCompany(Company company) throws DAOException, NotFoundException;
    CompanyWithDetailsDto getCompanyById(Long id) throws DAOException, NotFoundException;
    List<CompanyListDto> getAllCompanies();
    void updateCompany(Company company) throws DAOException, NotFoundException;
    void deleteCompany(Long id) throws DAOException, NotFoundException;
}
