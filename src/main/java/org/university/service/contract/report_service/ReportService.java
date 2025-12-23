package org.university.service.contract.report_service;

import org.university.dto.BuildingAmountDto;
import org.university.dto.CompanyAmountDto;
import org.university.dto.EmployeeAmountDto;
import org.university.dto.EmployeeBuildingsCountDto;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.entity.Resident;

import java.time.YearMonth;
import java.util.List;

public interface ReportService {
    List<EmployeeBuildingsCountDto> getBuildingsCountByEmployeesForCompany(Long companyId);
    long getCountBuildingsByEmployee(Long employeeId);
    List<Building> getBuildingsByEmployee(Long employeeId);


    long countApartmentsByBuilding(Long buildingId);
    List<Apartment> getApartmentsByBuilding(Long buildingId);

    long countResidentsByBuilding(Long buildingId);
    List<Resident> getResidentsByBuilding(Long buildingId);

    long countCompaniesForAmountsToPay(YearMonth billingMonth);
    List<CompanyAmountDto> getAmountsToPayByCompany(YearMonth billingMonth);

    long countBuildingsForAmountsToPay(YearMonth billingMonth);
    List<BuildingAmountDto> getAmountsToPayByBuilding(YearMonth billingMonth);

    long countEmployeesForAmountsToPay(YearMonth billingMonth);
    List<EmployeeAmountDto> getAmountsToPayByEmployee(YearMonth billingMonth);

    long countCompaniesForPaidAmounts(YearMonth billingMonth);
    List<CompanyAmountDto> getPaidAmountsByCompany(YearMonth billingMonth);

    long countBuildingsForPaidAmounts(YearMonth billingMonth);
    List<BuildingAmountDto> getPaidAmountsByBuilding(YearMonth billingMonth);

    long countEmployeesForPaidAmounts(YearMonth billingMonth);
    List<EmployeeAmountDto> getPaidAmountsByEmployee(YearMonth billingMonth);
}
