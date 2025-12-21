package org.university.dao.person_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Person;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public class PersonCrudDao {

    public void createPerson(Person person){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(person);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while creating person: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Person getPersonById(Long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.find(Person.class, id);
        }catch(Exception e){
            throw new DAOException("Error while getting person with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public List<Person> getAllPeople(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT p FROM Person p", Person.class).getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting all people: ", e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void updatePerson(Long id, Person person){
        Session session = null;
        Transaction transaction = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Person updatedPerson = session.find(Person.class, id);
            if(updatedPerson == null){
                throw new NotFoundException("Person with id " + id + " does not exist");
            }
            updatedPerson.setFirstName(person.getFirstName());
            updatedPerson.setLastName(person.getLastName());
            updatedPerson.setAge(person.getAge());
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while updating person with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void deletePerson(Long id){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Person person = session.find(Person.class, id);
            if(person == null){
                throw new NotFoundException("Person with id " + id + " does not exist");
            }
            session.remove(person);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting person with id: " + id, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
}
