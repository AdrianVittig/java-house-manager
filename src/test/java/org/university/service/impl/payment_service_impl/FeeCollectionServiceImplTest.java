package org.university.service.impl.payment_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.dao.payment_dao.PaymentCrudDao;
import org.university.dto.FileDto;
import org.university.dto.PaymentWithDetailsDto;
import org.university.entity.*;
import org.university.service.impl.file_manage_service_impl.FileServiceImpl;
import org.university.util.PaymentStatus;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeeCollectionServiceImplTest {

    private static FeeCollectionServiceImpl service;
    private static InvoiceCrudDao invoiceDao;
    private static PaymentCrudDao paymentDao;
    private static FileServiceImpl fileService;

    private final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    private final java.util.List<File> createdFiles = new java.util.ArrayList<>();

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new FeeCollectionServiceImpl();
        invoiceDao = new InvoiceCrudDao();
        paymentDao = new PaymentCrudDao();
        fileService = new FileServiceImpl();
    }

    @AfterEach
    void cleanup() {
        for (File f : createdFiles) {
            try {
                if (f != null && f.exists()) Files.deleteIfExists(f.toPath());
            } catch (Exception ignored) {
            }
        }
        createdFiles.clear();

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

    private String dirPath() {
        try {
            Field f = FileServiceImpl.class.getDeclaredField("DIR_PATH");
            f.setAccessible(true);
            return (String) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String fullPath(Long invoiceId, YearMonth billingMonth, Long buildingId) {
        return dirPath() + "\\" + "Paid Invoice - "
                + " Invoice#" + invoiceId
                + " - Building#" + buildingId
                + " - (" + billingMonth + ")"
                + " - " + ".ser";
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

    private Apartment persistApartment(Building b, String number) {
        Apartment a = new Apartment();
        a.setNumber(number);
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
    void payInvoice_success_createsPayment_updatesInvoice_andWritesFile() throws Exception {
        Assumptions.assumeTrue(isWindows);

        Files.createDirectories(Path.of(dirPath()));

        Company c = persistCompany("C1");
        Employee e = persistEmployee(c);
        Building b = persistBuilding(e);
        Apartment a = persistApartment(b, "Room: 1001");

        YearMonth bm = YearMonth.of(2025, 1);
        Invoice inv = persistInvoice(a, bm, new BigDecimal("99.99"));

        PaymentWithDetailsDto dto = service.payInvoice(inv.getId());
        assertNotNull(dto);

        Payment p = paymentDao.getPaymentByInvoiceId(inv.getId());
        assertNotNull(p);
        assertEquals(PaymentStatus.PAID, p.getPaymentStatus());

        Invoice updated = invoiceDao.getInvoiceWithDetails(inv.getId());
        assertNotNull(updated);
        assertEquals(PaymentStatus.PAID, updated.getPaymentStatus());

        File f = new File(fullPath(inv.getId(), bm, b.getId()));
        createdFiles.add(f);
        assertTrue(f.exists());

        FileDto read = fileService.readFile(inv.getId(), bm, b.getId());
        assertNotNull(read);
        assertEquals(inv.getId(), read.getInvoiceId());
        assertEquals(b.getId(), read.getBuildingId());
        assertEquals(bm, read.getBillingMonth());
    }

    @Test
    void payInvoice_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.payInvoice(null));
    }

    @Test
    void collectFeesForBuilding_createsInvoicesAndPaysAll() throws Exception {
        Assumptions.assumeTrue(isWindows);

        Files.createDirectories(Path.of(dirPath()));

        Company c = persistCompany("C1");
        Employee e = persistEmployee(c);
        Building b = persistBuilding(e);
        Apartment a1 = persistApartment(b, "Room: 1001");
        Apartment a2 = persistApartment(b, "Room: 1002");

        YearMonth bm = YearMonth.of(2025, 2);

        service.collectFeesForBuilding(b.getId(), bm);

        List<Invoice> invs = invoiceDao.getInvoicesByBuildingAndMonth(b.getId(), bm);
        assertNotNull(invs);
        assertEquals(2, invs.size());
        assertTrue(invs.stream().allMatch(i -> i.getPaymentStatus() == PaymentStatus.PAID));

        List<Payment> pays = paymentDao.getPaymentsByBuildingAndMonth(b.getId(), bm);
        assertNotNull(pays);
        assertEquals(2, pays.size());

        for (Invoice i : invs) {
            createdFiles.add(new File(fullPath(i.getId(), bm, b.getId())));
        }
    }
}
