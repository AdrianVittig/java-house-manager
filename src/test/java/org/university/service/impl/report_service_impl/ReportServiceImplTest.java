package org.university.service.impl.report_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.dao.payment_dao.PaymentCrudDao;
import org.university.dto.BuildingAmountDto;
import org.university.dto.CompanyAmountDto;
import org.university.dto.EmployeeAmountDto;
import org.university.dto.EmployeeBuildingsCountDto;
import org.university.entity.*;
import org.university.util.PaymentStatus;
import org.university.util.ResidentRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceImplTest {

    private static ReportServiceImpl service;
    private static InvoiceCrudDao invoiceDao;
    private static PaymentCrudDao paymentDao;

    @BeforeAll
    static void init() {
        SessionFactoryUtil.getSessionFactory();
        service = new ReportServiceImpl();
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
        c.setRevenue(new BigDecimal("250000.00"));
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(c);
            tx.commit();
        }
        return c;
    }

    private Employee persistEmployee(Company c, String firstName, String lastName) {
        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setAge(30);
        e.setFeeCollectingDate(LocalDate.of(2025, 1, 15));
        e.setCompany(c);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(e);
            tx.commit();
        }
        return e;
    }

    private Building persistBuilding(Employee e, String name, String address) {
        Building b = new Building();
        b.setName(name);
        b.setAddress(address);
        b.setBuiltUpArea(new BigDecimal("1800.00"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.18"));
        b.setCountOfFloors(6);
        b.setApartmentsPerFloor(3);
        b.setBuiltDate(LocalDate.now().minusYears(10));

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Employee managed = session.find(Employee.class, e.getId());
            b.setEmployee(managed);
            session.persist(b);
            tx.commit();
        }
        return b;
    }

    private Apartment persistApartment(Building b, String number, BigDecimal area) {
        Apartment a = new Apartment();
        a.setNumber(number);
        a.setArea(area);
        a.setHasPet(false);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Building managed = session.find(Building.class, b.getId());
            a.setBuilding(managed);
            session.persist(a);
            tx.commit();
        }
        return a;
    }

    private void persistResident(Apartment a, String firstName, String lastName, int age) {
        Resident r = new Resident();
        r.setFirstName(firstName);
        r.setLastName(lastName);
        r.setAge(age);
        r.setUsesElevator(true);
        r.setRole(ResidentRole.OWNER);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Apartment managed = session.find(Apartment.class, a.getId());
            r.setApartment(managed);
            session.persist(r);
            tx.commit();
        }
    }

    private Invoice persistInvoice(Apartment a, YearMonth month, BigDecimal amount, PaymentStatus status) {
        Invoice i = new Invoice();
        Apartment ref = new Apartment();
        ref.setId(a.getId());
        i.setApartment(ref);
        i.setBillingMonth(month);
        i.setDueDate(month.atEndOfMonth());
        i.setTotalAmount(amount);
        i.setPaymentStatus(status);
        invoiceDao.createInvoice(i);
        return i;
    }

    private void pay(Long invoiceId) {
        Payment p = new Payment();
        Invoice inv = new Invoice();
        inv.setId(invoiceId);
        p.setInvoice(inv);
        paymentDao.createPayment(p);
    }

    @Test
    void getBuildingsCountByEmployeesForCompany_success() {
        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Georgi", "Georgiev");
        Employee e2 = persistEmployee(c1, "Maria", "Ivanov");

        persistBuilding(e1, "Building 1", "Address 1");
        persistBuilding(e2, "Building 2", "Address 2");

        List<EmployeeBuildingsCountDto> list = service.getBuildingsCountByEmployeesForCompany(c1.getId());

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(d -> d.getEmployeeId().equals(e1.getId()) && d.getBuildingsCount() == 1));
        assertTrue(list.stream().anyMatch(d -> d.getEmployeeId().equals(e2.getId()) && d.getBuildingsCount() == 1));
    }

    @Test
    void getBuildingsCountByEmployeesForCompany_whenCompanyIdNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getBuildingsCountByEmployeesForCompany(null));
    }

    @Test
    void getCountBuildingsByEmployee_success() {
        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Georgi", "Georgiev");

        persistBuilding(e1, "Building 1", "Address 1");
        persistBuilding(e1, "Building 2", "Address 2");

        long count = service.getCountBuildingsByEmployee(e1.getId());

        assertEquals(2L, count);
    }

    @Test
    void getCountBuildingsByEmployee_whenEmployeeIdNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getCountBuildingsByEmployee(null));
    }

    @Test
    void getBuildingsByEmployee_success() {
        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Maria", "Ivanov");

        persistBuilding(e1, "Building 1", "Address 1");
        persistBuilding(e1, "Building 2", "Address 2");

        List<Building> list = service.getBuildingsByEmployee(e1.getId());

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(b -> "Building 1".equals(b.getName())));
        assertTrue(list.stream().anyMatch(b -> "Building 2".equals(b.getName())));
    }

    @Test
    void getBuildingsByEmployee_whenEmployeeIdNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getBuildingsByEmployee(null));
    }

    @Test
    void countApartmentsByBuilding_success() {
        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Petar", "Petrov");
        Building b1 = persistBuilding(e1, "Building 1", "Address 1");

        persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));
        persistApartment(b1, "Apartment 2", new BigDecimal("60.00"));
        persistApartment(b1, "Apartment 3", new BigDecimal("70.00"));

        long count = service.countApartmentsByBuilding(b1.getId());

        assertEquals(3L, count);
    }

    @Test
    void countApartmentsByBuilding_whenBuildingIdNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.countApartmentsByBuilding(null));
    }

    @Test
    void getApartmentsByBuilding_success() {
        Company c1 = persistCompany("Company 2");
        Employee e1 = persistEmployee(c1, "Ivan", "Ivanov");
        Building b1 = persistBuilding(e1, "Building 1", "Address 1");

        persistApartment(b1, "Apartment 1", new BigDecimal("55.00"));
        persistApartment(b1, "Apartment 2", new BigDecimal("65.00"));

        List<Apartment> list = service.getApartmentsByBuilding(b1.getId());

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(a -> "Apartment 1".equals(a.getNumber())));
        assertTrue(list.stream().anyMatch(a -> "Apartment 2".equals(a.getNumber())));
    }

    @Test
    void getApartmentsByBuilding_whenBuildingIdNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getApartmentsByBuilding(null));
    }

    @Test
    void countResidentsByBuilding_success() {
        Company c1 = persistCompany("Company 3");
        Employee e1 = persistEmployee(c1, "Yordan", "Georgiev");
        Building b1 = persistBuilding(e1, "Building 1", "Address 1");
        Apartment a1 = persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));
        Apartment a2 = persistApartment(b1, "Apartment 2", new BigDecimal("60.00"));

        persistResident(a1, "Georgi", "Georgiev", 30);
        persistResident(a1, "Maria", "Ivanov", 25);
        persistResident(a2, "Petar", "Petrov", 35);

        long count = service.countResidentsByBuilding(b1.getId());

        assertEquals(3L, count);
    }

    @Test
    void countResidentsByBuilding_whenBuildingIdNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.countResidentsByBuilding(null));
    }

    @Test
    void getResidentsByBuilding_success() {
        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Georgi", "Georgiev");
        Building b1 = persistBuilding(e1, "Building 1", "Address 1");
        Apartment a1 = persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));

        persistResident(a1, "Maria", "Ivanov", 25);
        persistResident(a1, "Petar", "Petrov", 35);

        List<Resident> list = service.getResidentsByBuilding(b1.getId());

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(r -> "Maria".equals(r.getFirstName()) && "Ivanov".equals(r.getLastName())));
        assertTrue(list.stream().anyMatch(r -> "Petar".equals(r.getFirstName()) && "Petrov".equals(r.getLastName())));
    }

    @Test
    void getResidentsByBuilding_whenBuildingIdNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getResidentsByBuilding(null));
    }

    @Test
    void getAmountsToPayByCompany_success() {
        YearMonth month = YearMonth.of(2025, 1);

        Company c1 = persistCompany("Company 1");
        Company c2 = persistCompany("Company 2");

        Employee e1 = persistEmployee(c1, "Georgi", "Georgiev");
        Employee e2 = persistEmployee(c2, "Maria", "Ivanov");

        Building b1 = persistBuilding(e1, "Building 1", "Address 1");
        Building b2 = persistBuilding(e2, "Building 2", "Address 2");

        Apartment a1 = persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));
        Apartment a2 = persistApartment(b2, "Apartment 2", new BigDecimal("60.00"));

        persistInvoice(a1, month, new BigDecimal("100.00"), PaymentStatus.NOT_PAID);
        persistInvoice(a2, month, new BigDecimal("300.00"), PaymentStatus.PAID);

        List<CompanyAmountDto> list = service.getAmountsToPayByCompany(month);

        CompanyAmountDto company1 = list.stream().filter(x -> "Company 1".equals(x.getCompanyName())).findFirst().orElseThrow();
        CompanyAmountDto company2 = list.stream().filter(x -> "Company 2".equals(x.getCompanyName())).findFirst().orElseThrow();

        assertEquals(0, company1.getAmount().compareTo(new BigDecimal("100.00")));
        assertEquals(0, company2.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(list.size(), service.countCompaniesForAmountsToPay(month));
    }

    @Test
    void getAmountsToPayByCompany_whenBillingMonthNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getAmountsToPayByCompany(null));
    }

    @Test
    void getAmountsToPayByBuilding_success() {
        YearMonth month = YearMonth.of(2025, 1);

        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Petar", "Petrov");

        Building b1 = persistBuilding(e1, "Building 1", "Address 1");
        Building b2 = persistBuilding(e1, "Building 2", "Address 2");

        Apartment a1 = persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));
        Apartment a2 = persistApartment(b2, "Apartment 2", new BigDecimal("60.00"));

        persistInvoice(a1, month, new BigDecimal("120.00"), PaymentStatus.NOT_PAID);
        persistInvoice(a2, month, new BigDecimal("80.00"), PaymentStatus.PAID);

        List<BuildingAmountDto> list = service.getAmountsToPayByBuilding(month);

        BuildingAmountDto building1 = list.stream().filter(x -> "Building 1".equals(x.getBuildingName())).findFirst().orElseThrow();
        BuildingAmountDto building2 = list.stream().filter(x -> "Building 2".equals(x.getBuildingName())).findFirst().orElseThrow();

        assertEquals(0, building1.getAmount().compareTo(new BigDecimal("120.00")));
        assertEquals(0, building2.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(list.size(), service.countBuildingsForAmountsToPay(month));
    }

    @Test
    void getAmountsToPayByBuilding_whenBillingMonthNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getAmountsToPayByBuilding(null));
    }

    @Test
    void getAmountsToPayByEmployee_success() {
        YearMonth month = YearMonth.of(2025, 1);

        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Georgi", "Georgiev");
        Employee e2 = persistEmployee(c1, "Maria", "Ivanov");

        Building b1 = persistBuilding(e1, "Building 1", "Address 1");
        Building b2 = persistBuilding(e2, "Building 2", "Address 2");

        Apartment a1 = persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));
        Apartment a2 = persistApartment(b2, "Apartment 2", new BigDecimal("60.00"));

        persistInvoice(a1, month, new BigDecimal("10.00"), PaymentStatus.NOT_PAID);
        persistInvoice(a2, month, new BigDecimal("20.00"), PaymentStatus.PAID);

        List<EmployeeAmountDto> list = service.getAmountsToPayByEmployee(month);

        EmployeeAmountDto emp1 = list.stream().filter(x -> x.getEmployeeId().equals(e1.getId())).findFirst().orElseThrow();
        EmployeeAmountDto emp2 = list.stream().filter(x -> x.getEmployeeId().equals(e2.getId())).findFirst().orElseThrow();

        assertEquals(0, emp1.getAmount().compareTo(new BigDecimal("10.00")));
        assertEquals(0, emp2.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(list.size(), service.countEmployeesForAmountsToPay(month));
    }

    @Test
    void getAmountsToPayByEmployee_whenBillingMonthNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getAmountsToPayByEmployee(null));
    }

    @Test
    void getPaidAmountsByCompany_success() {
        YearMonth month = YearMonth.of(2025, 1);

        Company c1 = persistCompany("Company 1");
        Company c2 = persistCompany("Company 2");

        Employee e1 = persistEmployee(c1, "Georgi", "Georgiev");
        Employee e2 = persistEmployee(c2, "Maria", "Ivanov");

        Building b1 = persistBuilding(e1, "Building 1", "Address 1");
        Building b2 = persistBuilding(e2, "Building 2", "Address 2");

        Apartment a1 = persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));
        Apartment a2 = persistApartment(b2, "Apartment 2", new BigDecimal("60.00"));

        Invoice i1 = persistInvoice(a1, month, new BigDecimal("100.00"), PaymentStatus.NOT_PAID);
        Invoice i2 = persistInvoice(a2, month, new BigDecimal("300.00"), PaymentStatus.NOT_PAID);

        pay(i2.getId());

        List<CompanyAmountDto> list = service.getPaidAmountsByCompany(month);

        CompanyAmountDto company1 = list.stream().filter(x -> "Company 1".equals(x.getCompanyName())).findFirst().orElseThrow();
        CompanyAmountDto company2 = list.stream().filter(x -> "Company 2".equals(x.getCompanyName())).findFirst().orElseThrow();

        assertEquals(0, company1.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(0, company2.getAmount().compareTo(new BigDecimal("300.00")));
        assertEquals(list.size(), service.countCompaniesForPaidAmounts(month));
    }

    @Test
    void getPaidAmountsByCompany_whenBillingMonthNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getPaidAmountsByCompany(null));
    }

    @Test
    void getPaidAmountsByBuilding_success() {
        YearMonth month = YearMonth.of(2025, 1);

        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Petar", "Petrov");

        Building b1 = persistBuilding(e1, "Building 1", "Address 1");
        Building b2 = persistBuilding(e1, "Building 2", "Address 2");

        Apartment a1 = persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));
        Apartment a2 = persistApartment(b2, "Apartment 2", new BigDecimal("60.00"));

        Invoice i1 = persistInvoice(a1, month, new BigDecimal("120.00"), PaymentStatus.NOT_PAID);
        Invoice i2 = persistInvoice(a2, month, new BigDecimal("80.00"), PaymentStatus.NOT_PAID);

        pay(i1.getId());

        List<BuildingAmountDto> list = service.getPaidAmountsByBuilding(month);

        BuildingAmountDto building1 = list.stream().filter(x -> "Building 1".equals(x.getBuildingName())).findFirst().orElseThrow();
        BuildingAmountDto building2 = list.stream().filter(x -> "Building 2".equals(x.getBuildingName())).findFirst().orElseThrow();

        assertEquals(0, building1.getAmount().compareTo(new BigDecimal("120.00")));
        assertEquals(0, building2.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(list.size(), service.countBuildingsForPaidAmounts(month));
    }

    @Test
    void getPaidAmountsByBuilding_whenBillingMonthNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getPaidAmountsByBuilding(null));
    }

    @Test
    void getPaidAmountsByEmployee_success() {
        YearMonth month = YearMonth.of(2025, 1);

        Company c1 = persistCompany("Company 1");
        Employee e1 = persistEmployee(c1, "Georgi", "Georgiev");
        Employee e2 = persistEmployee(c1, "Maria", "Ivanov");

        Building b1 = persistBuilding(e1, "Building 1", "Address 1");
        Building b2 = persistBuilding(e2, "Building 2", "Address 2");

        Apartment a1 = persistApartment(b1, "Apartment 1", new BigDecimal("50.00"));
        Apartment a2 = persistApartment(b2, "Apartment 2", new BigDecimal("60.00"));

        Invoice i1 = persistInvoice(a1, month, new BigDecimal("10.00"), PaymentStatus.NOT_PAID);
        Invoice i2 = persistInvoice(a2, month, new BigDecimal("20.00"), PaymentStatus.NOT_PAID);

        pay(i1.getId());

        List<EmployeeAmountDto> list = service.getPaidAmountsByEmployee(month);

        EmployeeAmountDto emp1 = list.stream().filter(x -> x.getEmployeeId().equals(e1.getId())).findFirst().orElseThrow();
        EmployeeAmountDto emp2 = list.stream().filter(x -> x.getEmployeeId().equals(e2.getId())).findFirst().orElseThrow();

        assertEquals(0, emp1.getAmount().compareTo(new BigDecimal("10.00")));
        assertEquals(0, emp2.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(list.size(), service.countEmployeesForPaidAmounts(month));
    }

    @Test
    void getPaidAmountsByEmployee_whenBillingMonthNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getPaidAmountsByEmployee(null));
    }
}
