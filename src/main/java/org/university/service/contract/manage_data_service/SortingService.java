package org.university.service.contract.manage_data_service;

import org.university.dto.CompanyRevenueDto;
import org.university.dto.EmployeeBuildingsCountDto;
import org.university.entity.Employee;
import org.university.entity.Resident;

import java.util.List;

public interface SortingService {
    List<CompanyRevenueDto> sortCompaniesByCollectedFeesDesc();
    List<Employee> sortEmployeesByCompanyByName(Long companyId);
    List<EmployeeBuildingsCountDto> sortEmployeesByCompanyByBuildingsCountDesc(Long companyId);
    List<Resident> sortResidentsByBuildingByName(Long buildingId);
    List<Resident> sortResidentsByBuildingByAge(Long buildingId);
}
