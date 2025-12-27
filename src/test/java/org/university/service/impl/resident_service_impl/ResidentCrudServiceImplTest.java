package org.university.service.impl.resident_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dto.ResidentWithDetailsDto;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.entity.Resident;
import org.university.exception.NotFoundException;
import org.university.util.ResidentRole;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ResidentCrudServiceImplTest {

    private static ResidentCrudServiceImpl service;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new ResidentCrudServiceImpl();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.createQuery("DELETE FROM Resident").executeUpdate();
            session.createQuery("DELETE FROM Invoice").executeUpdate();
            session.createQuery("DELETE FROM Apartment").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();

            tx.commit();
        }
    }

    private Building persistBuilding() {
        Building b = new Building();
        b.setName("Building 1");
        b.setAddress("Address 1");
        b.setBuiltUpArea(new BigDecimal("120"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        b.setCountOfFloors(2);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now().minusDays(1));

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
        a.setArea(new BigDecimal("70.00"));
        a.setHasPet(false);

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
    void createResident_success() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");

        Resident r = new Resident();
        Apartment ref = new Apartment();
        ref.setId(a.getId());
        r.setApartment(ref);
        r.setFirstName("Maria");
        r.setLastName("Ivanov");
        r.setAge(25);
        r.setUsesElevator(true);
        r.setRole(ResidentRole.OWNER);

        service.createResident(r);

        assertNotNull(r.getId());

        ResidentWithDetailsDto dto = service.getResidentById(r.getId());
        assertNotNull(dto);
        assertEquals(r.getId(), dto.getId());
        assertNotNull(dto.getApartmentId());
        assertEquals(a.getId(), dto.getApartmentId());
        assertEquals("Maria", dto.getFirstName());
        assertEquals("Ivanov", dto.getLastName());
        assertEquals(25, dto.getAge());
    }

    @Test
    void createResident_whenNullResident_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.createResident(null));
    }

    @Test
    void createResident_whenApartmentMissing_throws() {
        Resident r = new Resident();
        r.setFirstName("Georgi");
        r.setLastName("Georgiev");
        r.setAge(25);
        r.setUsesElevator(true);
        r.setRole(ResidentRole.OWNER);

        assertThrows(IllegalArgumentException.class, () -> service.createResident(r));
    }

    @Test
    void getResidentById_notFound() {
        assertThrows(NotFoundException.class, () -> service.getResidentById(999999L));
    }

    @Test
    void updateResident_success() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");

        Resident r = new Resident();
        Apartment ref = new Apartment();
        ref.setId(a.getId());
        r.setApartment(ref);
        r.setFirstName("Ivan");
        r.setLastName("Ivanov");
        r.setAge(25);
        r.setUsesElevator(true);
        r.setRole(ResidentRole.OWNER);

        service.createResident(r);

        r.setFirstName("Petar");
        r.setLastName("Petrov");
        r.setAge(31);
        r.setUsesElevator(false);

        service.updateResident(r);

        ResidentWithDetailsDto updated = service.getResidentById(r.getId());
        assertEquals("Petar", updated.getFirstName());
        assertEquals("Petrov", updated.getLastName());
        assertEquals(31, updated.getAge());
        assertFalse(updated.isUsesElevator());
    }

    @Test
    void updateResident_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.updateResident(null));
    }

    @Test
    void updateResident_whenIdNull_throws() {
        Resident r = new Resident();
        assertThrows(IllegalArgumentException.class, () -> service.updateResident(r));
    }

    @Test
    void deleteResident_success() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "Apartment 1");

        Resident r = new Resident();
        Apartment ref = new Apartment();
        ref.setId(a.getId());
        r.setApartment(ref);
        r.setFirstName("Yordan");
        r.setLastName("Georgiev");
        r.setAge(25);
        r.setUsesElevator(true);
        r.setRole(ResidentRole.OWNER);

        service.createResident(r);
        Long id = r.getId();

        service.deleteResident(id);

        assertThrows(NotFoundException.class, () -> service.getResidentById(id));
    }

    @Test
    void deleteResident_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.deleteResident(999999L));
    }
}
