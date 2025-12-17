package org.university.dao.resident_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Person;
import org.university.entity.Resident;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public class ResidentCrudDao {
    public void createResident(Resident resident){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(resident);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while creating resident: " + e, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Resident getResidentById(Long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.find(Resident.class, id);
        }catch(Exception e){
            throw new DAOException("Error while getting resident with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public List<Resident> getAllResidents(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT r FROM Resident r", Resident.class).getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting all residents: " + e, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void updateResident(Long id, Resident resident){
        Session session = null;
        Transaction transaction = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Resident updatedResident = session.find(Resident.class, id);
            if(updatedResident == null){
                throw new NotFoundException("Resident with id " + id + " does not exist");
            }
            updatedResident.setFirstName(resident.getFirstName());
            updatedResident.setLastName(resident.getLastName());
            updatedResident.setAge(resident.getAge());
            updatedResident.setRole(resident.getRole());
            updatedResident.setHasPet(resident.isHasPet());
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while updating resident with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void deleteResident(Long id){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Resident resident = session.find(Resident.class, id);
            if(resident == null){
                throw new NotFoundException("Resident with id " + id + " does not exist");
            }
            session.remove(resident);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting resident with id: " + id, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
}
