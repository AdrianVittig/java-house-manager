package org.university.dao.payment_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.entity.*;
import org.university.exception.DAOException;
import org.university.util.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCrudDaoTest {

    private static PaymentCrudDao paymentCrudDao;
    private static InvoiceCrudDao invoiceCrudDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        paymentCrudDao = new PaymentCrudDao();
        invoiceCrudDao = new InvoiceCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Payment").executeUpdate();
            session.createQuery("DELETE FROM Invoice").executeUpdate();
            session.createQuery("DELETE FROM Resident").executeUpdate();
            session.createQuery("DELETE FROM Apartment").executeUpdate();
            session.createQuery("DELETE FROM Contract").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();
            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();

            transaction.commit();
        }
    }

    private Building persistBuilding() {
        Building building = new Building();
        building.setName("Test Building");
        building.setAddress("Test Address");
        building.setBuiltUpArea(new BigDecimal("120"));
        building.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        building.setCountOfFloors(3);
        building.setApartmentsPerFloor(2);
        building.setBuiltDate(LocalDate.now());

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(building);
            transaction.commit();
        }

        assertNotNull(building.getId());
        return building;
    }

    private Apartment persistApartment(Building building, String number) {
        Apartment apartment = new Apartment();
        apartment.setNumber(number);
        apartment.setArea(new BigDecimal("70"));
        apartment.setHasPet(false);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Building managedBuilding = session.find(Building.class, building.getId());
            assertNotNull(managedBuilding);

            apartment.setBuilding(managedBuilding);
            session.persist(apartment);

            transaction.commit();
        }

        assertNotNull(apartment.getId());
        return apartment;
    }

    private Invoice persistInvoice(Apartment apartment, YearMonth billingMonth, BigDecimal totalAmount, PaymentStatus paymentStatus) {
        Invoice invoice = new Invoice();

        Apartment apartmentRef = new Apartment();
        apartmentRef.setId(apartment.getId());

        invoice.setApartment(apartmentRef);
        invoice.setBillingMonth(billingMonth);
        invoice.setDueDate(billingMonth.atEndOfMonth());
        invoice.setTotalAmount(totalAmount);
        invoice.setPaymentStatus(paymentStatus);

        invoiceCrudDao.createInvoice(invoice);
        assertNotNull(invoice.getId());

        return invoice;
    }

    private Payment persistPaymentForInvoice(Long invoiceId) {
        Payment payment = new Payment();
        Invoice invoiceRef = new Invoice();
        invoiceRef.setId(invoiceId);
        payment.setInvoice(invoiceRef);

        paymentCrudDao.createPayment(payment);
        assertNotNull(payment.getId());
        return payment;
    }

    private Company persistCompany() {
        Company company = new Company();
        company.setName("Test Company");
        company.setRevenue(new BigDecimal("100000"));

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(company);
            transaction.commit();
        }

        assertNotNull(company.getId());
        return company;
    }

    private Employee persistEmployee(Company company) {
        Employee employee = new Employee();
        employee.setFirstName("Ivan");
        employee.setLastName("Ivanov");
        employee.setAge(30);
        employee.setFeeCollectingDate(LocalDate.now());
        employee.setCompany(company);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(employee);
            transaction.commit();
        }

        assertNotNull(employee.getId());
        return employee;
    }

    private void assignEmployeeToBuilding(Building building, Employee employee) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Building managedBuilding = session.find(Building.class, building.getId());
            Employee managedEmployee = session.find(Employee.class, employee.getId());

            assertNotNull(managedBuilding);
            assertNotNull(managedEmployee);

            managedBuilding.setEmployee(managedEmployee);

            transaction.commit();
        }
    }

    @Test
    void createPayment_persistsAndUpdatesInvoiceToPaid() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Invoice invoice = persistInvoice(apartment, YearMonth.of(2025, 1), new BigDecimal("99.99"), PaymentStatus.NOT_PAID);

        Payment payment = persistPaymentForInvoice(invoice.getId());

        Payment foundPayment = paymentCrudDao.getPaymentById(payment.getId());
        assertNotNull(foundPayment);
        assertNotNull(foundPayment.getInvoice());
        assertEquals(invoice.getId(), foundPayment.getInvoice().getId());
        assertEquals(PaymentStatus.PAID, foundPayment.getPaymentStatus());
        assertEquals(0, foundPayment.getAmount().compareTo(new BigDecimal("99.99")));
        assertNotNull(foundPayment.getPaidAt());

        Invoice updatedInvoice = invoiceCrudDao.getInvoiceWithDetails(invoice.getId());
        assertNotNull(updatedInvoice);
        assertEquals(PaymentStatus.PAID, updatedInvoice.getPaymentStatus());
        assertNotNull(updatedInvoice.getPayment());
        assertEquals(foundPayment.getId(), updatedInvoice.getPayment().getId());
    }

    @Test
    void createPayment_whenNull_throwsDAOException() {
        assertThrows(DAOException.class, () -> paymentCrudDao.createPayment(null));
    }

    @Test
    void createPayment_whenInvoiceNull_throwsDAOException() {
        Payment payment = new Payment();
        assertThrows(DAOException.class, () -> paymentCrudDao.createPayment(payment));
    }

    @Test
    void createPayment_whenInvoiceMissing_throwsDAOException() {
        Payment payment = new Payment();
        Invoice invoiceRef = new Invoice();
        invoiceRef.setId(999999L);
        payment.setInvoice(invoiceRef);

        assertThrows(DAOException.class, () -> paymentCrudDao.createPayment(payment));
    }

    @Test
    void createPayment_whenInvoiceAlreadyPaid_throwsDAOException() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Invoice invoice = persistInvoice(apartment, YearMonth.of(2025, 2), new BigDecimal("10.00"), PaymentStatus.PAID);

        Payment payment = new Payment();
        Invoice invoiceRef = new Invoice();
        invoiceRef.setId(invoice.getId());
        payment.setInvoice(invoiceRef);

        assertThrows(DAOException.class, () -> paymentCrudDao.createPayment(payment));
    }

    @Test
    void createPayment_whenInvoiceAlreadyHasPayment_throwsDAOException() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Invoice invoice = persistInvoice(apartment, YearMonth.of(2025, 3), new BigDecimal("20.00"), PaymentStatus.NOT_PAID);

        persistPaymentForInvoice(invoice.getId());

        Payment secondPayment = new Payment();
        Invoice invoiceRef = new Invoice();
        invoiceRef.setId(invoice.getId());
        secondPayment.setInvoice(invoiceRef);

        assertThrows(DAOException.class, () -> paymentCrudDao.createPayment(secondPayment));
    }

    @Test
    void getPaymentById_whenMissing_returnsNull() {
        assertNull(paymentCrudDao.getPaymentById(999999L));
    }

    @Test
    void getPaymentById_returnsPaymentWithInvoiceAndApartment() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Invoice invoice = persistInvoice(apartment, YearMonth.of(2025, 4), new BigDecimal("11.11"), PaymentStatus.NOT_PAID);

        Payment payment = persistPaymentForInvoice(invoice.getId());

        Payment foundPayment = paymentCrudDao.getPaymentById(payment.getId());

        assertNotNull(foundPayment);
        assertEquals(payment.getId(), foundPayment.getId());
        assertNotNull(foundPayment.getInvoice());
        assertEquals(invoice.getId(), foundPayment.getInvoice().getId());
        assertNotNull(foundPayment.getInvoice().getApartment());
        assertEquals(apartment.getId(), foundPayment.getInvoice().getApartment().getId());
    }

    @Test
    void getPaymentByInvoiceId_returnsPayment() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Invoice invoice = persistInvoice(apartment, YearMonth.of(2025, 5), new BigDecimal("33.33"), PaymentStatus.NOT_PAID);

        Company company = persistCompany();
        Employee employee = persistEmployee(company);
        assignEmployeeToBuilding(building, employee);

        Payment payment = persistPaymentForInvoice(invoice.getId());

        Payment foundPayment = paymentCrudDao.getPaymentByInvoiceId(invoice.getId());

        assertNotNull(foundPayment);
        assertEquals(payment.getId(), foundPayment.getId());
        assertNotNull(foundPayment.getInvoice());
        assertEquals(invoice.getId(), foundPayment.getInvoice().getId());
        assertNotNull(foundPayment.getInvoice().getApartment());
        assertNotNull(foundPayment.getInvoice().getApartment().getBuilding());
        assertNotNull(foundPayment.getInvoice().getApartment().getBuilding().getEmployee());
        assertNotNull(foundPayment.getInvoice().getApartment().getBuilding().getEmployee().getCompany());
        assertEquals(company.getId(), foundPayment.getInvoice().getApartment().getBuilding().getEmployee().getCompany().getId());
    }

    @Test
    void getPaymentsByApartmentId_returnsPayments() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");

        Invoice invoice1 = persistInvoice(apartment, YearMonth.of(2025, 6), new BigDecimal("10.00"), PaymentStatus.NOT_PAID);
        Invoice invoice2 = persistInvoice(apartment, YearMonth.of(2025, 7), new BigDecimal("20.00"), PaymentStatus.NOT_PAID);

        persistPaymentForInvoice(invoice1.getId());
        persistPaymentForInvoice(invoice2.getId());

        List<Payment> paymentList = paymentCrudDao.getPaymentsByApartmentId(apartment.getId());

        assertNotNull(paymentList);
        assertEquals(2, paymentList.size());
        assertTrue(paymentList.get(0).getPaidAt().compareTo(paymentList.get(1).getPaidAt()) >= 0);

        Set<Long> invoiceIds = paymentList.stream().map(payment -> payment.getInvoice().getId()).collect(Collectors.toSet());
        assertTrue(invoiceIds.contains(invoice1.getId()));
        assertTrue(invoiceIds.contains(invoice2.getId()));
    }

    @Test
    void getPaymentsByBuildingId_returnsOnlyPaymentsForThatBuilding() {
        Building building1 = persistBuilding();
        Apartment apartment1 = persistApartment(building1, "1001");
        Apartment apartment2 = persistApartment(building1, "1002");

        Building building2 = persistBuilding();
        Apartment apartment3 = persistApartment(building2, "1003");

        Invoice invoice1 = persistInvoice(apartment1, YearMonth.of(2025, 8), new BigDecimal("10.00"), PaymentStatus.NOT_PAID);
        Invoice invoice2 = persistInvoice(apartment2, YearMonth.of(2025, 8), new BigDecimal("15.00"), PaymentStatus.NOT_PAID);
        Invoice invoice3 = persistInvoice(apartment3, YearMonth.of(2025, 8), new BigDecimal("99.00"), PaymentStatus.NOT_PAID);

        persistPaymentForInvoice(invoice1.getId());
        persistPaymentForInvoice(invoice2.getId());
        persistPaymentForInvoice(invoice3.getId());

        List<Payment> paymentList = paymentCrudDao.getPaymentsByBuildingId(building1.getId());

        assertNotNull(paymentList);
        assertEquals(2, paymentList.size());

        for (Payment payment : paymentList) {
            Invoice invoice = invoiceCrudDao.getInvoiceById(payment.getInvoice().getId());
            assertNotNull(invoice);
            assertNotNull(invoice.getApartment());
            assertNotNull(invoice.getApartment().getBuilding());
            assertEquals(building1.getId(), invoice.getApartment().getBuilding().getId());
        }
    }

    @Test
    void getPaymentsByBuildingAndMonth_returnsOnlyThatMonth() {
        Building building = persistBuilding();
        Apartment apartment1 = persistApartment(building, "1001");
        Apartment apartment2 = persistApartment(building, "1002");

        Invoice invoiceJan = persistInvoice(apartment1, YearMonth.of(2025, 1), new BigDecimal("10.00"), PaymentStatus.NOT_PAID);
        Invoice invoiceFeb1 = persistInvoice(apartment1, YearMonth.of(2025, 2), new BigDecimal("20.00"), PaymentStatus.NOT_PAID);
        Invoice invoiceFeb2 = persistInvoice(apartment2, YearMonth.of(2025, 2), new BigDecimal("30.00"), PaymentStatus.NOT_PAID);

        persistPaymentForInvoice(invoiceJan.getId());
        persistPaymentForInvoice(invoiceFeb1.getId());
        persistPaymentForInvoice(invoiceFeb2.getId());

        List<Payment> paymentList = paymentCrudDao.getPaymentsByBuildingAndMonth(building.getId(), YearMonth.of(2025, 2));

        assertNotNull(paymentList);
        assertEquals(2, paymentList.size());

        for (Payment payment : paymentList) {
            Invoice invoice = invoiceCrudDao.getInvoiceById(payment.getInvoice().getId());
            assertNotNull(invoice);
            assertEquals(YearMonth.of(2025, 2), invoice.getBillingMonth());
        }
    }

    @Test
    void getAllPayments_returnsPayments() {
        Building building = persistBuilding();
        Apartment apartment1 = persistApartment(building, "1001");
        Apartment apartment2 = persistApartment(building, "1002");

        Invoice invoice1 = persistInvoice(apartment1, YearMonth.of(2025, 9), new BigDecimal("10.00"), PaymentStatus.NOT_PAID);
        Invoice invoice2 = persistInvoice(apartment2, YearMonth.of(2025, 10), new BigDecimal("20.00"), PaymentStatus.NOT_PAID);

        persistPaymentForInvoice(invoice1.getId());
        persistPaymentForInvoice(invoice2.getId());

        List<Payment> paymentList = paymentCrudDao.getAllPayments();

        assertNotNull(paymentList);
        assertEquals(2, paymentList.size());
        assertTrue(paymentList.get(0).getPaidAt().compareTo(paymentList.get(1).getPaidAt()) >= 0);
    }

    @Test
    void updatePayment_updatesOnlyNonNullFields() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Invoice invoice = persistInvoice(apartment, YearMonth.of(2025, 11), new BigDecimal("55.55"), PaymentStatus.NOT_PAID);

        Payment payment = persistPaymentForInvoice(invoice.getId());
        Payment beforePayment = paymentCrudDao.getPaymentById(payment.getId());
        assertNotNull(beforePayment);

        LocalDateTime newPaidAt = LocalDateTime.now().minusDays(3).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);

        Payment patchPayment = new Payment();
        patchPayment.setAmount(new BigDecimal("123.45"));
        patchPayment.setPaidAt(newPaidAt);
        patchPayment.setPaymentStatus(null);

        paymentCrudDao.updatePayment(payment.getId(), patchPayment);

        Payment updatedPayment = paymentCrudDao.getPaymentById(payment.getId());
        assertNotNull(updatedPayment);
        assertEquals(0, updatedPayment.getAmount().compareTo(new BigDecimal("123.45")));
        assertEquals(newPaidAt, updatedPayment.getPaidAt().truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
        assertEquals(beforePayment.getPaymentStatus(), updatedPayment.getPaymentStatus());
    }

    @Test
    void updatePayment_whenMissing_throwsDAOException() {
        Payment patchPayment = new Payment();
        patchPayment.setAmount(new BigDecimal("10.00"));
        assertThrows(DAOException.class, () -> paymentCrudDao.updatePayment(999999L, patchPayment));
    }

    @Test
    void deletePayment_deletesAndSetsInvoiceToNotPaid() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Invoice invoice = persistInvoice(apartment, YearMonth.of(2025, 12), new BigDecimal("66.66"), PaymentStatus.NOT_PAID);

        Payment payment = persistPaymentForInvoice(invoice.getId());

        paymentCrudDao.deletePayment(payment.getId());

        assertNull(paymentCrudDao.getPaymentById(payment.getId()));

        Invoice updatedInvoice = invoiceCrudDao.getInvoiceById(invoice.getId());
        assertNotNull(updatedInvoice);
        assertEquals(PaymentStatus.NOT_PAID, updatedInvoice.getPaymentStatus());
    }

    @Test
    void deletePayment_whenMissing_throwsDAOException() {
        assertThrows(DAOException.class, () -> paymentCrudDao.deletePayment(999999L));
    }
}
