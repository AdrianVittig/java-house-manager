package org.university.service.impl.employee_service_impl;

import org.university.dao.building_dao.BuildingCrudDao;
import org.university.dao.employee_dao.EmployeeCrudDao;
import org.university.dao.employee_dao.EmployeeMapper;
import org.university.dto.EmployeeBuildingsManagementDto;
import org.university.entity.Building;
import org.university.entity.Contract;
import org.university.entity.Employee;
import org.university.exception.NotFoundException;
import org.university.service.contract.building_service.BuildingCrudService;
import org.university.service.contract.contract_service.ContractCrudService;
import org.university.service.contract.employee_service.EmployeeAllocationService;
import org.university.service.contract.employee_service.EmployeeCrudService;
import org.university.service.impl.building_service_impl.BuildingCrudServiceImpl;
import org.university.service.impl.contract_service_impl.ContractCrudServiceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EmployeeAllocationServiceImpl implements EmployeeAllocationService {
    private final EmployeeCrudDao employeeDao = new EmployeeCrudDao();
    private final EmployeeMapper employeeMapper = new EmployeeMapper();
    private final BuildingCrudDao buildingDao = new BuildingCrudDao();
    private final ContractCrudService contractCrudService = new ContractCrudServiceImpl();
    private final EmployeeCrudService employeeCrudService = new EmployeeCrudServiceImpl();
    private final BuildingCrudService buildingCrudService = new BuildingCrudServiceImpl();

    @Override
    public EmployeeBuildingsManagementDto allocateEmployeeToBuilding(Long buildingId) {
        Building building = buildingDao.getBuildingById(buildingId);

        if(building == null){
            throw new NotFoundException("Building with id " + buildingId + " does not exist");
        }

        if(building.getEmployee() != null){
            throw new IllegalArgumentException("Building with id " + buildingId + " already has an employee");
        }

        Long employeeId = employeeDao.getEmployeeIdWithLeastContracts();
        if(employeeId == null){
            throw new NotFoundException("There are no employees with least contracts");
        }

        Employee employeeWithLeastContracts = employeeDao.getEmployeeWithRelations(employeeId);

        if(employeeWithLeastContracts == null){
            throw new NotFoundException("Employee with id " + employeeId + " does not exist");
        }

        Contract contract = new Contract();
        contract.setEmployee(employeeWithLeastContracts);
        contract.setBuilding(building);
        contractCrudService.createContract(contract);

        return employeeMapper.employeeToAllocationDto(employeeWithLeastContracts);
    }

    @Override
    public void reallocateEmployeeBuildings(Long employeeId, Long companyId) {
        Employee employeeToLeave = employeeDao.getEmployeeWithRelations(employeeId);
        if (employeeToLeave == null) {
            throw new NotFoundException("Employee with id " + employeeId + " does not exist");
        }

        List<Contract> contractsToReallocate = new ArrayList<>(employeeToLeave.getContractList());
        if (contractsToReallocate.isEmpty()) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " does not have any contracts");
        }

        for (Contract contract : contractsToReallocate) {
            Long targetEmployeeId = employeeDao.getEmployeeIdWithLeastContractsExcluding(employeeId, companyId);
            if (targetEmployeeId == null) {
                throw new IllegalArgumentException("There are no employees available to reallocate contracts");
            }

            Employee targetEmployee = employeeDao.getEmployeeById(targetEmployeeId);
            if (targetEmployee == null) {
                throw new NotFoundException("Employee with id " + targetEmployeeId + " does not exist");
            }

            Building building = contract.getBuilding();
            if (building == null || building.getId() == null) {
                throw new IllegalArgumentException("Contract with id " + contract.getId() + " does not have a building");
            }

            contract.setEmployee(targetEmployee);
            contractCrudService.updateContract(contract);

            buildingDao.updateBuildingEmployee(building.getId(), targetEmployee);
        }
    }
}
