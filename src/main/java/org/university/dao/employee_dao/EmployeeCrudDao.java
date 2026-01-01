package org.university.dao.employee_dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.university.configuration.SessionFactoryUtil;
import org.university.entity.Employee;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public class EmployeeCrudDao {
    public void createEmployee(Employee employee){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(employee);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while creating employee: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Employee getEmployeeById(Long id){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.find(Employee.class, id);
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            throw new DAOException("Error while getting employee with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public List<Employee> getAllEmployees(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
        }catch(Exception e){
            throw new DAOException("Error while getting all employees: ", e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Long getEmployeeIdWithLeastContracts(){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT e.id " +
                    "FROM Employee e " +
                    "LEFT JOIN e.contractList c " +
                    "GROUP BY e.id " +
                    "ORDER BY COUNT(c) ASC, e.id ASC",
                            Long.class)
                    .setMaxResults(1)
                    .getSingleResult();
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            throw new DAOException("Error while getting employee id with fewest contracts: ", e);
        }
        finally{
            if(session != null && session.isOpen()) session.close();
        }
    }


    public Employee getEmployeeWithRelations(Long employeeId){
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery("SELECT DISTINCT e " +
                    "FROM Employee e " +
                    "LEFT JOIN FETCH e.contractList " +
                    "WHERE e.id = :id",
                    Employee.class)
                    .setParameter("id", employeeId)
                    .getSingleResult();
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            throw new DAOException("Error while getting employee with relations: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public Long getEmployeeIdWithLeastContractsExcluding(Long excludedEmployeeId, Long companyId) {
        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            List<Long> ids = session.createQuery(
                            "SELECT e.id " +
                                    "FROM Employee e " +
                                    "LEFT JOIN e.contractList c " +
                                    "WHERE e.id <> :excludedId AND e.company.id = :companyId " +
                                    "GROUP BY e.id " +
                                    "ORDER BY COUNT(c) ASC, e.id ASC",
                            Long.class
                    )
                    .setParameter("excludedId", excludedEmployeeId)
                    .setParameter("companyId", companyId)
                    .setMaxResults(1)
                    .getResultList();

            return ids.isEmpty() ? null : ids.get(0);

        }catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Error while getting employee id with fewest contracts (excluding): ", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    public void updateEmployee(Long id, Employee employee){
        Session session = null;
        Transaction transaction = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Employee updatedEmployee = session.find(Employee.class, id);
            if(updatedEmployee == null){
                throw new NotFoundException("Employee with id " + id + " does not exist");
            }
            updatedEmployee.setFirstName(employee.getFirstName());
            updatedEmployee.setLastName(employee.getLastName());
            updatedEmployee.setAge(employee.getAge());
            updatedEmployee.setFeeCollectingDate(employee.getFeeCollectingDate());
            transaction.commit();
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while updating employee with id: " + id, e);
        } finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    public void deleteEmployee(Long id){
        Session session = null;
        Transaction transaction = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Employee employee = session.find(Employee.class, id);
            if(employee == null){
               throw new NotFoundException("Employee with id " + id + " does not exist");
            }
            session.remove(employee);
            transaction.commit();
        }catch (NotFoundException e) {
            throw e;
        }catch(Exception e){
            if(transaction != null) transaction.rollback();
            throw new DAOException("Error while deleting employee with id: " + id, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
}
