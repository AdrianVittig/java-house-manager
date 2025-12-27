package org.university.service.impl.contract_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.contract_dao.ContractCrudDao;
import org.university.entity.Building;
import org.university.entity.Company;
import org.university.entity.Contract;
import org.university.entity.Employee;
import org.university.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ContractCrudServiceImplTest {

    private static ContractCrudServiceImpl service;
    private static ContractCrudDao contractDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new ContractCrudServiceImpl();
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

    private Company persistCompany() {
        Company c = new Company();
        c.setName("Test Company");
        c.setRevenue(new BigDecimal("1000.00"));

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(c);
            tx.commit();
        }

        assertNotNull(c.getId());
        return c;
    }

    private Employee persistEmployee() {
        Company c = persistCompany();
        Employee e = new Employee();
        e.setFirstName("Ivan");
        e.setLastName("Ivanov");
        e.setAge(30);
        e.setFeeCollectingDate(LocalDate.now());
        e.setCompany(c);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(e);
            tx.commit();
        }

        assertNotNull(e.getId());
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

    @Test
    void createContract_success_generatesNumberAndDates() {
        Employee e = persistEmployee();
        Building b = persistBuilding("Building");

        Contract c = new Contract();
        c.setEmployee(e);
        c.setBuilding(b);

        service.createContract(c);

        assertNotNull(c.getId());
        assertNotNull(c.getNumber());
        assertNotNull(c.getIssueDate());
        assertNotNull(c.getEndDate());
        assertTrue(c.getNumber().startsWith("Contract: "));
        assertEquals(LocalDate.now(), c.getIssueDate());
        assertEquals(LocalDate.now().plusYears(1), c.getEndDate());
    }

    @Test
    void createContract_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.createContract(null));
    }

    @Test
    void createContract_whenMissingEmployee_throws() {
        Building b = persistBuilding("Building");
        Contract c = new Contract();
        c.setBuilding(b);
        assertThrows(IllegalArgumentException.class, () -> service.createContract(c));
    }

    @Test
    void createContract_whenBuildingAlreadyHasContract_throws() {
        Employee e = persistEmployee();
        Building b = persistBuilding("Building");

        Contract c1 = new Contract();
        c1.setEmployee(e);
        c1.setBuilding(b);
        service.createContract(c1);

        Contract c2 = new Contract();
        c2.setEmployee(e);
        c2.setBuilding(b);

        assertThrows(IllegalArgumentException.class, () -> service.createContract(c2));
    }

    @Test
    void getContractById_success() {
        Employee e = persistEmployee();
        Building b = persistBuilding("Building");

        Contract c = new Contract();
        c.setEmployee(e);
        c.setBuilding(b);
        service.createContract(c);

        assertNotNull(service.getContractById(c.getId()));
    }

    @Test
    void getContractById_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.getContractById(999999L));
    }

    @Test
    void getAllContracts_returnsList() {
        Employee e = persistEmployee();
        Building b1 = persistBuilding("Building");
        Building b2 = persistBuilding("Building 2");

        Contract c1 = new Contract();
        c1.setEmployee(e);
        c1.setBuilding(b1);
        service.createContract(c1);

        Contract c2 = new Contract();
        c2.setEmployee(e);
        c2.setBuilding(b2);
        service.createContract(c2);

        assertEquals(2, service.getAllContracts().size());
    }

    @Test
    void updateContract_success() {
        Employee e = persistEmployee();
        Building b = persistBuilding("Building");

        Contract c = new Contract();
        c.setEmployee(e);
        c.setBuilding(b);
        service.createContract(c);

        c.setNumber("Contract: 7777");
        c.setIssueDate(LocalDate.now().minusDays(5));
        c.setEndDate(LocalDate.now().plusYears(2));

        service.updateContract(c);

        Contract updated = contractDao.getContractById(c.getId());
        assertNotNull(updated);
        assertEquals("Contract: 7777", updated.getNumber());
        assertEquals(c.getIssueDate(), updated.getIssueDate());
        assertEquals(c.getEndDate(), updated.getEndDate());
    }

    @Test
    void updateContract_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.updateContract(null));
    }

    @Test
    void deleteContract_success() {
        Employee e = persistEmployee();
        Building b = persistBuilding("Building");

        Contract c = new Contract();
        c.setEmployee(e);
        c.setBuilding(b);
        service.createContract(c);

        service.deleteContract(c.getId());

        assertNull(contractDao.getContractById(c.getId()));
    }

    @Test
    void deleteContract_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.deleteContract(999999L));
    }
}
