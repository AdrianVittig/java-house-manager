package org.university.service.impl.employee_service_impl;

import org.university.dao.employee_dao.EmployeeCrudDao;
import org.university.dao.employee_dao.EmployeeMapper;
import org.university.dto.EmployeeListDto;
import org.university.dto.EmployeeWithDetailsDto;
import org.university.entity.Employee;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.service.contract.employee_service.EmployeeCrudService;

import java.util.List;

public class EmployeeCrudServiceImpl implements EmployeeCrudService {
    private final EmployeeCrudDao employeeDao = new EmployeeCrudDao();
    private final EmployeeMapper employeeMapper = new EmployeeMapper();
    @Override
    public void createEmployee(Employee employee) throws DAOException, NotFoundException {
        if(employee == null){
            throw new IllegalArgumentException("Employee cannot be null");
        }

        employeeDao.createEmployee(employee);
    }

    @Override
    public EmployeeWithDetailsDto getEmployeeById(Long id) throws DAOException, NotFoundException {
        Employee employee = employeeDao.getEmployeeById(id);
        if(employee == null){
            throw new NotFoundException("Employee with id " + id + " does not exist");
        }
        return employeeMapper.toDetailsDto(employee);
    }



    @Override
    public List<EmployeeListDto> getAllEmployees() {
        return employeeDao.getAllEmployees()
                .stream()
                .map(employeeMapper::toListDto)
                .toList();
    }

    @Override
    public EmployeeWithDetailsDto getEmployeeWithLeastBuildings() {
        Long employeeId = employeeDao.getEmployeeIdWithLeastContracts();
        Employee employee = employeeDao.getEmployeeWithRelations(employeeId);
        if(employee == null || employee.getId() == null){
            throw new NotFoundException("Employee with least buildings does not exist");
        }
        return employeeMapper.toDetailsDto(employee);
    }

    @Override
    public void updateEmployee(Employee employee) throws DAOException, NotFoundException {
        if(employee == null || employee.getId() == null){
            throw new IllegalArgumentException("Employee cannot be null");
        }

        employeeDao.updateEmployee(employee.getId(), employee);
    }

    @Override
    public void deleteEmployee(Long id) throws DAOException, NotFoundException {
        EmployeeWithDetailsDto employee = getEmployeeById(id);
        if(employee == null){
            throw new NotFoundException("Employee with id " + id + " does not exist");
        }
        employeeDao.deleteEmployee(id);
    }
}
