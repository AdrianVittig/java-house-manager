package org.university.dao.employee_dao;

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
import org.university.exception.DAOException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeCrudDaoTest {

    private static EmployeeCrudDao employeeCrudDao;
    private static ContractCrudDao contractCrudDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        employeeCrudDao = new EmployeeCrudDao();
        contractCrudDao = new ContractCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Contract").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();
            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();

            transaction.commit();
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

    private Employee persistEmployee(String firstName, String lastName, int age, Company company) {
        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setAge(age);
        e.setFeeCollectingDate(LocalDate.now());
        e.setCompany(company);

        employeeCrudDao.createEmployee(e);
        assertNotNull(e.getId());
        return e;
    }

    private Building persistBuilding(String name, String address) {
        Building b = new Building();
        b.setName(name);
        b.setAddress(address);
        b.setBuiltUpArea(new BigDecimal("120"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        b.setCountOfFloors(3);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now());

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(b);
            tx.commit();
        }
        assertNotNull(b.getId());
        return b;
    }

    private Contract persistContract(Employee e, Building b, String number) {
        Contract c = new Contract();
        c.setNumber(number);
        c.setIssueDate(LocalDate.now().minusDays(1));
        c.setEndDate(LocalDate.now().plusDays(30));
        c.setEmployee(e);
        c.setBuilding(b);

        contractCrudDao.createContract(c);
        assertNotNull(c.getId());
        return c;
    }

    @Test
    void createEmployee_persistsEntity() {
        Company company = persistCompany("Company 1");
        Employee e = persistEmployee("Ivan", "Ivanov", 25, company);
        assertNotNull(e.getId());
    }

    @Test
    void getEmployeeById_returnsEntity() {
        Company company = persistCompany("Company 1");
        Employee e = persistEmployee("Georgi", "Georgiev", 25, company);

        Employee found = employeeCrudDao.getEmployeeById(e.getId());

        assertNotNull(found);
        assertEquals(e.getId(), found.getId());
        assertEquals("Georgi", found.getFirstName());
        assertEquals("Georgiev", found.getLastName());
    }

    @Test
    void getEmployeeById_whenMissing_returnsNull() {
        Employee found = employeeCrudDao.getEmployeeById(89988L);
        assertNull(found);
    }

    @Test
    void getAllEmployees_returnsList() {
        Company company = persistCompany("Company 1");
        persistEmployee("Ivan", "Ivanov", 25, company);
        persistEmployee("Maria", "Petrov", 26, company);

        List<Employee> employees = employeeCrudDao.getAllEmployees();

        assertNotNull(employees);
        assertTrue(employees.size() >= 2);
    }

    @Test
    void getEmployeeIdWithLeastContracts_returnsEmployeeWithZeroContracts() {
        Company company = persistCompany("Company 1");

        Employee e1 = persistEmployee("Petar", "Petrov", 20, company);
        Employee e2 = persistEmployee("Yordan", "Georgiev", 20, company);

        Building b = persistBuilding("Building 1", "Address 1");
        persistContract(e2, b, "Contract: 1001");

        Long leastId = employeeCrudDao.getEmployeeIdWithLeastContracts();

        assertNotNull(leastId);
        assertEquals(e1.getId(), leastId);
    }

    @Test
    void getEmployeeIdWithLeastContracts_whenNoEmployees_throwsDAOException() {
        assertThrows(DAOException.class, () -> employeeCrudDao.getEmployeeIdWithLeastContracts());
    }

    @Test
    void getEmployeeWithRelations_returnsEmployeeAndContractList() {
        Company company = persistCompany("Company 1");
        Employee e = persistEmployee("Georgi", "Georgiev", 25, company);

        Building b = persistBuilding("Building 1", "Address 1");
        persistContract(e, b, "Contract: 1001");

        Employee found = employeeCrudDao.getEmployeeWithRelations(e.getId());

        assertNotNull(found);
        assertEquals(e.getId(), found.getId());
        assertNotNull(found.getContractList());
        assertEquals(1, found.getContractList().size());
    }

    @Test
    void getEmployeeWithRelations_whenMissing_throwsDAOException() {
        assertThrows(DAOException.class, () -> employeeCrudDao.getEmployeeWithRelations(999999L));
    }

    @Test
    void getEmployeeIdWithLeastContractsExcluding_returnsLeastExcludingGivenEmployee() {
        Company company = persistCompany("Company 1");

        Employee e1 = persistEmployee("Ivan", "Ivanov", 20, company);
        Employee e2 = persistEmployee("Georgi", "Georgiev", 28, company);
        Employee e3 = persistEmployee("Petar", "Petrov", 35, company);

        Building b = persistBuilding("Building 1", "Address 1");
        Building b2 = persistBuilding("Building 2", "Address 2");
        Building b3 = persistBuilding("Building 3", "Address 3");

        persistContract(e2, b, "Contract: 1001");
        persistContract(e3, b2, "Contract: 1002");
        persistContract(e3, b3, "Contract: 1003");

        Long id = employeeCrudDao.getEmployeeIdWithLeastContractsExcluding(e1.getId(), company.getId());

        assertNotNull(id);
        assertEquals(e2.getId(), id);
    }

    @Test
    void getEmployeeIdWithLeastContractsExcluding_whenNoOtherEmployees_returnsNull() {
        Company c = persistCompany("Company 1");
        Employee only = persistEmployee("Yordan", "Georgiev", 22, c);

        Long id = employeeCrudDao.getEmployeeIdWithLeastContractsExcluding(only.getId(), c.getId());

        assertNull(id);
    }

    @Test
    void getEmployeeIdWithLeastContractsExcluding_whenCompanyHasNoEmployees_returnsNull() {
        Company c = persistCompany("Company 1");
        Long id = employeeCrudDao.getEmployeeIdWithLeastContractsExcluding(123L, c.getId());
        assertNull(id);
    }

    @Test
    void updateEmployee_updatesFields() {
        Company company = persistCompany("Company 1");
        Employee e = persistEmployee("Ivan", "Ivanov", 25, company);

        Employee toUpdate = new Employee();
        toUpdate.setFirstName("Georgi");
        toUpdate.setLastName("Georgiev");
        toUpdate.setAge(40);
        toUpdate.setFeeCollectingDate(LocalDate.now().minusDays(3));

        employeeCrudDao.updateEmployee(e.getId(), toUpdate);

        Employee updated = employeeCrudDao.getEmployeeById(e.getId());
        assertNotNull(updated);

        assertEquals("Georgi", updated.getFirstName());
        assertEquals("Georgiev", updated.getLastName());
        assertEquals(40, updated.getAge());
        assertEquals(toUpdate.getFeeCollectingDate(), updated.getFeeCollectingDate());
    }

    @Test
    void updateEmployee_whenMissing_throwsDAOException() {
        Employee patch = new Employee();
        patch.setFirstName("Georgi");
        patch.setLastName("Georgiev");
        patch.setAge(40);
        patch.setFeeCollectingDate(LocalDate.now());

        assertThrows(DAOException.class, () -> employeeCrudDao.updateEmployee(999999L, patch));
    }

    @Test
    void deleteEmployee_deletesEntity() {
        Company company = persistCompany("Company 1");
        Employee e = persistEmployee("Ivan", "Ivanov", 25, company);

        employeeCrudDao.deleteEmployee(e.getId());

        Employee found = employeeCrudDao.getEmployeeById(e.getId());
        assertNull(found);
    }

    @Test
    void deleteEmployee_whenMissing_throwsDAOException() {
        assertThrows(DAOException.class, () -> employeeCrudDao.deleteEmployee(999999L));
    }
}
