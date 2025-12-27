package org.university.service.impl.invoice_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.dto.InvoiceWithDetailsDto;
import org.university.entity.*;
import org.university.util.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceCrudServiceImplTest {

    private static InvoiceCrudServiceImpl service;
    private static InvoiceCrudDao invoiceDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new InvoiceCrudServiceImpl();
        invoiceDao = new InvoiceCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.createQuery("DELETE FROM Payment").executeUpdate();
            session.createQuery("DELETE FROM Invoice").executeUpdate();
            session.createQuery("DELETE FROM Resident").executeUpdate();
            session.createQuery("DELETE FROM Apartment").executeUpdate();
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

    private Employee persistEmployee(Company c, LocalDate collectingDate) {
        Employee e = new Employee();
        e.setFirstName("Ivan");
        e.setLastName("Ivanov");
        e.setAge(30);
        e.setFeeCollectingDate(collectingDate);
        e.setCompany(c);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Company managed = session.find(Company.class, c.getId());
            e.setCompany(managed);
            session.persist(e);
            tx.commit();
        }
        assertNotNull(e.getId());
        return e;
    }

    private Building persistBuilding(Employee e) {
        Building b = new Building();
        b.setName("Building");
        b.setAddress("Addr");
        b.setBuiltUpArea(new BigDecimal("120"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        b.setCountOfFloors(2);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now().minusDays(1));
        b.setEmployee(e);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Employee managed = session.find(Employee.class, e.getId());
            b.setEmployee(managed);
            session.persist(b);
            tx.commit();
        }
        assertNotNull(b.getId());
        return b;
    }

    private Apartment persistApartment(Building b, String number, BigDecimal area, boolean hasPet) {
        Apartment a = new Apartment();
        a.setNumber(number);
        a.setArea(area);
        a.setHasPet(hasPet);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Building managed = session.find(Building.class, b.getId());
            a.setBuilding(managed);
            session.persist(a);
            tx.commit();
        }
        assertNotNull(a.getId());
        return a;
    }

    @Test
    void createInvoiceForApartment_success_dueDateClampedToMonthLength() {
        Company c = persistCompany("Company");
        Employee e = persistEmployee(c, LocalDate.of(2025, 1, 31));
        Building b = persistBuilding(e);
        Apartment a = persistApartment(b, "Room: 1001", new BigDecimal("70.00"), false);

        YearMonth bm = YearMonth.of(2025, 2);

        service.createInvoiceForApartment(a.getId(), bm);

        Invoice invoice = invoiceDao.getInvoiceByApartmentAndMonth(a.getId(), bm);
        assertNotNull(invoice);
        assertEquals(bm, invoice.getBillingMonth());
        assertEquals(LocalDate.of(2025, 2, 28), invoice.getDueDate());
        assertEquals(PaymentStatus.NOT_PAID, invoice.getPaymentStatus());
        assertEquals(0, new BigDecimal("70.00").compareTo(invoice.getTotalAmount()));
    }

    @Test
    void createInvoiceForApartment_whenAlreadyExists_throws() {
        Company c = persistCompany("Company");
        Employee e = persistEmployee(c, LocalDate.of(2025, 1, 10));
        Building b = persistBuilding(e);
        Apartment a = persistApartment(b, "Room: 1001", new BigDecimal("70.00"), false);

        YearMonth bm = YearMonth.of(2025, 1);

        service.createInvoiceForApartment(a.getId(), bm);
        assertThrows(IllegalArgumentException.class, () -> service.createInvoiceForApartment(a.getId(), bm));
    }

    @Test
    void createInvoicesForBuilding_createsMissingAndSkipsExisting() {
        Company c = persistCompany("Company");
        Employee e = persistEmployee(c, LocalDate.of(2025, 1, 10));
        Building b = persistBuilding(e);

        Apartment a1 = persistApartment(b, "Room: 1001", new BigDecimal("70.00"), false);
        Apartment a2 = persistApartment(b, "Room: 1002", new BigDecimal("50.00"), false);

        YearMonth bm = YearMonth.of(2025, 3);

        service.createInvoiceForApartment(a1.getId(), bm);
        service.createInvoicesForBuilding(b.getId(), bm);

        List<Invoice> invoices = invoiceDao.getInvoicesByBuildingAndMonth(b.getId(), bm);
        assertNotNull(invoices);
        assertEquals(2, invoices.size());
    }

    @Test
    void getInvoiceById_whenMissing_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getInvoiceById(999999L));
    }

    @Test
    void deleteInvoice_success() {
        Company c = persistCompany("Company");
        Employee e = persistEmployee(c, LocalDate.of(2025, 1, 10));
        Building b = persistBuilding(e);
        Apartment a = persistApartment(b, "Room: 1001", new BigDecimal("70.00"), false);

        YearMonth bm = YearMonth.of(2025, 4);

        service.createInvoiceForApartment(a.getId(), bm);

        Invoice inv = invoiceDao.getInvoiceByApartmentAndMonth(a.getId(), bm);
        assertNotNull(inv);

        InvoiceWithDetailsDto dto = service.getInvoiceById(inv.getId());
        assertNotNull(dto);

        service.deleteInvoice(inv.getId());

        assertNull(invoiceDao.getInvoiceById(inv.getId()));
    }
}
