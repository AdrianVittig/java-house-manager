package org.university.dao.contract_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Building;
import org.university.entity.Company;
import org.university.entity.Contract;
import org.university.entity.Employee;
import org.university.exception.DAOException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContractCrudDaoTest {
    private static ContractCrudDao contractCrudDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        contractCrudDao = new ContractCrudDao();
    }

    @AfterEach
    void cleanup(){
        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Contract").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();
            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();

            transaction.commit();
        }
    }

    private Company persistCompany(){
        Company c = new Company();
        c.setName("Test Company");
        c.setRevenue(new BigDecimal("1000.00"));

        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(c);
            transaction.commit();
        }
        assertNotNull(c.getId());
        return c;
    }

    private Employee persistEmployee(){
        Company c = persistCompany();

        Employee e = new Employee();
        e.setFirstName("Georgi");
        e.setLastName("Georgiev");
        e.setAge(30);
        e.setFeeCollectingDate(LocalDate.now());
        e.setCompany(c);

        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();

            Company managed = session.find(Company.class, c.getId());
            assertNotNull(managed);

            e.setCompany(managed);
            session.persist(e);

            transaction.commit();
        }
        assertNotNull(e.getId());
        return e;
    }

    private Building persistBuilding(){
        Building b = new Building();
        b.setName("Test Building");
        b.setAddress("Test Address");
        b.setBuiltUpArea(new BigDecimal("120"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        b.setCountOfFloors(3);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now());

        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(b);
            transaction.commit();
        }
        assertNotNull(b.getId());
        return b;
    }

    private Contract persistContract(Employee e, Building b) {
        Contract c = new Contract();
        c.setNumber("Contract: 1001");
        c.setIssueDate(LocalDate.now().minusDays(10));
        c.setEndDate(LocalDate.now().plusYears(1));
        c.setEmployee(e);
        c.setBuilding(b);

        contractCrudDao.createContract(c);
        return c;
    }

    @Test
    void createContract_persists() {
        Employee e = persistEmployee();
        Building b = persistBuilding();

        Contract c = persistContract(e, b);
        assertNotNull(c.getId());
    }

    @Test
    void createContract_whenEmployeeMissing_throwsDAOException(){
        Building b = persistBuilding();

        Employee fakeEmployee = new Employee();
        fakeEmployee.setId(9999L);

        Contract c = new Contract();
        c.setNumber("Contract: 1001");
        c.setIssueDate(LocalDate.now().minusDays(10));
        c.setEndDate(LocalDate.now().plusYears(1));
        c.setEmployee(fakeEmployee);
        c.setBuilding(b);

        assertThrows(DAOException.class, () -> contractCrudDao.createContract(c));
    }

    @Test
    void createContract_whenBuildingMissing_throwsDAOException(){
        Employee e = persistEmployee();

        Building fakeBuilding = new Building();
        fakeBuilding.setId(9999L);

        Contract c = new Contract();
        c.setNumber("Contract: 1001");
        c.setIssueDate(LocalDate.now().minusDays(10));
        c.setEndDate(LocalDate.now().plusYears(1));
        c.setEmployee(e);
        c.setBuilding(fakeBuilding);

        assertThrows(DAOException.class, () -> contractCrudDao.createContract(c));
    }

    @Test
    void getContractById_returnsEntity() {
        Employee e = persistEmployee();
        Building b = persistBuilding();
        Contract c = persistContract(e, b);

        Contract found = contractCrudDao.getContractById(c.getId());

        assertNotNull(found);
        assertEquals(c.getId(), found.getId());
        assertEquals("Contract: 1001", found.getNumber());
    }

    @Test
    void getContractById_whenMissing_returnsNull(){
        Contract found = contractCrudDao.getContractById(89888888L);
        assertNull(found);
    }

    @Test
    void getAllContracts_thenReturnList() {
        Employee e = persistEmployee();
        Building b = persistBuilding();
        persistContract(e, b);

        Building b2 = persistBuilding();
        Employee e2 = persistEmployee();
        persistContract(e2, b2);

        List<Contract> contracts = contractCrudDao.getAllContracts();

        assertNotNull(contracts);
        assertEquals(2, contracts.size());
    }

    @Test
    void getContractWithDetails_returnsEntityWithEmployeeAndBuilding() {
        Employee e = persistEmployee();
        Building b = persistBuilding();
        Contract c = persistContract(e, b);

        Contract found = contractCrudDao.getContractWithDetails(c.getId());

        assertNotNull(found);
        assertEquals(c.getId(), found.getId());

        assertNotNull(found.getEmployee());
        assertEquals(e.getId(), found.getEmployee().getId());

        assertNotNull(found.getBuilding());
        assertEquals(b.getId(), found.getBuilding().getId());
    }

    @Test
    void getContractWithDetails_whenMissing_returnsNull(){
        Contract found = contractCrudDao.getContractWithDetails(89888888L);
        assertNull(found);
    }

    @Test
    void existsByBuildingId_whenExists_returnsTrue() {
        Employee e = persistEmployee();
        Building b = persistBuilding();
        persistContract(e, b);

        assertTrue(contractCrudDao.existsByBuildingId(b.getId()));
    }

    @Test
    void existsByBuildingId_whenMissing_returnsFalse() {
        Building b = persistBuilding();
        assertFalse(contractCrudDao.existsByBuildingId(b.getId()));
    }

    @Test
    void getCountOfContracts_returnsCorrectCount() {
        Employee e = persistEmployee();
        Building b = persistBuilding();
        persistContract(e, b);

        assertEquals(1, contractCrudDao.getCountOfContracts());
    }

    @Test
    void updateContract_updatesFields() {
        Employee e = persistEmployee();
        Building b = persistBuilding();
        Contract c = persistContract(e, b);

        Contract toUpdate = new Contract();
        toUpdate.setNumber("Contract: 2000");
        toUpdate.setIssueDate(LocalDate.now().minusDays(15));
        toUpdate.setEndDate(LocalDate.now().plusYears(2));
        toUpdate.setEmployee(e);
        toUpdate.setBuilding(b);

        contractCrudDao.updateContract(c.getId(), toUpdate);

        Contract updated = contractCrudDao.getContractById(c.getId());
        assertNotNull(updated);
        assertEquals("Contract: 2000", updated.getNumber());
        assertEquals(toUpdate.getIssueDate(), updated.getIssueDate());
        assertEquals(toUpdate.getEndDate(), updated.getEndDate());
    }

    @Test
    void updateContract_whenMissing_throwsDAOException(){
        Contract toUpdate = new Contract();
        toUpdate.setId(89888888L);
        toUpdate.setIssueDate(LocalDate.now().minusDays(15));
        toUpdate.setEndDate(LocalDate.now().plusYears(2));

        assertThrows(DAOException.class, () -> contractCrudDao.updateContract(89888888L, toUpdate));
    }

    @Test
    void deleteContract_deletesEntity() {
        Employee e = persistEmployee();
        Building b = persistBuilding();
        Contract c = persistContract(e, b);

        contractCrudDao.deleteContract(c.getId());

        Contract found = contractCrudDao.getContractById(c.getId());
        assertNull(found);
    }

    @Test
    void deleteContract_whenMissing_throwsDAOException(){
        assertThrows(DAOException.class, () -> contractCrudDao.deleteContract(89888888L));
    }
}