package org.university.dao.employee_dao;

import org.university.dto.EmployeeListDto;
import org.university.dto.EmployeeWithDetailsDto;
import org.university.entity.Contract;
import org.university.entity.Employee;

public class EmployeeMapper {
    public EmployeeListDto toListDto(Employee employee){
        EmployeeListDto employeeListDto = new EmployeeListDto();
        employeeListDto.setId(employee.getId());
        employeeListDto.setFirstName(employee.getFirstName());
        employeeListDto.setLastName(employee.getLastName());
        employeeListDto.setAge(employee.getAge());
        employeeListDto.setFeeCollectingDate(employee.getFeeCollectingDate());
        return employeeListDto;
    }

    public EmployeeWithDetailsDto toDetailsDto(Employee employee){
        EmployeeWithDetailsDto employeeWithDetailsDto = new EmployeeWithDetailsDto();
        employeeWithDetailsDto.setId(employee.getId());
        employeeWithDetailsDto.setFirstName(employee.getFirstName());
        employeeWithDetailsDto.setLastName(employee.getLastName());
        employeeWithDetailsDto.setAge(employee.getAge());
        employeeWithDetailsDto.setFeeCollectingDate(employee.getFeeCollectingDate());

        if(employee.getCompany() != null){
            employeeWithDetailsDto.setCompanyId(employee.getCompany().getId());
        }

        if(employee.getContractList() != null){
            employeeWithDetailsDto.setContractIdsList(employee.getContractList().stream().map(Contract::getId).toList());
        }

        if(employee.getBuildingList() != null){
            employeeWithDetailsDto.setBuildingIdsList(employee.getBuildingList().stream().map(building -> building.getId()).toList());
        }
        return employeeWithDetailsDto;
    }
}
