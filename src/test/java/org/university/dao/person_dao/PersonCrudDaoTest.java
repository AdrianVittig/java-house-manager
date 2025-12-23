package org.university.dao.person_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Person;
import org.university.exception.DAOException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonCrudDaoTest {

    private static PersonCrudDao personCrudDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        personCrudDao = new PersonCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Person").executeUpdate();
            transaction.commit();
        }
    }

    private Person persistPerson(String firstName, String lastName, int age) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAge(age);

        personCrudDao.createPerson(person);
        assertNotNull(person.getId());
        return person;
    }

    @Test
    void createPerson_persists() {
        Person person = persistPerson("Ivan", "Ivanov", 25);
        assertNotNull(person.getId());
    }

    @Test
    void getPersonById_returnsEntity() {
        Person person = persistPerson("Ivan", "Ivanov", 25);

        Person found = personCrudDao.getPersonById(person.getId());

        assertNotNull(found);
        assertEquals(person.getId(), found.getId());
        assertEquals("Ivan", found.getFirstName());
        assertEquals("Ivanov", found.getLastName());
        assertEquals(25, found.getAge());
    }

    @Test
    void getPersonById_whenMissing_returnsNull() {
        Person found = personCrudDao.getPersonById(999999L);
        assertNull(found);
    }

    @Test
    void getAllPeople_returnsList() {
        persistPerson("Ivan", "Ivanov", 25);
        persistPerson("Maria", "Petrova", 30);

        List<Person> people = personCrudDao.getAllPeople();

        assertNotNull(people);
        assertTrue(people.size() >= 2);
    }

    @Test
    void updatePerson_updatesFields() {
        Person person = persistPerson("Ivan", "Ivanov", 25);

        Person patchPerson = new Person();
        patchPerson.setFirstName("Georgi");
        patchPerson.setLastName("Georgiev");
        patchPerson.setAge(31);

        personCrudDao.updatePerson(person.getId(), patchPerson);

        Person updated = personCrudDao.getPersonById(person.getId());
        assertNotNull(updated);
        assertEquals("Georgi", updated.getFirstName());
        assertEquals("Georgiev", updated.getLastName());
        assertEquals(31, updated.getAge());
    }

    @Test
    void updatePerson_whenMissing_throwsDAOException() {
        Person patchPerson = new Person();
        patchPerson.setFirstName("Georgi");
        patchPerson.setLastName("Georgiev");
        patchPerson.setAge(31);

        assertThrows(DAOException.class, () -> personCrudDao.updatePerson(999999L, patchPerson));
    }

    @Test
    void deletePerson_deletesEntity() {
        Person person = persistPerson("Ivan", "Ivanov", 25);

        personCrudDao.deletePerson(person.getId());

        Person found = personCrudDao.getPersonById(person.getId());
        assertNull(found);
    }

    @Test
    void deletePerson_whenMissing_throwsDAOException() {
        assertThrows(DAOException.class, () -> personCrudDao.deletePerson(999999L));
    }
}