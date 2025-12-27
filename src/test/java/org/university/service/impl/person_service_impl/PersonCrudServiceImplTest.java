package org.university.service.impl.person_service_impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.configuration.SessionFactoryUtil;
import org.university.dao.person_dao.PersonCrudDao;
import org.university.dto.PersonDto;
import org.university.entity.Person;
import org.university.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class PersonCrudServiceImplTest {

    private static PersonCrudServiceImpl service;
    private static PersonCrudDao personDao;

    @BeforeAll
    static void setUp() {
        SessionFactoryUtil.getSessionFactory();
        service = new PersonCrudServiceImpl();
        personDao = new PersonCrudDao();
    }

    @AfterEach
    void cleanup() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("DELETE FROM Person").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void createPerson_success() {
        Person p = new Person();
        p.setFirstName("Ivan");
        p.setLastName("Ivanov");
        p.setAge(25);

        service.createPerson(p);

        assertNotNull(p.getId());
        Person found = personDao.getPersonById(p.getId());
        assertNotNull(found);
    }

    @Test
    void createPerson_validation() {
        assertThrows(IllegalArgumentException.class, () -> service.createPerson(null));

        Person p = new Person();
        p.setFirstName(null);
        p.setLastName("x");
        p.setAge(1);
        assertThrows(IllegalArgumentException.class, () -> service.createPerson(p));

        Person p2 = new Person();
        p2.setFirstName("x");
        p2.setLastName("y");
        p2.setAge(0);
        assertThrows(IllegalArgumentException.class, () -> service.createPerson(p2));
    }

    @Test
    void getPersonById_notFound_throws() {
        assertThrows(NotFoundException.class, () -> service.getPersonById(999999L));
    }

    @Test
    void updatePerson_success() {
        Person p = new Person();
        p.setFirstName("Ivan");
        p.setLastName("Ivanov");
        p.setAge(25);
        service.createPerson(p);

        p.setFirstName("Georgi");
        p.setLastName("Georgiev");
        p.setAge(30);

        service.updatePerson(p);

        PersonDto dto = service.getPersonById(p.getId());
        assertEquals("Georgi", dto.getFirstName());
        assertEquals("Georgiev", dto.getLastName());
        assertEquals(30, dto.getAge());
    }

    @Test
    void deletePerson_success() {
        Person p = new Person();
        p.setFirstName("Ivan");
        p.setLastName("Ivanov");
        p.setAge(25);
        service.createPerson(p);

        service.deletePerson(p.getId());

        assertNull(personDao.getPersonById(p.getId()));
    }
}
