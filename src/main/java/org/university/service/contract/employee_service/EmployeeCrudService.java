package org.university.service.contract.employee_service;

import org.university.dto.EmployeeListDto;
import org.university.dto.EmployeeWithDetailsDto;
import org.university.entity.Employee;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public interface EmployeeCrudService {
    void createEmployee(Employee employee) throws DAOException, NotFoundException;
    EmployeeWithDetailsDto getEmployeeById(Long id) throws DAOException, NotFoundException;
    List<EmployeeListDto> getAllEmployees();
    EmployeeWithDetailsDto getEmployeeWithLeastBuildings();
    void updateEmployee(Employee employee) throws DAOException, NotFoundException;
    void deleteEmployee(Long id) throws DAOException, NotFoundException;
}
