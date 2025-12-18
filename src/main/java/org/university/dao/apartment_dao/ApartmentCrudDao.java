package org.university.dao.apartment_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public class ApartmentCrudDao {

    public void createApartment(Apartment apartment){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(apartment);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while creating apartment: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Apartment getApartmentById(Long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT DISTINCT a FROM Apartment a " +
                    "LEFT JOIN FETCH a.building " +
                    "LEFT JOIN FETCH a.residentList " +
                            "WHERE a.id = :id", Apartment.class)
                    .setParameter("id", id)
                    .getResultList().stream().findFirst().orElse(null);
        }catch(Exception e){
            throw new DAOException("Error while getting apartment with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public List<Apartment> getAllApartments(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT a FROM Apartment a", Apartment.class).getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting all apartments: ", e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public long getCountOfApartmentsByBuildingId(Long buildingId){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT COUNT(a) FROM Apartment a WHERE a.building.id = :buildingId", Long.class)
                    .setParameter("buildingId", buildingId)
                    .getSingleResult();
        }catch(Exception e){
            throw new DAOException("Error while getting count of apartments by building id: " + buildingId, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void updateApartment(Long id, Apartment apartment){
        Session session = null;
        Transaction transaction = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Apartment updatedApartment = session.find(Apartment.class, id);
            if(updatedApartment == null){
                throw new NotFoundException("Apartment with id " + id + " does not exist");
            }
            updatedApartment.setArea(apartment.getArea());
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while updating apartment with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void deleteApartment(Long id){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Apartment apartment = session.find(Apartment.class, id);
            if(apartment == null){
                throw new NotFoundException("Apartment with id " + id + " does not exist");
            }
            session.remove(apartment);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting apartment with id: " + id, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
}
