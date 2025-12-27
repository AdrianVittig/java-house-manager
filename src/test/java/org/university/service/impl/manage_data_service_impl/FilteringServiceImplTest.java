package org.university.service.impl.manage_data_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.dao.payment_dao.PaymentCrudDao;
import org.university.dto.CompanyRevenueDto;
import org.university.dto.EmployeeBuildingsCountDto;
import org.university.entity.*;
import org.university.util.PaymentStatus;
import org.university.util.ResidentRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilteringServiceImplTest {

    private static FilteringServiceImpl service;
    private static InvoiceCrudDao invoiceDao;
    private static PaymentCrudDao paymentDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new FilteringServiceImpl();
        invoiceDao = new InvoiceCrudDao();
        paymentDao = new PaymentCrudDao();
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
        return c;
    }

    private Employee persistEmployee(Company c, String fn, String ln) {
        Employee e = new Employee();
        e.setFirstName(fn);
        e.setLastName(ln);
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

    private Building persistBuilding(Employee e, String name) {
        Building b = new Building();
        b.setName(name);
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

    private Resident persistResident(Apartment a, String fn, int age) {
        Resident r = new Resident();
        Apartment ref = new Apartment();
        ref.setId(a.getId());
        r.setApartment(ref);
        r.setFirstName(fn);
        r.setLastName("X");
        r.setAge(age);
        r.setUsesElevator(true);
        r.setRole(ResidentRole.OWNER);
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(r);
            tx.commit();
        }
        return r;
    }

    private Invoice persistInvoice(Apartment a, YearMonth bm, BigDecimal amount, PaymentStatus status) {
        Invoice i = new Invoice();
        Apartment ref = new Apartment();
        ref.setId(a.getId());
        i.setApartment(ref);
        i.setBillingMonth(bm);
        i.setDueDate(bm.atEndOfMonth());
        i.setTotalAmount(amount);
        i.setPaymentStatus(status);
        invoiceDao.createInvoice(i);
        return i;
    }

    private void payInvoice(Long invoiceId) {
        Payment p = new Payment();
        Invoice ref = new Invoice();
        ref.setId(invoiceId);
        p.setInvoice(ref);
        paymentDao.createPayment(p);
    }

    @Test
    void filterCompaniesByMinCollectedFees_returnsOnlyAboveThreshold() {
        Company c1 = persistCompany("C1");
        Company c2 = persistCompany("C2");

        Employee e1 = persistEmployee(c1, "A", "A");
        Employee e2 = persistEmployee(c2, "B", "B");

        Building b1 = persistBuilding(e1, "Building");
        Building b2 = persistBuilding(e2, "Building 2");

        Apartment a1 = persistApartment(b1, "Room: 1001");
        Apartment a2 = persistApartment(b2, "Room: 2001");

        Invoice i1 = persistInvoice(a1, YearMonth.of(2025, 1), new BigDecimal("100.00"), PaymentStatus.NOT_PAID);
        Invoice i2 = persistInvoice(a1, YearMonth.of(2025, 2), new BigDecimal("30.00"), PaymentStatus.NOT_PAID);
        Invoice i3 = persistInvoice(a2, YearMonth.of(2025, 1), new BigDecimal("50.00"), PaymentStatus.NOT_PAID);

        payInvoice(i1.getId());
        payInvoice(i2.getId());
        payInvoice(i3.getId());

        List<CompanyRevenueDto> res = service.filterCompaniesByMinCollectedFees(new BigDecimal("100.00"));
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(c1.getId(), res.get(0).getCompanyId());
        assertEquals("C1", res.get(0).getCompanyName());
        assertEquals(0, new BigDecimal("130.00").compareTo(res.get(0).getCollectedFees()));
    }

    @Test
    void filterEmployeesByCompanyName_returnsEmployees() {
        Company c1 = persistCompany("C1");
        Company c2 = persistCompany("C2");

        persistEmployee(c1, "Ivan", "Ivanov");
        persistEmployee(c1, "Georgi", "Georgiev");
        persistEmployee(c2, "Petar", "Petrov");

        List<Employee> res = service.filterEmployeesByCompanyName("C1");
        assertNotNull(res);
        assertEquals(2, res.size());
    }

    @Test
    void filterEmployeesByCompanyWithMinBuildings_returnsDtos() {
        Company c = persistCompany("C1");
        Employee e1 = persistEmployee(c, "A", "A");
        Employee e2 = persistEmployee(c, "B", "B");

        persistBuilding(e1, "Building");
        persistBuilding(e1, "Building 2");
        persistBuilding(e2, "Building 3");

        List<EmployeeBuildingsCountDto> res = service.filterEmployeesByCompanyWithMinBuildings(c.getId(), 2);
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(e1.getId(), res.get(0).getEmployeeId());
        assertEquals(2L, res.get(0).getBuildingsCount());
    }

    @Test
    void filterResidentsByBuildingByName_and_byAge() {
        Company c = persistCompany("C1");
        Employee e = persistEmployee(c, "A", "A");
        Building b = persistBuilding(e, "Building");
        Apartment a = persistApartment(b, "Room: 1001");

        persistResident(a, "Ivan", 25);
        persistResident(a, "Ivana", 30);
        persistResident(a, "Petar", 40);

        List<Resident> byName = service.filterResidentsByBuildingByName(b.getId(), "Iva");
        assertEquals(2, byName.size());

        List<Resident> byAge = service.filterResidentsByBuildingByAge(b.getId(), 26, 40);
        assertEquals(2, byAge.size());
        assertTrue(byAge.stream().allMatch(r -> r.getAge() >= 26 && r.getAge() <= 40));
    }
}
