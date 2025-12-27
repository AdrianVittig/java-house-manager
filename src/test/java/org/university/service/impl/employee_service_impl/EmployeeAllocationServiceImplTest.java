package org.university.service.impl.employee_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Building;
import org.university.entity.Company;
import org.university.entity.Contract;
import org.university.entity.Employee;
import org.university.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeAllocationServiceImplTest {

    private static EmployeeAllocationServiceImpl service;

    @BeforeAll
    static void init() {
        SessionFactoryUtil.getSessionFactory();
        service = new EmployeeAllocationServiceImpl();
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
        c.setRevenue(new BigDecimal("200000.00"));
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(c);
            tx.commit();
        }
        return c;
    }

    private Employee persistEmployee(Company company, String firstName, String lastName) {
        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setAge(30);
        e.setFeeCollectingDate(LocalDate.of(2025, 1, 15));
        e.setCompany(company);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(e);
            tx.commit();
        }
        return e;
    }

    private Building persistBuilding(String name, String address, Employee employeeOrNull) {
        Building b = new Building();
        b.setName(name);
        b.setAddress(address);
        b.setBuiltUpArea(new BigDecimal("2100.00"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.18"));
        b.setCountOfFloors(6);
        b.setApartmentsPerFloor(3);
        b.setBuiltDate(LocalDate.now().minusYears(9));

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            if (employeeOrNull != null) {
                Employee managed = session.find(Employee.class, employeeOrNull.getId());
                b.setEmployee(managed);
            }
            session.persist(b);
            tx.commit();
        }
        return b;
    }

    private void persistContract(Employee e, Building b) {
        Contract c = new Contract();
        c.setEmployee(e);
        c.setBuilding(b);
        c.setIssueDate(LocalDate.now());
        c.setEndDate(LocalDate.now().plusYears(1));
        c.setNumber("Contract: 1001");

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Employee me = session.find(Employee.class, e.getId());
            Building mb = session.find(Building.class, b.getId());
            c.setEmployee(me);
            c.setBuilding(mb);
            session.persist(c);
            mb.setEmployee(me);
            session.merge(mb);
            tx.commit();
        }
    }

    @Test
    void allocateEmployeeToBuilding_success_assignsLeastLoadedEmployee() {
        Company company = persistCompany("Company 1");
        Employee e1 = persistEmployee(company, "Georgi", "Georgiev");
        Employee e2 = persistEmployee(company, "Maria", "Ivanov");

        Building busy = persistBuilding("Building 1", "Address 1", null);
        Building target = persistBuilding("Building 2", "Address 2", null);

        persistContract(e2, busy);

        var dto = service.allocateEmployeeToBuilding(target.getId());

        assertNotNull(dto);
        assertEquals(e1.getId(), dto.getId());
    }

    @Test
    void allocateEmployeeToBuilding_whenBuildingMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.allocateEmployeeToBuilding(999999L));
    }

    @Test
    void allocateEmployeeToBuilding_whenAlreadyHasEmployee_throws() {
        Company company = persistCompany("Company 2");
        Employee e = persistEmployee(company, "Petar", "Petrov");
        Building b = persistBuilding("Building 1", "Address 1", e);

        assertThrows(IllegalArgumentException.class, () -> service.allocateEmployeeToBuilding(b.getId()));
    }

    @Test
    void allocateEmployeeToBuilding_whenNoEmployees_throws() {
        Building b = persistBuilding("Building 3", "Address 3", null);
        assertThrows(RuntimeException.class, () -> service.allocateEmployeeToBuilding(b.getId()));
    }

    @Test
    void reallocateEmployeeBuildings_success_movesContractsAndUpdatesBuildings() {
        Company company = persistCompany("Company 3");
        Employee leaving = persistEmployee(company, "Ivan", "Ivanov");
        Employee target = persistEmployee(company, "Yordan", "Georgiev");

        Building b1 = persistBuilding("Building 1", "Address 1", null);
        Building b2 = persistBuilding("Building 2", "Address 2", null);

        persistContract(leaving, b1);
        persistContract(leaving, b2);

        service.reallocateEmployeeBuildings(leaving.getId(), company.getId());

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Building mb1 = session.find(Building.class, b1.getId());
            Building mb2 = session.find(Building.class, b2.getId());

            assertNotNull(mb1.getEmployee());
            assertNotNull(mb2.getEmployee());
            assertEquals(target.getId(), mb1.getEmployee().getId());
            assertEquals(target.getId(), mb2.getEmployee().getId());
        }
    }

    @Test
    void reallocateEmployeeBuildings_whenEmployeeMissing_throws() {
        assertThrows(RuntimeException.class, () -> service.reallocateEmployeeBuildings(999999L, 1L));
    }

    @Test
    void reallocateEmployeeBuildings_whenNoContracts_throws() {
        Company company = persistCompany("Company 1");
        Employee leaving = persistEmployee(company, "Georgi", "Georgiev");
        assertThrows(IllegalArgumentException.class, () -> service.reallocateEmployeeBuildings(leaving.getId(), company.getId()));
    }

    @Test
    void reallocateEmployeeBuildings_whenNoTargetEmployees_throws() {
        Company company = persistCompany("Company 2");
        Employee leaving = persistEmployee(company, "Maria", "Ivanov");
        Building b = persistBuilding("Building 1", "Address 1", null);
        persistContract(leaving, b);

        assertThrows(IllegalArgumentException.class, () -> service.reallocateEmployeeBuildings(leaving.getId(), company.getId()));
    }
}
