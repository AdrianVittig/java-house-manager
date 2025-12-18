package org.university.service.contract.employee_service;

import org.university.dto.EmployeeBuildingsManagementDto;

public interface EmployeeAllocationService {
    EmployeeBuildingsManagementDto allocateEmployeeToBuilding(Long buildingId);
    void reallocateEmployeeBuildings(Long employeeId, Long companyId);
}
