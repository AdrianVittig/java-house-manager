package org.university.dao.company_dao;

import org.university.dto.CompanyListDto;
import org.university.dto.CompanyWithDetailsDto;
import org.university.entity.Company;

public class CompanyMapper {
    public CompanyListDto toListDto(Company company){
        CompanyListDto companyListDto = new CompanyListDto();
        companyListDto.setId(company.getId());
        companyListDto.setName(company.getName());
        companyListDto.setRevenue(company.getRevenue());
        return companyListDto;
    }

    public CompanyWithDetailsDto toDetailsDto(Company company){
        CompanyWithDetailsDto companyWithDetailsDto = new CompanyWithDetailsDto();
        companyWithDetailsDto.setId(company.getId());
        companyWithDetailsDto.setName(company.getName());
        companyWithDetailsDto.setRevenue(company.getRevenue());

        if(company.getEmployeeList() != null){
            companyWithDetailsDto.setEmployeeIdsList(company.getEmployeeList().stream().map(employee -> employee.getId()).toList());
        }
        return companyWithDetailsDto;
    }
}
