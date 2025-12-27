package org.university.service.impl.building_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.apartment_dao.ApartmentCrudDao;
import org.university.dao.building_dao.BuildingCrudDao;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.exception.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCrudServiceImplTest {

    private static BuildingCrudServiceImpl service;
    private static BuildingCrudDao buildingDao;
    private static ApartmentCrudDao apartmentDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new BuildingCrudServiceImpl();
        buildingDao = new BuildingCrudDao();
        apartmentDao = new ApartmentCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("DELETE FROM Payment").executeUpdate();
            session.createQuery("DELETE FROM Invoice").executeUpdate();
            session.createQuery("DELETE FROM Contract").executeUpdate();
            session.createQuery("DELETE FROM Resident").executeUpdate();
            session.createQuery("DELETE FROM Apartment").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();
            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Company").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();
            tx.commit();
        }
    }

    private Building newValidBuilding(String name) {
        Building b = new Building();
        b.setName(name);
        b.setAddress("Test Address");
        b.setBuiltUpArea(new BigDecimal("120"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        b.setCountOfFloors(3);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now().minusDays(10));
        return b;
    }

    private Building persistBuilding(String name) {
        Building b = newValidBuilding(name);
        service.createBuilding(b);
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
            assertNotNull(managed);
            a.setBuilding(managed);
            session.persist(a);
            tx.commit();
        }

        assertNotNull(a.getId());
        return a;
    }

    @Test
    void createBuilding_success() {
        Building b = newValidBuilding("TestBuilding");
        service.createBuilding(b);
        assertNotNull(b.getId());
        assertNotNull(buildingDao.getBuildingById(b.getId()));
    }

    @Test
    void createBuilding_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.createBuilding(null));
    }

    @Test
    void createBuilding_whenInvalidFloors_throws() {
        Building b = newValidBuilding("TestBuilding");
        b.setCountOfFloors(0);
        assertThrows(IllegalArgumentException.class, () -> service.createBuilding(b));
    }

    @Test
    void getBuildingById_success() {
        Building b = persistBuilding("TestBuilding");
        assertNotNull(service.getBuildingById(b.getId()));
    }

    @Test
    void getBuildingById_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.getBuildingById(999999L));
    }

    @Test
    void getAllBuildings_returnsList() {
        persistBuilding("Building");
        persistBuilding("Building 2");
        assertTrue(service.getAllBuildings().size() >= 2);
    }

    @Test
    void updateBuilding_success() {
        Building b = persistBuilding("Building");
        b.setName("Updated");
        b.setAddress("Updated Address");
        b.setBuiltUpArea(new BigDecimal("200"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.3"));
        b.setCountOfFloors(5);
        b.setApartmentsPerFloor(4);
        b.setBuiltDate(LocalDate.now().minusDays(20));

        service.updateBuilding(b);

        Building updated = buildingDao.getBuildingById(b.getId());
        assertNotNull(updated);
        assertEquals("Updated", updated.getName());
        assertEquals("Updated Address", updated.getAddress());
        assertEquals(0, updated.getBuiltUpArea().compareTo(new BigDecimal("200")));
        assertEquals(5, updated.getCountOfFloors());
        assertEquals(4, updated.getApartmentsPerFloor());
    }

    @Test
    void updateBuilding_whenNull_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.updateBuilding(null));
    }

    @Test
    void deleteBuilding_success_whenNoApartments() {
        Building b = persistBuilding("Building");
        assertEquals(0, apartmentDao.getCountOfApartmentsByBuildingId(b.getId()));
        service.deleteBuilding(b.getId());
        assertNull(buildingDao.getBuildingById(b.getId()));
    }

    @Test
    void deleteBuilding_whenHasApartments_throws() {
        Building b = persistBuilding("Building");
        persistApartment(b, "Room: 1001");
        assertTrue(apartmentDao.getCountOfApartmentsByBuildingId(b.getId()) > 0);
        assertThrows(IllegalArgumentException.class, () -> service.deleteBuilding(b.getId()));
    }

    @Test
    void deleteBuilding_whenMissing_throws() {
        assertThrows(NotFoundException.class, () -> service.deleteBuilding(999999L));
    }
}
