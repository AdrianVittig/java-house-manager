package org.university.service.impl.manage_data_service_impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.university.configuration.SessionFactoryUtil;
import org.university.dto.CompanyRevenueDto;
import org.university.dto.EmployeeBuildingsCountDto;
import org.university.entity.*;
import org.university.service.contract.manage_data_service.FilteringService;

import java.math.BigDecimal;
import java.util.*;

public class FilteringServiceImpl implements FilteringService {

    @Override
    public List<CompanyRevenueDto> filterCompaniesByMinCollectedFees(BigDecimal minCollectedFees) {
        if (minCollectedFees == null) {
            throw new IllegalArgumentException("Min collected fees cannot be null");
        }
        if (minCollectedFees.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Min collected fees cannot be negative");
        }

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();

            CriteriaQuery<Company> cqCompanies = cb.createQuery(Company.class);
            Root<Company> cRoot = cqCompanies.from(Company.class);
            cqCompanies.select(cRoot);
            List<Company> companies = session.createQuery(cqCompanies).getResultList();

            Map<Long, String> companyNameById = new HashMap<>();
            Map<Long, BigDecimal> sumByCompanyId = new HashMap<>();

            for (Company c : companies) {
                if (c == null || c.getId() == null) continue;
                companyNameById.put(c.getId(), c.getName());
                sumByCompanyId.put(c.getId(), BigDecimal.ZERO);
            }

            CriteriaQuery<Payment> cqPayments = cb.createQuery(Payment.class);
            Root<Payment> pRoot = cqPayments.from(Payment.class);
            cqPayments.select(pRoot);
            List<Payment> paymentList = session.createQuery(cqPayments).getResultList();

            for (Payment payment : paymentList) {
                if (payment == null || payment.getInvoice() == null) continue;

                Invoice invoice = payment.getInvoice();
                if (invoice.getApartment() == null) continue;

                Apartment apartment = invoice.getApartment();
                if (apartment.getBuilding() == null) continue;

                Building building = apartment.getBuilding();
                if (building.getEmployee() == null) continue;

                Employee employee = building.getEmployee();
                if (employee.getCompany() == null || employee.getCompany().getId() == null) continue;

                Long companyId = employee.getCompany().getId();

                BigDecimal amount = payment.getAmount();
                if (amount == null) amount = BigDecimal.ZERO;

                sumByCompanyId.merge(companyId, amount, BigDecimal::add);
                companyNameById.putIfAbsent(companyId, employee.getCompany().getName());
            }

            List<CompanyRevenueDto> result = new ArrayList<>();
            for (Map.Entry<Long, BigDecimal> entry : sumByCompanyId.entrySet()) {
                Long companyId = entry.getKey();
                BigDecimal sum = entry.getValue();

                if (sum.compareTo(minCollectedFees) >= 0) {
                    result.add(new CompanyRevenueDto(companyId, companyNameById.get(companyId), sum));
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error while filtering companies by collected fees: ", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<Employee> filterEmployeesByCompanyName(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be null/blank");
        }

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);

            Predicate byCompany = cb.equal(root.get("company").get("name"), companyName);

            cq.select(root).where(byCompany);
            return session.createQuery(cq).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Error while filtering employees by company name: ", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<EmployeeBuildingsCountDto> filterEmployeesByCompanyWithMinBuildings(Long companyId, Integer minBuildings) {
        if (companyId == null) {
            throw new IllegalArgumentException("Company id cannot be null");
        }
        if (minBuildings == null) {
            throw new IllegalArgumentException("Min buildings cannot be null");
        }
        if (minBuildings < 0) {
            throw new IllegalArgumentException("Min buildings cannot be negative");
        }

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();

            CriteriaQuery<Employee> cqEmployees = cb.createQuery(Employee.class);
            Root<Employee> eRoot = cqEmployees.from(Employee.class);
            Predicate byCompanyEmployees = cb.equal(eRoot.get("company").get("id"), companyId);
            cqEmployees.select(eRoot).where(byCompanyEmployees);
            List<Employee> employees = session.createQuery(cqEmployees).getResultList();

            Map<Long, Employee> employeeById = new HashMap<>();
            Map<Long, Long> countByEmployeeId = new HashMap<>();

            for (Employee e : employees) {
                if (e == null || e.getId() == null) continue;
                employeeById.put(e.getId(), e);
                countByEmployeeId.put(e.getId(), 0L);
            }

            CriteriaQuery<Building> cqBuildings = cb.createQuery(Building.class);
            Root<Building> bRoot = cqBuildings.from(Building.class);
            Predicate byCompanyBuildings = cb.equal(bRoot.get("employee").get("company").get("id"), companyId);
            cqBuildings.select(bRoot).where(byCompanyBuildings);
            List<Building> buildings = session.createQuery(cqBuildings).getResultList();

            for (Building building : buildings) {
                if (building == null || building.getEmployee() == null || building.getEmployee().getId() == null) continue;
                Long employeeId = building.getEmployee().getId();
                countByEmployeeId.merge(employeeId, 1L, Long::sum);
            }

            List<EmployeeBuildingsCountDto> res = new ArrayList<>();
            for (Map.Entry<Long, Long> entry : countByEmployeeId.entrySet()) {
                Long employeeId = entry.getKey();
                Long count = entry.getValue();

                if (count >= minBuildings) {
                    Employee e = employeeById.get(employeeId);
                    if (e == null) continue;

                    res.add(new EmployeeBuildingsCountDto(
                            employeeId,
                            e.getFirstName(),
                            e.getLastName(),
                            count
                    ));
                }
            }

            return res;

        } catch (Exception e) {
            throw new RuntimeException("Error while filtering employees by company with min buildings: ", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<Resident> filterResidentsByBuildingByName(Long buildingId, String firstName) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building id cannot be null");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null/blank");
        }

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Resident> cq = cb.createQuery(Resident.class);
            Root<Resident> root = cq.from(Resident.class);

            Predicate byBuilding = cb.equal(root.get("apartment").get("building").get("id"), buildingId);
            Predicate byFirstName = cb.like(root.get("firstName"), "%" + firstName + "%");

            cq.select(root).where(cb.and(byBuilding, byFirstName));
            return session.createQuery(cq).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Error while filtering residents by building name: ", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<Resident> filterResidentsByBuildingByAge(Long buildingId, Integer minAge, Integer maxAge) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building id cannot be null");
        }
        if (minAge == null || maxAge == null) {
            throw new IllegalArgumentException("Min age and max age cannot be null");
        }
        if (minAge < 0 || maxAge < 0) {
            throw new IllegalArgumentException("Min age and max age cannot be negative");
        }
        if (minAge > maxAge) {
            throw new IllegalArgumentException("Min age cannot be greater than max age");
        }

        Session session = null;
        try {
            session = SessionFactoryUtil.getSessionFactory().openSession();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Resident> cq = cb.createQuery(Resident.class);
            Root<Resident> root = cq.from(Resident.class);

            Predicate byBuilding = cb.equal(root.get("apartment").get("building").get("id"), buildingId);
            Predicate byAge = cb.between(root.get("age"), minAge, maxAge);

            cq.select(root).where(cb.and(byBuilding, byAge));
            return session.createQuery(cq).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Error while filtering residents by building age: ", e);
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
    }
}
