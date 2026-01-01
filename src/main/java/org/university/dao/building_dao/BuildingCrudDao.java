package org.university.dao.building_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.entity.Employee;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public class BuildingCrudDao {
    public void createBuilding(Building building){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(building);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while creating building: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Building getBuildingById(Long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.find(Building.class, id);
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            throw new DAOException("Error while getting building with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public List<Building> getAllBuildings(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT b FROM Building b", Building.class).getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting all buildings: ", e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void updateBuilding(Long id, Building building){
        Session session = null;
        Transaction transaction = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Building updatedBuilding = session.find(Building.class, id);
            if(updatedBuilding == null){
                throw new NotFoundException("Building with id " + id + " does not exist");
            }
            updatedBuilding.setName(building.getName());
            updatedBuilding.setAddress(building.getAddress());
            updatedBuilding.setApartmentsPerFloor(building.getApartmentsPerFloor());
            updatedBuilding.setCountOfFloors(building.getCountOfFloors());
            updatedBuilding.setBuiltUpArea(building.getBuiltUpArea());
            updatedBuilding.setCommonAreasPercentageOfBuiltUpArea(building.getCommonAreasPercentageOfBuiltUpArea());
            updatedBuilding.setBuiltDate(building.getBuiltDate());
            transaction.commit();
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while updating building with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Building getBuildingWithApartmentsAndEmployee(long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT DISTINCT b FROM Building b " +
                            "LEFT JOIN FETCH b.employee " +
                            "LEFT JOIN FETCH b.apartmentList a " +
                            "WHERE b.id = :id ", Building.class
                    )
                    .setParameter("id", id)
                    .getResultList().stream().findFirst().orElse(null);
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            throw new DAOException("Error while getting building with apartments and employee, id: " + id, e);
        }
        finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Building getBuildingWithApartmentsAndResidents(long id) {
        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            Building building = session.createQuery(
                            "SELECT DISTINCT b FROM Building b " +
                                    "LEFT JOIN FETCH b.employee " +
                                    "LEFT JOIN FETCH b.apartmentList a " +
                                    "WHERE b.id = :id", Building.class
                    )
                    .setParameter("id", id)
                    .getResultList().stream().findFirst().orElse(null);

            if (building == null) {
                return null;
            }

            session.createQuery(
                            "SELECT DISTINCT a FROM Apartment a " +
                                    "LEFT JOIN FETCH a.residentList r " +
                                    "WHERE a.building.id = :id", Apartment.class
                    )
                    .setParameter("id", id)
                    .getResultList();

            return building;

        } catch (NotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new DAOException("Error while getting building with apartments and residents, id: " + id, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }


    public Building getBuildingWithDetails(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT b FROM Building b " +
                                    "LEFT JOIN FETCH b.employee " +
                                    "LEFT JOIN FETCH b.apartmentList " +
                                    "LEFT JOIN FETCH b.contract " +
                                    "WHERE b.id = :id", Building.class
                    ).setParameter("id", id)
                    .getResultList().stream().findFirst().orElse(null);
        }catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Error while getting building details id=" + id, e);
        }
    }

    public void updateBuildingEmployee(Long buildingId, Employee employee){
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Building managed = session.find(Building.class, buildingId);
            if (managed == null) {
                throw new NotFoundException("Building with id " + buildingId + " does not exist");
            }

            managed.setEmployee(employee);

            tx.commit();
        } catch (NotFoundException e) {
            throw e;
        }catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new DAOException("Error while updating building employee for building id: " + buildingId, e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public void deleteBuilding(Long id){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Building building = session.find(Building.class, id);
            if(building == null){
                throw new NotFoundException("Building with id " + id + " does not exist");
            }
            session.remove(building);
            transaction.commit();
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting building with id: " + id, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
}
