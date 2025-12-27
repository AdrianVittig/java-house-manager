package org.university.service.impl.company_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.company_dao.CompanyCrudDao;
import org.university.entity.Company;
import org.university.exception.NotFoundException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CompanyCrudServiceImplTest {

    private static CompanyCrudServiceImpl service;
    private static CompanyCrudDao companyDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new CompanyCrudServiceImpl();
        companyDao = new CompanyCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();
            tx.commit();
        }
    }

    private Company newCompany(String name) {
        Company c = new Company();
        c.setName(name);
        c.setRevenue(new BigDecimal("1000.00"));
        return c;
    }

    @Test
    void createCompany_success() {
        Company c = newCompany("Test Company");
        service.createCompany(c);
        assertNotNull(c.getId());
        assertNotNull(companyDao.getCompanyById(c.getId()));
    }

    @Test
    void createCompany_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.createCompany(null));
    }

    @Test
    void createCompany_whenBlankName_throws() {
        Company c = newCompany(" ");
        assertThrows(IllegalArgumentException.class, () -> service.createCompany(c));
    }

    @Test
    void getCompanyById_success() {
        Company c = newCompany("Test Company");
        service.createCompany(c);
        assertNotNull(service.getCompanyById(c.getId()));
    }

    @Test
    void getCompanyById_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.getCompanyById(999999L));
    }

    @Test
    void getAllCompanies_returnsList() {
        Company c1 = newCompany("Company");
        Company c2 = newCompany("Company 2");
        service.createCompany(c1);
        service.createCompany(c2);
        assertEquals(2, service.getAllCompanies().size());
    }

    @Test
    void updateCompany_success() {
        Company c = newCompany("C1");
        service.createCompany(c);

        c.setName("Updated");
        c.setRevenue(new BigDecimal("5555.55"));

        service.updateCompany(c);

        Company updated = companyDao.getCompanyById(c.getId());
        assertNotNull(updated);
        assertEquals("Updated", updated.getName());
        assertEquals(new BigDecimal("5555.55"), updated.getRevenue());
    }

    @Test
    void updateCompany_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.updateCompany(null));
    }

    @Test
    void deleteCompany_success() {
        Company c = newCompany("Company 1");
        service.createCompany(c);

        service.deleteCompany(c.getId());

        assertNull(companyDao.getCompanyById(c.getId()));
    }

    @Test
    void deleteCompany_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.deleteCompany(999999L));
    }
}
