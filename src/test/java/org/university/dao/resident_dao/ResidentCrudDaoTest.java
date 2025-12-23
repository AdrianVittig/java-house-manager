package org.university.dao.resident_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.entity.Resident;
import org.university.exception.DAOException;
import org.university.util.ResidentRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResidentCrudDaoTest {

    private static ResidentCrudDao residentCrudDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        residentCrudDao = new ResidentCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createQuery("DELETE FROM Resident").executeUpdate();
            session.createQuery("DELETE FROM Apartment").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();

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

    private Resident persistResident(Apartment apartment, String firstName, String lastName, int age) {
        Resident resident = new Resident();

        Apartment apartmentRef = new Apartment();
        apartmentRef.setId(apartment.getId());

        resident.setApartment(apartmentRef);
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        resident.setAge(age);
        resident.setUsesElevator(true);
        resident.setRole(ResidentRole.OWNER);

        residentCrudDao.createResident(resident);
        assertNotNull(resident.getId());
        return resident;
    }

    @Test
    void createResident_persists() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");

        Resident resident = persistResident(apartment, "Ivan", "Ivanov", 25);

        assertNotNull(resident.getId());
    }

    @Test
    void createResident_whenApartmentMissing_throwsDAOException() {
        Apartment apartment = new Apartment();
        apartment.setId(999999L);

        Resident resident = new Resident();
        resident.setApartment(apartment);
        resident.setFirstName("Ivan");
        resident.setLastName("Ivanov");
        resident.setAge(25);
        resident.setUsesElevator(true);

        assertThrows(DAOException.class, () -> residentCrudDao.createResident(resident));
    }

    @Test
    void getResidentById_returnsEntity() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Resident resident = persistResident(apartment, "Ivan", "Ivanov", 25);

        Resident found = residentCrudDao.getResidentById(resident.getId());

        assertNotNull(found);
        assertEquals(resident.getId(), found.getId());
        assertEquals("Ivan", found.getFirstName());
        assertEquals("Ivanov", found.getLastName());
        assertEquals(25, found.getAge());
    }

    @Test
    void getResidentById_whenMissing_returnsNull() {
        Resident found = residentCrudDao.getResidentById(999999L);
        assertNull(found);
    }

    @Test
    void getResidentWithDetails_returnsEntityWithApartment() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Resident resident = persistResident(apartment, "Ivan", "Ivanov", 25);

        Resident found = residentCrudDao.getResidentWithDetails(resident.getId());

        assertNotNull(found);
        assertEquals(resident.getId(), found.getId());
        assertNotNull(found.getApartment());
        assertEquals(apartment.getId(), found.getApartment().getId());
    }

    @Test
    void getResidentWithDetails_whenMissing_returnsNull() {
        Resident found = residentCrudDao.getResidentWithDetails(999999L);
        assertNull(found);
    }

    @Test
    void getAllResidents_returnsList() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        persistResident(apartment, "Ivan", "Ivanov", 25);
        persistResident(apartment, "Maria", "Petrova", 30);

        List<Resident> residents = residentCrudDao.getAllResidents();

        assertNotNull(residents);
        assertTrue(residents.size() >= 2);
    }

    @Test
    void updateResident_updatesFields() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Resident resident = persistResident(apartment, "Ivan", "Ivanov", 25);

        Resident patchResident = new Resident();
        patchResident.setFirstName("Georgi");
        patchResident.setLastName("Georgiev");
        patchResident.setAge(31);
        patchResident.setRole(resident.getRole());
        patchResident.setUsesElevator(false);

        residentCrudDao.updateResident(resident.getId(), patchResident);

        Resident updated = residentCrudDao.getResidentById(resident.getId());
        assertNotNull(updated);
        assertEquals("Georgi", updated.getFirstName());
        assertEquals("Georgiev", updated.getLastName());
        assertEquals(31, updated.getAge());
        assertFalse(updated.isUsesElevator());
    }

    @Test
    void updateResident_whenMissing_throwsDAOException() {
        Resident patchResident = new Resident();
        patchResident.setFirstName("Georgi");
        patchResident.setLastName("Georgiev");
        patchResident.setAge(31);
        patchResident.setUsesElevator(false);

        assertThrows(DAOException.class, () -> residentCrudDao.updateResident(999999L, patchResident));
    }

    @Test
    void deleteResident_deletesEntity() {
        Building building = persistBuilding();
        Apartment apartment = persistApartment(building, "1001");
        Resident resident = persistResident(apartment, "Ivan", "Ivanov", 25);

        residentCrudDao.deleteResident(resident.getId());

        Resident found = residentCrudDao.getResidentById(resident.getId());
        assertNull(found);
    }

    @Test
    void deleteResident_whenMissing_throwsDAOException() {
        assertThrows(DAOException.class, () -> residentCrudDao.deleteResident(999999L));
    }
}