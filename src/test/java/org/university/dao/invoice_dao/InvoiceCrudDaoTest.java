package org.university.dao.invoice_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.entity.Invoice;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.util.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceCrudDaoTest {

    private static InvoiceCrudDao invoiceCrudDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        invoiceCrudDao = new InvoiceCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Payment").executeUpdate();
            session.createQuery("DELETE FROM Invoice").executeUpdate();
            session.createQuery("DELETE FROM Contract").executeUpdate();
            session.createQuery("DELETE FROM Resident").executeUpdate();
            session.createQuery("DELETE FROM Apartment").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();
            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();

            transaction.commit();
        }
    }

    private Building persistBuilding() {
        Building b = new Building();
        b.setName("Building 1");
        b.setAddress("Address 1");
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

    private Apartment persistApartment(Building b, String number) {
        Apartment a = new Apartment();
        a.setNumber(number);
        a.setArea(new BigDecimal("70"));
        a.setHasPet(false);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Building managedBuilding = session.find(Building.class, b.getId());
            assertNotNull(managedBuilding);

            a.setBuilding(managedBuilding);
            session.persist(a);

            tx.commit();
        }

        assertNotNull(a.getId());
        return a;
    }

    private Invoice createInvoiceForApartment(Apartment a, YearMonth billingMonth) {
        Invoice i = new Invoice();

        Apartment ref = new Apartment();
        ref.setId(a.getId());

        i.setApartment(ref);
        i.setBillingMonth(billingMonth);
        i.setDueDate(billingMonth.atEndOfMonth());
        i.setTotalAmount(new BigDecimal("99.99"));
        i.setPaymentStatus(PaymentStatus.NOT_PAID);

        invoiceCrudDao.createInvoice(i);
        assertNotNull(i.getId());

        return i;
    }

    @Test
    void createInvoice_persistsEntity() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");

        Invoice i = createInvoiceForApartment(a, YearMonth.of(2021, 1));

        assertNotNull(i.getId());
    }

    @Test
    void createInvoice_whenNull_throwsDAOException_withIllegalArgumentCause() {
        assertThrows(DAOException.class, () -> invoiceCrudDao.createInvoice(null));
    }

    @Test
    void getInvoiceById_returnsEntityAndApartment() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");
        Invoice i = createInvoiceForApartment(a, YearMonth.of(2021, 1));

        Invoice found = invoiceCrudDao.getInvoiceById(i.getId());

        assertNotNull(found);
        assertEquals(i.getId(), found.getId());
        assertNotNull(found.getApartment());
        assertEquals(a.getId(), found.getApartment().getId());
    }

    @Test
    void getInvoiceById_whenMissing_returnsNull() {
        Invoice found = invoiceCrudDao.getInvoiceById(999999L);
        assertNull(found);
    }

    @Test
    void getInvoicesByApartment_returnsInvoicesOrderedByBillingMonthDesc() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");

        createInvoiceForApartment(a, YearMonth.of(2025, 1));
        createInvoiceForApartment(a, YearMonth.of(2025, 3));
        createInvoiceForApartment(a, YearMonth.of(2025, 2));

        List<Invoice> invoiceList = invoiceCrudDao.getInvoicesByApartment(a.getId());

        assertNotNull(invoiceList);
        assertEquals(3, invoiceList.size());
        assertEquals(YearMonth.of(2025, 3), invoiceList.get(0).getBillingMonth());
        assertEquals(YearMonth.of(2025, 2), invoiceList.get(1).getBillingMonth());
        assertEquals(YearMonth.of(2025, 1), invoiceList.get(2).getBillingMonth());
    }

    @Test
    void getInvoicesByBuilding_returnsInvoices() {
        Building b = persistBuilding();
        Apartment a1 = persistApartment(b, "Apartment 1");
        Apartment a2 = persistApartment(b, "Apartment 2");

        createInvoiceForApartment(a1, YearMonth.of(2025, 5));
        createInvoiceForApartment(a2, YearMonth.of(2025, 5));
        createInvoiceForApartment(a1, YearMonth.of(2025, 4));

        List<Invoice> invoiceList = invoiceCrudDao.getInvoicesByBuilding(b.getId());

        assertNotNull(invoiceList);
        assertEquals(3, invoiceList.size());
        assertTrue(invoiceList.stream().allMatch(inv -> inv.getApartment() != null));
    }

    @Test
    void getAllInvoices_returnsList() {
        Building b = persistBuilding();
        Apartment a1 = persistApartment(b, "Apartment 1");
        Apartment a2 = persistApartment(b, "Apartment 2");

        createInvoiceForApartment(a1, YearMonth.of(2025, 1));
        createInvoiceForApartment(a2, YearMonth.of(2025, 1));

        List<Invoice> invoiceList = invoiceCrudDao.getAllInvoices();

        assertNotNull(invoiceList);
        assertEquals(2, invoiceList.size());
    }

    @Test
    void getInvoiceByApartmentAndMonth_returnsInvoice() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");
        createInvoiceForApartment(a, YearMonth.of(2021, 1));

        Invoice toFind = createInvoiceForApartment(a, YearMonth.of(2021, 2));

        Invoice found = invoiceCrudDao.getInvoiceByApartmentAndMonth(a.getId(), YearMonth.of(2021, 2));

        assertNotNull(found);
        assertEquals(toFind.getId(), found.getId());
        assertEquals(YearMonth.of(2021, 2), found.getBillingMonth());
    }

    @Test
    void getInvoiceWithDetails_returnsEntity() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");
        Invoice i = createInvoiceForApartment(a, YearMonth.of(2021, 1));

        Invoice found = invoiceCrudDao.getInvoiceWithDetails(i.getId());

        assertNotNull(found);
        assertEquals(i.getId(), found.getId());
        assertNotNull(found.getApartment());
        assertEquals(a.getId(), found.getApartment().getId());
    }

    @Test
    void getInvoicesByBuildingAndMonth_returnsOnlyThatMonth() {
        Building b = persistBuilding();
        Apartment a1 = persistApartment(b, "Apartment 1");
        Apartment a2 = persistApartment(b, "Apartment 2");

        createInvoiceForApartment(a1, YearMonth.of(2021, 1));
        createInvoiceForApartment(a2, YearMonth.of(2021, 2));
        createInvoiceForApartment(a1, YearMonth.of(2021, 2));

        YearMonth targetMonth = YearMonth.of(2021, 2);

        List<Invoice> invoices = invoiceCrudDao.getInvoicesByBuildingAndMonth(b.getId(), targetMonth);

        assertNotNull(invoices);
        assertEquals(2, invoices.size());

        assertTrue(invoices.stream().allMatch(inv -> targetMonth.equals(inv.getBillingMonth())));
        assertTrue(invoices.stream().allMatch(inv -> inv.getApartment() != null));
        assertTrue(invoices.stream().allMatch(inv -> inv.getApartment().getBuilding() != null));
        assertTrue(invoices.stream().allMatch(inv -> b.getId().equals(inv.getApartment().getBuilding().getId())));
    }

    @Test
    void updateInvoice_whenMissing_throwsDAOException() {
        Invoice toUpdate = new Invoice();
        toUpdate.setTotalAmount(new BigDecimal("100.00"));
        assertThrows(NotFoundException.class, () -> invoiceCrudDao.updateInvoice(999999L, toUpdate));
    }

    @Test
    void deleteInvoice_deletesEntity() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");

        Invoice i = createInvoiceForApartment(a, YearMonth.of(2025, 12));

        invoiceCrudDao.deleteInvoice(i.getId());

        Invoice found = invoiceCrudDao.getInvoiceById(i.getId());
        assertNull(found);
    }
}
