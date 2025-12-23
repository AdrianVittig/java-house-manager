package org.university.dao.apartment_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.exception.DAOException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApartmentCrudDaoTest {

    private static ApartmentCrudDao apartmentCrudDao;


    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        apartmentCrudDao = new ApartmentCrudDao();
    }

    @AfterEach
    void cleanup(){
        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Apartment").executeUpdate();
            session.createQuery("DELETE FROM Building").executeUpdate();
            transaction.commit();
        }
    }

    private Building persistBuilding(){
        Building b = new Building();
        b.setName("Test Building");
        b.setAddress("Test Address");
        b.setBuiltUpArea(new BigDecimal("120"));
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));
        b.setCountOfFloors(3);
        b.setApartmentsPerFloor(2);
        b.setBuiltDate(LocalDate.now());

        try(Session session = SessionFactoryUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(b);
            transaction.commit();
        }

        assertNotNull(b.getId());
        return b;
    }

    private Apartment persistApartment(Building b, String number){
        Apartment a = new Apartment();
        a.setNumber(number);
        a.setArea(new  BigDecimal("70"));
        a.setHasPet(false);
        a.setBuilding(b);

        apartmentCrudDao.createApartment(a);
        assertNotNull(a.getId());
        return a;
    }

    @Test
    void createApartment_persists() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "1000");
        assertNotNull(a.getId());
    }

    @Test
    void createApartment_whenBuildingMissing_throwsDAOException(){
        Building testBuilding = new Building();
        testBuilding.setId(99999L);

        Apartment a = new Apartment();
        a.setNumber("1000");
        a.setArea(new BigDecimal("70"));
        a.setHasPet(false);
        a.setBuilding(testBuilding);

        assertThrows(DAOException.class, () -> apartmentCrudDao.createApartment(a));
    }

    @Test
    void getApartmentById_returnsEntity() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "1000");

        Apartment found = apartmentCrudDao.getApartmentById(a.getId());

        assertNotNull(found);
        assertEquals(a.getId(), found.getId());
    }

    @Test
    void getApartmentById_whenMissing_thenReturnNull(){
        Apartment found = apartmentCrudDao.getApartmentById(89888888L);
        assertNull(found);
    }

    @Test
    void getAllApartments_returnsList() {
        Building b = persistBuilding();
        persistApartment(b, "1001");
        persistApartment(b, "1002");

        List<Apartment> apartments = apartmentCrudDao.getAllApartments();

        assertNotNull(apartments);
        assertTrue(apartments.size() >= 2);
    }

    @Test
    void getCountOfApartmentsByBuildingId_returnsCorrectCount() {
        Building b = persistBuilding();
        persistApartment(b, "1000");
        persistApartment(b, "1001");

        long count = apartmentCrudDao.getCountOfApartmentsByBuildingId(b.getId());

        assertEquals(2, count);
    }

    @Test
    void getApartmentWithResidents_returnsEntity() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "1000");

        Apartment found = apartmentCrudDao.getApartmentWithResidents(a.getId());

        assertNotNull(found);
        assertEquals(a.getId(), found.getId());
    }

    @Test
    void getApartmentWithBuildingAndEmployee_returnsEntity() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "1000");

        Apartment found = apartmentCrudDao.getApartmentWithBuildingAndEmployee(a.getId());

        assertNotNull(found);
        assertEquals(a.getId(), found.getId());
        assertNotNull(found.getBuilding());
        assertEquals(b.getId(), found.getBuilding().getId());
    }

    @Test
    void updateApartment_updatesFields() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "1000");

        Apartment newApartment = new Apartment();
        newApartment.setArea(new BigDecimal("80"));
        newApartment.setHasPet(true);

        apartmentCrudDao.updateApartment(a.getId(), newApartment);

        Apartment updated = apartmentCrudDao.getApartmentById(a.getId());
        assertNotNull(updated);

        assertNotNull(updated.getArea());
        assertEquals(0, updated.getArea().compareTo(new BigDecimal("80")));
        assertTrue(updated.isHasPet());
    }

    @Test
    void deleteApartment() {
        Building b = persistBuilding();
        Apartment a = persistApartment(b, "1000");

        apartmentCrudDao.deleteApartment(a.getId());

        Apartment found = apartmentCrudDao.getApartmentById(a.getId());
        assertNull(found);
    }
}