package org.university.dao.company_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Company;
import org.university.entity.Employee;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompanyCrudDaoTest {

    private static CompanyCrudDao companyCrudDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        companyCrudDao = new CompanyCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();

            transaction.commit();
        }
    }

    private Company persistCompany(String name) {
        Company c = new Company();
        c.setName(name);
        c.setRevenue(new BigDecimal("1000"));

        companyCrudDao.createCompany(c);
        assertNotNull(c.getId());
        return c;
    }

    private Employee persistEmployeeForCompany(Company company, String firstName, String lastName) {
        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setAge(30);
        e.setFeeCollectingDate(LocalDate.now());
        e.setCompany(company);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Company managedCompany = session.find(Company.class, company.getId());
            assertNotNull(managedCompany);

            e.setCompany(managedCompany);
            session.persist(e);

            transaction.commit();
        }
        assertNotNull(e.getId());
        return e;
    }

    @Test
    void createCompany_persistsEntity() {
        Company c = persistCompany("Company 1");
        assertNotNull(c.getId());
    }

    @Test
    void getCompanyById_returnsEntity() {
        Company c = persistCompany("Company 1");

        Company found = companyCrudDao.getCompanyById(c.getId());
        assertNotNull(found);
        assertEquals(c.getId(), found.getId());
        assertEquals("Company 1", found.getName());
    }

    @Test
    void getCompanyById_whenMissing_returnsNull() {
        Company found = companyCrudDao.getCompanyById(89888888L);
        assertNull(found);
    }

    @Test
    void getCompanyWithDetails_returnsCompanyWithEmployees() {
        Company c = persistCompany("Company 1");
        persistEmployeeForCompany(c, "Georgi", "Georgiev");
        persistEmployeeForCompany(c, "Maria", "Ivanov");

        Company found = companyCrudDao.getCompanyWithDetails(c.getId());
        assertNotNull(found);
        assertNotNull(found.getEmployeeList());
        assertEquals(2, found.getEmployeeList().size());
    }

    @Test
    void getCompanyWithDetails_whenMissing_returnsNull() {
        Company found = companyCrudDao.getCompanyWithDetails(8988858888L);
        assertNull(found);
    }

    @Test
    void getAllCompanies_returnsList() {
        persistCompany("Company 1");
        persistCompany("Company 2");

        List<Company> companies = companyCrudDao.getAllCompanies();
        assertNotNull(companies);
        assertEquals(2, companies.size());
    }

    @Test
    void updateCompany_updatesFields() {
        Company c = persistCompany("Company 1");
        Company toUpdate = new Company();
        toUpdate.setName("Company 2");
        toUpdate.setRevenue(new BigDecimal("30000.00"));

        companyCrudDao.updateCompany(c.getId(), toUpdate);

        Company updated = companyCrudDao.getCompanyById(c.getId());
        assertNotNull(updated);

        assertEquals("Company 2", updated.getName());
        assertEquals(new BigDecimal("30000.00"), updated.getRevenue());
    }

    @Test
    void updateCompany_whenMissing_throwsDAOException() {
        Company c = new Company();
        c.setName("Company 1");
        c.setRevenue(new BigDecimal("1000"));

        assertThrows(NotFoundException.class, () -> companyCrudDao.updateCompany(89999898L, c));
    }

    @Test
    void deleteCompany_deletesEntity() {
        Company c = persistCompany("Company 1");
        companyCrudDao.deleteCompany(c.getId());

        Company found = companyCrudDao.getCompanyById(c.getId());
        assertNull(found);
    }

    @Test
    void deleteCompany_whenMissing_throwsDAOException() {
        assertThrows(NotFoundException.class, () -> companyCrudDao.deleteCompany(89999898L));
    }
}
