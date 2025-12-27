package org.university.dao.building_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.entity.Employee;
import org.university.exception.DAOException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCrudDaoTest {

    private static BuildingCrudDao buildingCrudDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        buildingCrudDao = new BuildingCrudDao();
    }

    @AfterEach
    void cleanUp() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Apartment").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();
            session.createQuery("DELETE FROM Employee").executeUpdate();
            session.createQuery("DELETE FROM Person").executeUpdate();

            transaction.commit();
        }
    }

    private Building persistBuilding(String name, String address) {
        Building b = new Building();
        b.setName(name);
        b.setAddress(address);
        b.setBuiltUpArea(new BigDecimal("120"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        b.setCountOfFloors(3);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now());

        buildingCrudDao.createBuilding(b);
        assertNotNull(b.getId());
        return b;
    }

    private Employee persistEmployee(String firstName, String lastName) {
        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setAge(30);
        e.setFeeCollectingDate(LocalDate.now());

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(e);
            transaction.commit();
        }
        assertNotNull(e.getId());
        return e;
    }

    private Apartment persistApartmentForBuilding(Building b, String number) {
        Apartment a = new Apartment();
        a.setNumber(number);
        a.setArea(new BigDecimal("70"));
        a.setHasPet(false);

        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Building managed = session.find(Building.class, b.getId());
            assertNotNull(managed);

            a.setBuilding(managed);
            session.persist(a);

            transaction.commit();
        }

        assertNotNull(a.getId());
        return a;
    }

    @Test
    void createBuilding_persists() {
        Building b = persistBuilding("Building 1", "Address 1");
        assertNotNull(b.getId());
    }

    @Test
    void getBuildingById_returnsEntity() {
        Building b = persistBuilding("Building 1", "Address 1");

        Building found = buildingCrudDao.getBuildingById(b.getId());

        assertNotNull(found);
        assertEquals(b.getId(), found.getId());
    }

    @Test
    void getBuildingById_whenMissing_returnsNull() {
        Building found = buildingCrudDao.getBuildingById(89888888L);
        assertNull(found);
    }

    @Test
    void getAllBuildings_returnsList() {
        persistBuilding("Building 1", "Address 1");
        persistBuilding("Building 2", "Address 2");

        List<Building> buildingList = buildingCrudDao.getAllBuildings();

        assertNotNull(buildingList);
        assertTrue(buildingList.size() >= 2);
    }

    @Test
    void updateBuilding() {
        Building b = persistBuilding("Building 1", "Address 1");

        Building toUpdateBuilding = new Building();
        toUpdateBuilding.setName("Building 2");
        toUpdateBuilding.setAddress("Address 2");
        toUpdateBuilding.setBuiltUpArea(new BigDecimal("200"));
        toUpdateBuilding.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.3"));
        toUpdateBuilding.setCountOfFloors(5);
        toUpdateBuilding.setApartmentsPerFloor(4);
        toUpdateBuilding.setBuiltDate(LocalDate.now().minusDays(10));

        buildingCrudDao.updateBuilding(b.getId(), toUpdateBuilding);

        Building updatedBuilding = buildingCrudDao.getBuildingById(b.getId());
        assertNotNull(updatedBuilding);

        assertEquals("Building 2", updatedBuilding.getName());
        assertEquals("Address 2", updatedBuilding.getAddress());
        assertEquals(0, updatedBuilding.getBuiltUpArea().compareTo(new BigDecimal("200")));
        assertEquals(0, updatedBuilding.getCommonAreasPercentageOfBuiltUpArea().compareTo(new BigDecimal("0.3")));
        assertEquals(5, updatedBuilding.getCountOfFloors());
        assertEquals(4, updatedBuilding.getApartmentsPerFloor());
        assertEquals(toUpdateBuilding.getBuiltDate(), updatedBuilding.getBuiltDate());
    }

    @Test
    void updateBuilding_whenMissing_throwsDAOException() {
        Building b = new Building();
        b.setName("Building 2");
        b.setAddress("Address 2");
        b.setBuiltUpArea(new BigDecimal("200"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.3"));
        b.setCountOfFloors(5);
        b.setApartmentsPerFloor(4);
        b.setBuiltDate(LocalDate.now());

        assertThrows(DAOException.class, () -> buildingCrudDao.updateBuilding(899898L, b));
    }

    @Test
    void getBuildingWithApartmentsAndEmployee_thenReturnsEntityWithFetchedRelations() {
        Building b = persistBuilding("Building 1", "Address 1");
        persistApartmentForBuilding(b, "Apartment 1");
        persistApartmentForBuilding(b, "Apartment 2");

        Employee e = persistEmployee("Georgi", "Georgiev");
        buildingCrudDao.updateBuildingEmployee(b.getId(), e);

        Building found = buildingCrudDao.getBuildingWithApartmentsAndEmployee(b.getId());

        assertNotNull(found);
        assertEquals(b.getId(), found.getId());

        assertNotNull(found.getApartmentList());
        assertTrue(found.getApartmentList().size() >= 2);

        assertNotNull(found.getEmployee());
        assertEquals(e.getId(), found.getEmployee().getId());
    }

    @Test
    void getBuildingWithApartmentsAndResidents_thenReturnsEntity() {
        Building b = persistBuilding("Building 1", "Address 1");
        persistApartmentForBuilding(b, "Apartment 1");
        persistApartmentForBuilding(b, "Apartment 2");

        Building found = buildingCrudDao.getBuildingWithApartmentsAndResidents(b.getId());

        assertNotNull(found);
        assertEquals(b.getId(), found.getId());
        assertNotNull(found.getApartmentList());
        assertEquals(2, found.getApartmentList().size());
    }

    @Test
    void getBuildingWithDetails_returnsEntity() {
        Building b = persistBuilding("Building 1", "Address 1");
        persistApartmentForBuilding(b, "Apartment 1");

        Building found = buildingCrudDao.getBuildingWithDetails(b.getId());

        assertNotNull(found);
        assertEquals(b.getId(), found.getId());
        assertNotNull(found.getApartmentList());
        assertEquals(1, found.getApartmentList().size());
    }

    @Test
    void updateBuildingEmployee_setsEmployee() {
        Building b = persistBuilding("Building 1", "Address 1");
        Employee e = persistEmployee("Maria", "Ivanov");

        buildingCrudDao.updateBuildingEmployee(b.getId(), e);

        Building found = buildingCrudDao.getBuildingWithApartmentsAndEmployee(b.getId());
        assertNotNull(found);
        assertNotNull(found.getEmployee());
        assertEquals(e.getId(), found.getEmployee().getId());
    }

    @Test
    void updateBuildingEmployee_whenMissingBuilding_throwsDAOException() {
        Employee e = persistEmployee("Petar", "Petrov");
        assertThrows(DAOException.class, () -> buildingCrudDao.updateBuildingEmployee(89999898L, e));
    }

    @Test
    void deleteBuilding_deletesEntity() {
        Building b = persistBuilding("Building 1", "Address 1");

        buildingCrudDao.deleteBuilding(b.getId());

        Building found = buildingCrudDao.getBuildingById(b.getId());
        assertNull(found);
    }

    @Test
    void deleteBuilding_whenMissing_throwsDAOException() {
        assertThrows(DAOException.class, () -> buildingCrudDao.deleteBuilding(89999898L));
    }
}
