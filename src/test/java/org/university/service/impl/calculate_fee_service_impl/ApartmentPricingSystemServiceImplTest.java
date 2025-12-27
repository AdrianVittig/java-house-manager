package org.university.service.impl.calculate_fee_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.apartment_dao.ApartmentCrudDao;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.entity.Resident;
import org.university.util.ResidentRole;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ApartmentPricingSystemServiceImplTest {

    private static ApartmentPricingSystemServiceImpl service;
    private static ApartmentCrudDao apartmentDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new ApartmentPricingSystemServiceImpl();
        apartmentDao = new ApartmentCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.createQuery("DELETE FROM Resident").executeUpdate();
            session.createQuery("DELETE FROM Apartment").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();

            tx.commit();
        }
    }

    private Building persistBuilding() {
        Building b = new Building();
        b.setName("Building 1");
        b.setAddress("Address 1");
        b.setBuiltUpArea(new BigDecimal("1200.00"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.20"));
        b.setCountOfFloors(4);
        b.setApartmentsPerFloor(3);
        b.setBuiltDate(LocalDate.now().minusYears(5));

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(b);
            tx.commit();
        }
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
        return a;
    }

    private void persistResident(Long apartmentId, String firstName, String lastName, int age, boolean usesElevator) {
        Resident r = new Resident();
        r.setFirstName(firstName);
        r.setLastName(lastName);
        r.setAge(age);
        r.setUsesElevator(usesElevator);
        r.setRole(ResidentRole.OWNER);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Apartment a = session.find(Apartment.class, apartmentId);
            r.setApartment(a);
            session.persist(r);
            tx.commit();
        }
    }

    @Test
    void calculateFee_baseOnly() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1", new BigDecimal("80.00"), false);

        BigDecimal fee = service.calculateFee(a);

        assertEquals(0, fee.compareTo(new BigDecimal("80.00")));
    }

    @Test
    void calculateFee_withElevatorUsersAndPet() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 2", new BigDecimal("100.00"), true);

        persistResident(a.getId(), "Georgi", "Georgiev", 10, true);
        persistResident(a.getId(), "Maria", "Ivanov", 8, true);
        persistResident(a.getId(), "Petar", "Petrov", 7, true);
        persistResident(a.getId(), "Ivan", "Ivanov", 30, false);

        BigDecimal fee = service.calculateFee(a);

        BigDecimal expected = new BigDecimal("100.00")
                .add(new BigDecimal("100.00").multiply(new BigDecimal("0.15")).multiply(new BigDecimal("2")))
                .add(new BigDecimal("100.00").multiply(new BigDecimal("0.075")))
                .setScale(2);

        assertEquals(0, fee.compareTo(expected));
    }
}
