package org.university.service.impl.manage_data_service_impl;

import org.hibernate.Session;
import org.university.configuration.SessionFactoryUtil;
import org.university.dto.CompanyRevenueDto;
import org.university.dto.EmployeeBuildingsCountDto;
import org.university.entity.Employee;
import org.university.entity.Resident;
import org.university.service.contract.manage_data_service.SortingService;

import java.util.List;

public class SortingServiceImpl implements SortingService {
    @Override
    public List<CompanyRevenueDto> sortCompaniesByCollectedFeesDesc() {
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT new org.university.dto.CompanyRevenueDto(" +
                            "c.id, c.name, sum(p.amount)) " +
                            "FROM Payment p " +
                            "INNER JOIN p.invoice i " +
                            "INNER JOIN i.apartment a " +
                            "INNER JOIN a.building b " +
                            "INNER JOIN b.employee e " +
                            "INNER JOIN e.company c " +
                            "GROUP BY c.id, c.name " +
                            "ORDER BY sum(p.amount) DESC, c.name ASC", CompanyRevenueDto.class
            ).getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error while sorting companies by collected fees: ", e);
        }
        finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<Employee> sortEmployeesByCompanyByName(Long companyId) {
        if(companyId == null){
            throw new IllegalArgumentException("Company id cannot be null");
        }
        Session session = null;
        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT e FROM Employee e " +
                            "WHERE e.company.id = :companyId " +
                            "ORDER BY e.firstName ASC, e.lastName ASC, e.id ASC",
                    Employee.class
            ).setParameter("companyId", companyId)
                    .getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error while sorting employees by company name: ", e);
        }
        finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<EmployeeBuildingsCountDto> sortEmployeesByCompanyByBuildingsCountDesc(Long companyId) {
        if(companyId == null){
            throw new IllegalArgumentException("Company id cannot be null");
        }

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "SELECT new org.university.dto.EmployeeBuildingsCountDto("+
                                    "e.id, e.firstName, e.lastName, COUNT(DISTINCT b.id) )" +
                                    "FROM Building b " +
                                    "INNER JOIN b.employee e " +
                                    "WHERE e.company.id = :companyId " +
                                    "GROUP BY e.id, e.firstName, e.lastName " +
                                    "ORDER BY COUNT(DISTINCT b.id) DESC, e.firstName ASC, e.lastName ASC, e.id ASC",
                            EmployeeBuildingsCountDto.class
                    ).setParameter("companyId", companyId)
                    .getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error while sorting employees by company name: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }


    }

    @Override
    public List<Resident> sortResidentsByBuildingByName(Long buildingId) {
        if(buildingId == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT r FROM Resident r " +
                            "WHERE r.apartment.building.id = :buildingId " +
                            "ORDER BY r.firstName ASC, r.lastName ASC, r.id ASC",
                    Resident.class
            ).setParameter("buildingId", buildingId)
                    .getResultList();
        }catch (Exception e){
            throw new RuntimeException("Error while sorting residents by building name: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<Resident> sortResidentsByBuildingByAge(Long buildingId) {
        if(buildingId == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT r FROM Resident r " +
                            "WHERE r.apartment.building.id = :buildingId " +
                            "ORDER BY r.age ASC, r.firstName ASC, r.lastName ASC, r.id ASC",
                    Resident.class
            ).setParameter("buildingId", buildingId)
                    .getResultList();
        }catch (Exception e){
            throw new RuntimeException("Error while sorting residents by building age: ", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }
}
