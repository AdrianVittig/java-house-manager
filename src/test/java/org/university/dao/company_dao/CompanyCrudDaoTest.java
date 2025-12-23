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
    void cleanup(){
        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();

            transaction.commit();
        }
    }

    private Company persistCompany(String name){
        Company c = new Company();
        c.setName(name);
        c.setRevenue(new BigDecimal("1000"));

        companyCrudDao.createCompany(c);
        assertNotNull(c.getId());
        return c;
    }

    private Employee persistEmployeeForCompany(Company company){
        Employee e = new Employee();
        e.setFirstName("Georgi");
        e.setLastName("Georgiev");
        e.setAge(30);
        e.setFeeCollectingDate(LocalDate.now());
        e.setCompany(company);

        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
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
        Company c = persistCompany("Test Company");
        assertNotNull(c.getId());
    }

    @Test
    void getCompanyById_returnsEntity() {
        Company c = persistCompany("Test Company");

        Company found = companyCrudDao.getCompanyById(c.getId());
        assertNotNull(found);
        assertEquals(c.getId(), found.getId());
        assertEquals("Test Company", found.getName());
    }

    @Test
    void getCompanyById_whenMissing_returnsNull(){
        Company found = companyCrudDao.getCompanyById(89888888L);
        assertNull(found);
    }

    @Test
    void getCompanyWithDetails_returnsCompanyWithEmployees() {
        Company c = persistCompany("Test Company");
        persistEmployeeForCompany(c);
        persistEmployeeForCompany(c);

        Company found = companyCrudDao.getCompanyWithDetails(c.getId());
        assertNotNull(found);
        assertEquals(2, found.getEmployeeList().size());
        assertNotNull(found.getEmployeeList());
    }

    @Test
    void getCompanyWithDetails_whenMissing_returnsNull(){
        Company found = companyCrudDao.getCompanyWithDetails(8988858888L);
        assertNull(found);
    }

    @Test
    void getAllCompanies_returnsList() {
        persistCompany("Test Company 1");
        persistCompany("Test Company 2");

        List<Company> companies = companyCrudDao.getAllCompanies();
        assertNotNull(companies);
        assertEquals(2, companies.size());
    }

    @Test
    void updateCompany_updatesFields() {
        Company c = persistCompany("Test Company");
        Company toUpdate = new Company();
        toUpdate.setName("Updated Company");
        toUpdate.setRevenue(new BigDecimal("30000.00"));

        companyCrudDao.updateCompany(c.getId(), toUpdate);

        Company updated = companyCrudDao.getCompanyById(c.getId());
        assertNotNull(updated);

        assertEquals("Updated Company", updated.getName());
        assertEquals(new BigDecimal("30000.00"), updated.getRevenue());
    }

    @Test
    void updateCompany_whenMissing_throwsDAOException(){
        Company c = new Company();
        c.setName("Test Company");
        c.setRevenue(new BigDecimal("1000"));

        assertThrows(DAOException.class, () -> companyCrudDao.updateCompany(89999898L, c));
    }

    @Test
    void deleteCompany_deletesEntity() {
        Company c = persistCompany("Test Company");
        companyCrudDao.deleteCompany(c.getId());

        Company found = companyCrudDao.getCompanyById(c.getId());
        assertNull(found);
    }

    @Test
    void deleteCompany_whenMissing_throwsDAOException(){
        assertThrows(DAOException.class, () -> companyCrudDao.deleteCompany(89999898L));
    }
}