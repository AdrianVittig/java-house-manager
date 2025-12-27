package org.university.service.impl.apartment_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dto.ApartmentWithDetailsDto;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ApartmentCrudServiceImplTest {

    private ApartmentCrudServiceImpl service;
    private Building building;

    @BeforeEach
    void setUp() {
        service = new ApartmentCrudServiceImpl();

        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        building = new Building();
        building.setName("Building 1");
        building.setAddress("Address 1");
        building.setBuiltUpArea(new BigDecimal("120"));
        building.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        building.setCountOfFloors(2);
        building.setApartmentsPerFloor(2);
        building.setBuiltDate(LocalDate.now().minusDays(10));

        session.persist(building);

        tx.commit();
        session.close();
    }

    @AfterEach
    void tearDown() {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.createQuery("DELETE FROM Apartment").executeUpdate();
        session.createQuery("DELETE FROM Building").executeUpdate();

        tx.commit();
        session.close();
    }

    @Test
    void createApartment_success() {
        Apartment apartment = new Apartment();
        apartment.setArea(new BigDecimal("70.00"));
        apartment.setHasPet(false);
        apartment.setBuilding(building);
        apartment.setNumber("Apartment 1");

        service.createApartment(apartment);

        ApartmentWithDetailsDto persisted = service.getApartmentById(apartment.getId());

        assertNotNull(persisted);
        assertEquals(new BigDecimal("70.00"), persisted.getArea());
        assertEquals("Room: 1001", persisted.getNumber());
    }

    @Test
    void createApartment_noFreeApartments() {
        for (int i = 0; i < 4; i++) {
            Apartment a = new Apartment();
            a.setArea(BigDecimal.valueOf(50));
            a.setHasPet(false);
            a.setBuilding(building);
            a.setNumber("Apartment " + (i + 1));
            service.createApartment(a);
        }

        Apartment extra = new Apartment();
        extra.setArea(BigDecimal.valueOf(60));
        extra.setHasPet(false);
        extra.setBuilding(building);

        assertThrows(IllegalArgumentException.class, () -> service.createApartment(extra));
    }

    @Test
    void getApartmentById_notFound() {
        assertThrows(NotFoundException.class, () -> service.getApartmentById(999L));
    }

    @Test
    void updateApartment_success() {
        Apartment apartment = new Apartment();
        apartment.setArea(BigDecimal.valueOf(40));
        apartment.setHasPet(false);
        apartment.setBuilding(building);
        apartment.setNumber("Apartment 1");

        service.createApartment(apartment);

        apartment.setArea(BigDecimal.valueOf(95.00));
        apartment.setHasPet(true);

        service.updateApartment(apartment);

        ApartmentWithDetailsDto updated = service.getApartmentById(apartment.getId());

        assertEquals(new BigDecimal("95.00"), updated.getArea());
        assertTrue(updated.isHasPet());
    }

    @Test
    void deleteApartment_success() {
        Apartment apartment = new Apartment();
        apartment.setArea(BigDecimal.valueOf(55));
        apartment.setHasPet(false);
        apartment.setBuilding(building);
        apartment.setNumber("Apartment 1");

        service.createApartment(apartment);
        Long id = apartment.getId();

        service.deleteApartment(id);

        assertThrows(NotFoundException.class, () -> service.getApartmentById(id));
    }
}
