package org.university.service.impl.calculate_fee_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.entity.Building;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BuildingPricingSystemServiceImplTest {

    private static BuildingPricingSystemServiceImpl service;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new BuildingPricingSystemServiceImpl();
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
        b.setBuiltUpArea(new BigDecimal("2500.00"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.18"));
        b.setCountOfFloors(3);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now().minusYears(10));

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(b);
            tx.commit();
        }
        return b;
    }

    private Apartment persistApartment(Long buildingId, String number, BigDecimal area, boolean hasPet) {
        Apartment a = new Apartment();
        a.setNumber(number);
        a.setArea(area);
        a.setHasPet(hasPet);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Building b = session.find(Building.class, buildingId);
            a.setBuilding(b);
            session.persist(a);
            tx.commit();
        }
        return a;
    }

    @Test
    void calculateFeeForBuilding_sumsApartments() {
        Building b = persistBuilding();
        persistApartment(b.getId(), "Apartment 1", new BigDecimal("60.00"), false);
        persistApartment(b.getId(), "Apartment 2", new BigDecimal("90.00"), false);

        BigDecimal total = service.calculateFeeForBuilding(b);

        assertEquals(0, total.compareTo(new BigDecimal("150.00")));
    }
}
