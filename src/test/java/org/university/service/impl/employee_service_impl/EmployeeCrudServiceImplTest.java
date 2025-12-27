package org.university.service.impl.employee_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.contract_dao.ContractCrudDao;
import org.university.dao.employee_dao.EmployeeCrudDao;
import org.university.entity.Building;
import org.university.entity.Company;
import org.university.entity.Contract;
import org.university.entity.Employee;
import org.university.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeCrudServiceImplTest {

    private static EmployeeCrudServiceImpl service;
    private static EmployeeCrudDao employeeDao;
    private static ContractCrudDao contractDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new EmployeeCrudServiceImpl();
        employeeDao = new EmployeeCrudDao();
        contractDao = new ContractCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("DELETE FROM Contract").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();
            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();
            tx.commit();
        }
    }

    private Company persistCompany(String name) {
        Company c = new Company();
        c.setName(name);
        c.setRevenue(new BigDecimal("1000.00"));

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(c);
            tx.commit();
        }

        assertNotNull(c.getId());
        return c;
    }

    private Employee newEmployee(Company company, String first, String last, int age) {
        Employee e = new Employee();
        e.setFirstName(first);
        e.setLastName(last);
        e.setAge(age);
        e.setFeeCollectingDate(LocalDate.now());
        e.setCompany(company);
        return e;
    }

    private Building persistBuilding(String name) {
        Building b = new Building();
        b.setName(name);
        b.setAddress("Test Address");
        b.setBuiltUpArea(new BigDecimal("120"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        b.setCountOfFloors(3);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now().minusDays(10));

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(b);
            tx.commit();
        }

        assertNotNull(b.getId());
        return b;
    }

    private Contract persistContract(Employee e, Building b) {
        Contract c = new Contract();
        c.setNumber("Contract: " + System.nanoTime());
        c.setIssueDate(LocalDate.now().minusDays(1));
        c.setEndDate(LocalDate.now().plusDays(30));
        c.setEmployee(e);
        c.setBuilding(b);

        contractDao.createContract(c);
        assertNotNull(c.getId());
        return c;
    }

    @Test
    void createEmployee_success() {
        Company c = persistCompany("Company");
        Employee e = newEmployee(c, "Ivan", "Ivanov", 25);

        service.createEmployee(e);

        assertNotNull(e.getId());
        assertNotNull(employeeDao.getEmployeeById(e.getId()));
    }

    @Test
    void createEmployee_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.createEmployee(null));
    }

    @Test
    void getEmployeeById_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.getEmployeeById(999999L));
    }

    @Test
    void getAllEmployees_returnsList() {
        Company c = persistCompany("Company");
        Employee e1 = newEmployee(c, "Ivan", "Ivanov", 25);
        Employee e2 = newEmployee(c, "Georgi", "Georgiev", 30);
        service.createEmployee(e1);
        service.createEmployee(e2);

        assertTrue(service.getAllEmployees().size() >= 2);
    }

    @Test
    void updateEmployee_success() {
        Company c = persistCompany("Company");
        Employee e = newEmployee(c, "Ivan", "Ivanov", 25);
        service.createEmployee(e);

        e.setFirstName("Updated");
        e.setLastName("User");
        e.setAge(40);
        e.setFeeCollectingDate(LocalDate.now().minusDays(3));

        service.updateEmployee(e);

        Employee updated = employeeDao.getEmployeeById(e.getId());
        assertNotNull(updated);
        assertEquals("Updated", updated.getFirstName());
        assertEquals("User", updated.getLastName());
        assertEquals(40, updated.getAge());
    }

    @Test
    void updateEmployee_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.updateEmployee(null));
    }

    @Test
    void deleteEmployee_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.deleteEmployee(999999L));
    }
}
