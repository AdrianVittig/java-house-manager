package org.university.service.contract.manage_data_service;

import org.university.dto.CompanyRevenueDto;
import org.university.dto.EmployeeBuildingsCountDto;
import org.university.entity.Employee;
import org.university.entity.Resident;

import java.math.BigDecimal;
import java.util.List;

public interface FilteringService {
    List<CompanyRevenueDto> filterCompaniesByMinCollectedFees(BigDecimal minCollectedFees);

    List<Employee> filterEmployeesByCompanyName(String companyName);
    List<EmployeeBuildingsCountDto> filterEmployeesByCompanyWithMinBuildings(Long companyId, Integer minBuildings);

    List<Resident> filterResidentsByBuildingByName(Long buildingId, String firstName);
    List<Resident> filterResidentsByBuildingByAge(Long buildingId, Integer minAge, Integer maxAge);
}
