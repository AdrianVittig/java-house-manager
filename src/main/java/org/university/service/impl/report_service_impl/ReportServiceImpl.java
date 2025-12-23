package org.university.service.impl.report_service_impl;

import org.hibernate.Session;
import org.university.configuration.SessionFactoryUtil;
import org.university.dto.BuildingAmountDto;
import org.university.dto.CompanyAmountDto;
import org.university.dto.EmployeeAmountDto;
import org.university.dto.EmployeeBuildingsCountDto;
import org.university.entity.*;
import org.university.service.contract.report_service.ReportService;
import org.university.util.PaymentStatus;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

public class ReportServiceImpl implements ReportService {
    @Override
    public List<EmployeeBuildingsCountDto> getBuildingsCountByEmployeesForCompany(Long companyId) {
        if(companyId == null){
            throw new IllegalArgumentException("Company id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT new org.university.dto.EmployeeBuildingsCountDto(" +
                            "e.id, e.firstName, e.lastName, COUNT(DISTINCT b.id)) " +
                            "FROM Building b " +
                            "INNER JOIN b.employee e " +
                            "WHERE e.company.id = :companyId " +
                            "GROUP BY e.id, e.firstName, e.lastName " +
                            "ORDER BY e.firstName ASC, e.lastName ASC, e.id ASC",
                    EmployeeBuildingsCountDto.class
            ).setParameter("companyId", companyId)
                    .getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error while getting buildings count by employees for company id: " + companyId, e);
        }
        finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long getCountBuildingsByEmployee(Long employeeId) {
        if(employeeId == null){
            throw new IllegalArgumentException("Employee id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT COUNT(b.id) from Building b " +
                            "WHERE b.employee.id = :employeeId",
                            Long.class
                    )
                    .setParameter("employeeId", employeeId)
                    .uniqueResult();
        }catch(Exception e){
            throw new RuntimeException("Error while getting count buildings by employee id: " + employeeId, e);
        }
        finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<Building> getBuildingsByEmployee(Long employeeId) {
        if(employeeId == null){
            throw new IllegalArgumentException("Employee id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT b FROM Building b " +
                            "WHERE b.employee.id = :employeeId",
                            Building.class
                    )
                    .setParameter("employeeId", employeeId)
                    .getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error while getting buildings by employee id: " + employeeId, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long countApartmentsByBuilding(Long buildingId) {
        if(buildingId == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT COUNT(a.id) FROM Apartment a " +
                            "WHERE a.building.id = :buildingId",
                    Long.class
                    ).setParameter("buildingId", buildingId)
                    .uniqueResult();
        }catch(Exception e){
            throw new RuntimeException("Error while getting count apartments by building id: " + buildingId, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<Apartment> getApartmentsByBuilding(Long buildingId) {
        if(buildingId == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT a FROM Apartment a " +
                            "WHERE a.building.id = :buildingId",
                    Apartment.class
                    ).setParameter("buildingId", buildingId)
                    .getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error while getting apartments by building id: " + buildingId, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long countResidentsByBuilding(Long buildingId) {
        if(buildingId == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT COUNT(r.id) FROM Resident r " +
                            "WHERE r.apartment.building.id = :buildingId ",
                    Long.class
                    ).setParameter("buildingId", buildingId)
                    .uniqueResult();
        }catch(Exception e){
            throw new RuntimeException("Error while getting count residents by building id: " + buildingId, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<Resident> getResidentsByBuilding(Long buildingId) {
        if(buildingId == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();
            return session.createQuery(
                    "SELECT r FROM Resident r " +
                            "WHERE r.apartment.building.id = :buildingId ",
                    Resident.class
                    ).setParameter("buildingId", buildingId)
                    .getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error while getting residents by building id: " + buildingId, e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long countCompaniesForAmountsToPay(YearMonth billingMonth) {
        return getAmountsToPayByCompany(billingMonth).size();
    }

    @Override
    public List<CompanyAmountDto> getAmountsToPayByCompany(YearMonth billingMonth) {
        if(billingMonth == null){
            throw new IllegalArgumentException("Billing month cannot be null");
        }

        Session session = null;

        try{
            session = SessionFactoryUtil.getSessionFactory().openSession();

            List<Company> companyList = session.createQuery(
                    "SELECT c FROM Company c", Company.class
                    ).getResultList();

            Map<Long, CompanyAmountDto> res = new HashMap<>();

            for(Company c : companyList){
                if(c == null || c.getId() == null){
                    continue;
                }
                res.put(c.getId(), new CompanyAmountDto(c.getId(), c.getName(), BigDecimal.ZERO));
            }

            List<CompanyAmountDto> sum = session.createQuery(
                            "SELECT new org.university.dto.CompanyAmountDto(" +
                                    "c.id, c.name, SUM(i.totalAmount)) " +
                                    "FROM Invoice i " +
                                    "INNER JOIN i.apartment a " +
                                    "INNER JOIN a.building b " +
                                    "INNER JOIN b.employee e " +
                                    "INNER JOIN e.company c " +
                                    "WHERE i.billingMonth = :billingMonth " +
                                    "AND i.paymentStatus <> :paidStatus " +
                                    "GROUP BY c.id, c.name",
                            CompanyAmountDto.class
                    ).setParameter("billingMonth", billingMonth)
                    .setParameter("paidStatus", PaymentStatus.PAID)
                    .getResultList();

            for (CompanyAmountDto dto : sum) {
                if (dto == null || dto.getCompanyId() == null) continue;
                res.put(dto.getCompanyId(), dto);
            }

            ArrayList<CompanyAmountDto> list = new ArrayList<>(res.values());
            list.sort(Comparator
                    .comparing(CompanyAmountDto::getAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed()
                    .thenComparing(CompanyAmountDto::getCompanyName, Comparator.nullsLast(String::compareToIgnoreCase)));
            return list;
        }catch(Exception e){
            throw new RuntimeException("Error while getting companies for amounts to pay", e);
        }finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long countBuildingsForAmountsToPay(YearMonth billingMonth) {
        return getAmountsToPayByBuilding(billingMonth).size();
    }

    @Override
    public List<BuildingAmountDto> getAmountsToPayByBuilding(YearMonth billingMonth) {
        if (billingMonth == null) throw new IllegalArgumentException("Billing month cannot be null");

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            List<Building> buildings = session.createQuery(
                    "SELECT b FROM Building b ORDER BY b.name ASC, b.id ASC",
                    Building.class
            ).getResultList();

            Map<Long, BuildingAmountDto> result = new LinkedHashMap<>();
            for (Building b : buildings) {
                if (b == null || b.getId() == null) continue;
                result.put(b.getId(), new BuildingAmountDto(b.getId(), b.getName(), b.getAddress(), BigDecimal.ZERO));
            }

            List<BuildingAmountDto> sums = session.createQuery(
                            "SELECT new org.university.dto.BuildingAmountDto(" +
                                    "b.id, b.name, b.address, SUM(i.totalAmount)) " +
                                    "FROM Invoice i " +
                                    "INNER JOIN i.apartment a " +
                                    "INNER JOIN a.building b " +
                                    "WHERE i.billingMonth = :billingMonth " +
                                    "AND i.paymentStatus <> :paidStatus " +
                                    "GROUP BY b.id, b.name, b.address",
                            BuildingAmountDto.class
                    ).setParameter("billingMonth", billingMonth)
                    .setParameter("paidStatus", PaymentStatus.PAID)
                    .getResultList();

            for (BuildingAmountDto dto : sums) {
                if (dto == null || dto.getBuildingId() == null) continue;
                result.put(dto.getBuildingId(), dto);
            }

            ArrayList<BuildingAmountDto> list = new ArrayList<>(result.values());
            list.sort(Comparator
                    .comparing(BuildingAmountDto::getAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed()
                    .thenComparing(BuildingAmountDto::getBuildingName, Comparator.nullsLast(String::compareToIgnoreCase)));
            return list;
        }catch(Exception e){
            throw new RuntimeException("Error while getting buildings for amounts to pay", e);
        }
        finally{
            if(session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long countEmployeesForAmountsToPay(YearMonth billingMonth) {
        return getAmountsToPayByEmployee(billingMonth).size();
    }

    @Override
    public List<EmployeeAmountDto> getAmountsToPayByEmployee(YearMonth billingMonth) {
        if (billingMonth == null) throw new IllegalArgumentException("Billing month cannot be null");

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            List<Employee> employees = session.createQuery(
                    "SELECT e FROM Employee e ORDER BY e.firstName ASC, e.lastName ASC, e.id ASC",
                    Employee.class
            ).getResultList();

            Map<Long, EmployeeAmountDto> result = new LinkedHashMap<>();
            for (Employee e : employees) {
                if (e == null || e.getId() == null) continue;
                result.put(e.getId(), new EmployeeAmountDto(e.getId(), e.getFirstName(), e.getLastName(), BigDecimal.ZERO));
            }

            List<EmployeeAmountDto> sums = session.createQuery(
                            "SELECT new org.university.dto.EmployeeAmountDto(" +
                                    "e.id, e.firstName, e.lastName, SUM(i.totalAmount)) " +
                                    "FROM Invoice i " +
                                    "INNER JOIN i.apartment a " +
                                    "INNER JOIN a.building b " +
                                    "INNER JOIN b.employee e " +
                                    "WHERE i.billingMonth = :billingMonth " +
                                    "AND i.paymentStatus <> :paidStatus " +
                                    "GROUP BY e.id, e.firstName, e.lastName",
                            EmployeeAmountDto.class
                    ).setParameter("billingMonth", billingMonth)
                    .setParameter("paidStatus", PaymentStatus.PAID)
                    .getResultList();

            for (EmployeeAmountDto dto : sums) {
                if (dto == null || dto.getEmployeeId() == null) continue;
                result.put(dto.getEmployeeId(), dto);
            }

            ArrayList<EmployeeAmountDto> list = new ArrayList<>(result.values());
            list.sort(Comparator
                    .comparing(EmployeeAmountDto::getAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed()
                    .thenComparing(EmployeeAmountDto::getFirstName, Comparator.nullsLast(String::compareToIgnoreCase))
                    .thenComparing(EmployeeAmountDto::getLastName, Comparator.nullsLast(String::compareToIgnoreCase)));
            return list;

        } catch (Exception e) {
            throw new RuntimeException("Error while getting amounts to pay by employee", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long countCompaniesForPaidAmounts(YearMonth billingMonth) {
        return getPaidAmountsByCompany(billingMonth).size();
    }

    @Override
    public List<CompanyAmountDto> getPaidAmountsByCompany(YearMonth billingMonth) {
        if (billingMonth == null) throw new IllegalArgumentException("Billing month cannot be null");

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            List<Company> companies = session.createQuery(
                    "SELECT c FROM Company c ORDER BY c.name ASC, c.id ASC",
                    Company.class
            ).getResultList();

            Map<Long, CompanyAmountDto> result = new LinkedHashMap<>();
            for (Company c : companies) {
                if (c == null || c.getId() == null) continue;
                result.put(c.getId(), new CompanyAmountDto(c.getId(), c.getName(), BigDecimal.ZERO));
            }

            List<CompanyAmountDto> sums = session.createQuery(
                            "SELECT new org.university.dto.CompanyAmountDto(" +
                                    "c.id, c.name, SUM(p.amount)) " +
                                    "FROM Payment p " +
                                    "INNER JOIN p.invoice i " +
                                    "INNER JOIN i.apartment a " +
                                    "INNER JOIN a.building b " +
                                    "INNER JOIN b.employee e " +
                                    "INNER JOIN e.company c " +
                                    "WHERE i.billingMonth = :billingMonth " +
                                    "AND p.paymentStatus = :paidStatus " +
                                    "GROUP BY c.id, c.name",
                            CompanyAmountDto.class
                    ).setParameter("billingMonth", billingMonth)
                    .setParameter("paidStatus", PaymentStatus.PAID)
                    .getResultList();

            for (CompanyAmountDto dto : sums) {
                if (dto == null || dto.getCompanyId() == null) continue;
                result.put(dto.getCompanyId(), dto);
            }

            ArrayList<CompanyAmountDto> list = new ArrayList<>(result.values());
            list.sort(Comparator
                    .comparing(CompanyAmountDto::getAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed()
                    .thenComparing(CompanyAmountDto::getCompanyName, Comparator.nullsLast(String::compareToIgnoreCase)));
            return list;

        } catch (Exception e) {
            throw new RuntimeException("Error while getting paid amounts by company", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long countBuildingsForPaidAmounts(YearMonth billingMonth) {
        return getPaidAmountsByBuilding(billingMonth).size();
    }

    @Override
    public List<BuildingAmountDto> getPaidAmountsByBuilding(YearMonth billingMonth) {
        if (billingMonth == null) throw new IllegalArgumentException("Billing month cannot be null");

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            List<Building> buildings = session.createQuery(
                    "SELECT b FROM Building b ORDER BY b.name ASC, b.id ASC",
                    Building.class
            ).getResultList();

            Map<Long, BuildingAmountDto> result = new LinkedHashMap<>();
            for (Building b : buildings) {
                if (b == null || b.getId() == null) continue;
                result.put(b.getId(), new BuildingAmountDto(b.getId(), b.getName(), b.getAddress(), BigDecimal.ZERO));
            }

            List<BuildingAmountDto> sums = session.createQuery(
                            "SELECT new org.university.dto.BuildingAmountDto(" +
                                    "b.id, b.name, b.address, SUM(p.amount)) " +
                                    "FROM Payment p " +
                                    "INNER JOIN p.invoice i " +
                                    "INNER JOIN i.apartment a " +
                                    "INNER JOIN a.building b " +
                                    "WHERE i.billingMonth = :billingMonth " +
                                    "AND p.paymentStatus = :paidStatus " +
                                    "GROUP BY b.id, b.name, b.address",
                            BuildingAmountDto.class
                    ).setParameter("billingMonth", billingMonth)
                    .setParameter("paidStatus", PaymentStatus.PAID)
                    .getResultList();

            for (BuildingAmountDto dto : sums) {
                if (dto == null || dto.getBuildingId() == null) continue;
                result.put(dto.getBuildingId(), dto);
            }

            ArrayList<BuildingAmountDto> list = new ArrayList<>(result.values());
            list.sort(Comparator
                    .comparing(BuildingAmountDto::getAmount, Comparator.nullsFirst(BigDecimal::compareTo)).reversed()
                    .thenComparing(BuildingAmountDto::getBuildingName, Comparator.nullsLast(String::compareToIgnoreCase)));
            return list;

        } catch (Exception e) {
            throw new RuntimeException("Error while getting paid amounts by building", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public long countEmployeesForPaidAmounts(YearMonth billingMonth) {
        return getPaidAmountsByEmployee(billingMonth).size();
    }

    @Override
    public List<EmployeeAmountDto> getPaidAmountsByEmployee(YearMonth billingMonth) {
        if (billingMonth == null) throw new IllegalArgumentException("Billing month cannot be null");

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            List<Employee> employees = session.createQuery(
                    "SELECT e FROM Employee e ORDER BY e.firstName ASC, e.lastName ASC, e.id ASC",
                    Employee.class
            ).getResultList();

            Map<Long, EmployeeAmountDto> result = new LinkedHashMap<>();
            for (Employee e : employees) {
                if (e == null || e.getId() == null) continue;
                result.put(e.getId(), new EmployeeAmountDto(
                        e.getId(), e.getFirstName(), e.getLastName(), BigDecimal.ZERO
                ));
            }

            List<EmployeeAmountDto> sums = session.createQuery(
                            "SELECT new org.university.dto.EmployeeAmountDto(" +
                                    "e.id, e.firstName, e.lastName, SUM(p.amount)) " +
                                    "FROM Payment p " +
                                    "INNER JOIN p.invoice i " +
                                    "INNER JOIN i.apartment a " +
                                    "INNER JOIN a.building b " +
                                    "INNER JOIN b.employee e " +
                                    "WHERE i.billingMonth = :billingMonth " +
                                    "AND p.paymentStatus = :paidStatus " +
                                    "GROUP BY e.id, e.firstName, e.lastName",
                            EmployeeAmountDto.class
                    ).setParameter("billingMonth", billingMonth)
                    .setParameter("paidStatus", PaymentStatus.PAID)
                    .getResultList();

            for (EmployeeAmountDto dto : sums) {
                if (dto == null || dto.getEmployeeId() == null) continue;
                result.put(dto.getEmployeeId(), dto);
            }

            return new ArrayList<>(result.values());

        } catch (Exception e) {
            throw new RuntimeException("Error while getting paid amounts by building", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }
}
