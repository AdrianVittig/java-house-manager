package org.university.service.impl.company_service_impl;

import org.university.dao.company_dao.CompanyCrudDao;
import org.university.dao.company_dao.CompanyMapper;
import org.university.dto.CompanyListDto;
import org.university.dto.CompanyWithDetailsDto;
import org.university.entity.Company;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.service.contract.company_service.CompanyCrudService;

import java.util.List;

public class CompanyCrudServiceImpl implements CompanyCrudService {
    private final CompanyCrudDao companyDao = new CompanyCrudDao();
    private final CompanyMapper companyMapper = new CompanyMapper();
    @Override
    public void createCompany(Company company) throws DAOException, NotFoundException {
        if(company == null){
            throw new IllegalArgumentException("Company cannot be null");
        }

        if(company.getName() == null || company.getName().isBlank()){
            throw new IllegalArgumentException("Company name cannot be null or empty");
        }

        companyDao.createCompany(company);
    }

    @Override
    public CompanyWithDetailsDto getCompanyById(Long id) throws DAOException, NotFoundException {
        Company company = companyDao.getCompanyWithDetails(id);
        if(company == null){
            throw new NotFoundException("Company with id " + id + " does not exist");
        }
        return companyMapper.toDetailsDto(company);
    }

    @Override
    public List<CompanyListDto> getAllCompanies() {
        return companyDao.getAllCompanies()
                .stream()
                .map(companyMapper::toListDto)
                .toList();
    }

    @Override
    public void updateCompany(Company company) throws DAOException, NotFoundException {
        if(company == null || company.getId() == null){
            throw new IllegalArgumentException("Company cannot be null");
        }

        companyDao.updateCompany(company.getId(), company);
    }

    @Override
    public void deleteCompany(Long id) throws DAOException, NotFoundException {
        CompanyWithDetailsDto company = getCompanyById(id);
        if(company == null){
            throw new NotFoundException("Company with id " + id + " does not exist");
        }
        companyDao.deleteCompany(id);
    }
}
