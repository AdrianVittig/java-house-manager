package org.university.dao.company_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Apartment;
import org.university.entity.Company;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public class CompanyCrudDao {
    public void createCompany(Company company){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(company);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while creating company: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Company getCompanyById(Long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.find(Company.class, id);
        }catch(Exception e){
            throw new DAOException("Error while getting company with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
    public Company getCompanyWithDetails(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT c FROM Company c " +
                                    "LEFT JOIN FETCH c.employeeList " +
                                    "WHERE c.id = :id", Company.class
                    ).setParameter("id", id)
                    .getResultList().stream().findFirst().orElse(null);
        } catch (Exception e) {
            throw new DAOException("Error while getting company details id=" + id, e);
        }
    }

    public List<Company> getAllCompanies(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT c FROM Company c", Company.class).getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting all companies: ", e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void updateCompany(Long id, Company company){
        Session session = null;
        Transaction transaction = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Company updatedCompany = session.find(Company.class, id);
            if(updatedCompany == null){
                throw new NotFoundException("Company with id " + id + " does not exist");
            }
            updatedCompany.setName(company.getName());
            updatedCompany.setRevenue(company.getRevenue());
            transaction.commit();
        }catch (NotFoundException e) {
            throw e;
        } catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while updating company with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void deleteCompany(Long id){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Company company = session.find(Company.class, id);
            if(company == null){
                throw new NotFoundException("Company with id " + id + " does not exist");
            }
            session.remove(company);
            transaction.commit();
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting company with id: " + id, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
}
