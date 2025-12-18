package org.university.dao.contract_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Contract;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public class ContractCrudDao {
    public void createContract(Contract contract){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(contract);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while creating contract: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Contract getContractById(Long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.find(Contract.class, id);
        }catch(Exception e){
            throw new DAOException("Error while getting contract with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public List<Contract> getAllContracts(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT c FROM Contract c", Contract.class).getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting all contracts: ", e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public boolean existsByBuildingId(Long buildingId){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            Long count = session.createQuery("SELECT COUNT(c) FROM Contract c WHERE c.building.id = :buildingId", Long.class)
                    .setParameter("buildingId", buildingId)
                    .getSingleResult();

            return count > 0;
        }catch(Exception e){
            throw new DAOException("Error while checking if contract exists by building id: " + buildingId, e);
        }finally {
            if(session != null && session.isOpen()) session.close();
        }
    }

    public long getCountOfContracts(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT COUNT(c) FROM Contract c", Long.class)
                    .getSingleResult();
        }catch(Exception e){
            throw new DAOException("Error while getting count of contracts: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }



    public void updateContract(Long id, Contract contract){
        Session session = null;
        Transaction transaction = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Contract updatedContract = session.find(Contract.class, id);
            if(updatedContract == null){
                throw new NotFoundException("Contract with id " + id + " does not exist");
            }
            updatedContract.setNumber(contract.getNumber());
            updatedContract.setIssueDate(contract.getIssueDate());
            updatedContract.setEndDate(contract.getEndDate());
            updatedContract.setEmployee(contract.getEmployee());
            updatedContract.setBuilding(contract.getBuilding());
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while updating contract with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void deleteContract(Long id){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Contract contract = session.find(Contract.class, id);
            if(contract == null){
                throw new NotFoundException("Contract with id " + id + " does not exist");
            }
            session.remove(contract);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting contract with id: " + id, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

}
