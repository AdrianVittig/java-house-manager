package org.university.service.impl.payment_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.dto.PaymentWithDetailsDto;
import org.university.entity.*;
import org.university.util.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCrudServiceImplTest {

    private static PaymentCrudServiceImpl service;
    private static InvoiceCrudDao invoiceDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new PaymentCrudServiceImpl();
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

    private Company persistCompany() {
        Company c = new Company();
        c.setName("C1");
        c.setRevenue(new BigDecimal("1000.00"));
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(c);
            tx.commit();
        }
        return c;
    }

    private Employee persistEmployee(Company c) {
        Employee e = new Employee();
        e.setFirstName("Ivan");
        e.setLastName("Ivanov");
        e.setAge(30);
        e.setFeeCollectingDate(LocalDate.of(2025, 1, 10));
        e.setCompany(c);
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Company mc = session.find(Company.class, c.getId());
            e.setCompany(mc);
            session.persist(e);
            tx.commit();
        }
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
            Employee me = session.find(Employee.class, e.getId());
            b.setEmployee(me);
            session.persist(b);
            tx.commit();
        }
        return b;
    }

    private Apartment persistApartment(Building b) {
        Apartment a = new Apartment();
        a.setNumber("Room: 1001");
        a.setArea(new BigDecimal("70.00"));
        a.setHasPet(false);
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Building mb = session.find(Building.class, b.getId());
            a.setBuilding(mb);
            session.persist(a);
            tx.commit();
        }
        return a;
    }

    private Invoice persistInvoice(Apartment a, YearMonth bm, BigDecimal amount) {
        Invoice i = new Invoice();
        Apartment ref = new Apartment();
        ref.setId(a.getId());
        i.setApartment(ref);
        i.setBillingMonth(bm);
        i.setDueDate(bm.atEndOfMonth());
        i.setTotalAmount(amount);
        i.setPaymentStatus(PaymentStatus.NOT_PAID);
        invoiceDao.createInvoice(i);
        return i;
    }

    @Test
    void createPayment_validation() {
        assertThrows(IllegalArgumentException.class, () -> service.createPayment(null));

        Payment p = new Payment();
        assertThrows(IllegalArgumentException.class, () -> service.createPayment(p));

        p.setInvoice(new Invoice());
        assertThrows(IllegalArgumentException.class, () -> service.createPayment(p));
    }

    @Test
    void createPayment_and_getByInvoiceId_success() {
        Company c = persistCompany();
        Employee e = persistEmployee(c);
        Building b = persistBuilding(e);
        Apartment a = persistApartment(b);

        Invoice inv = persistInvoice(a, YearMonth.of(2025, 1), new BigDecimal("11.11"));

        Payment p = new Payment();
        Invoice ref = new Invoice();
        ref.setId(inv.getId());
        p.setInvoice(ref);

        service.createPayment(p);

        PaymentWithDetailsDto dto = service.getPaymentWithDetailsByInvoiceId(inv.getId());
        assertNotNull(dto);
        assertEquals(inv.getId(), dto.getInvoiceId());
        assertEquals(PaymentStatus.PAID, dto.getPaymentStatus());
    }

    @Test
    void deletePayment_success() {
        Company c = persistCompany();
        Employee e = persistEmployee(c);
        Building b = persistBuilding(e);
        Apartment a = persistApartment(b);

        Invoice inv = persistInvoice(a, YearMonth.of(2025, 1), new BigDecimal("11.11"));

        Payment p = new Payment();
        Invoice ref = new Invoice();
        ref.setId(inv.getId());
        p.setInvoice(ref);

        service.createPayment(p);

        PaymentWithDetailsDto dto = service.getPaymentWithDetailsByInvoiceId(inv.getId());
        assertNotNull(dto);

        service.deletePayment(dto.getId());

        Invoice updated = invoiceDao.getInvoiceById(inv.getId());
        assertNotNull(updated);
        assertEquals(PaymentStatus.NOT_PAID, updated.getPaymentStatus());
    }
}
