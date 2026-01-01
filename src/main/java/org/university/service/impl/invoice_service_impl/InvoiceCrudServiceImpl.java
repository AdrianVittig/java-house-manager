package org.university.service.impl.invoice_service_impl;

import org.university.dao.apartment_dao.ApartmentCrudDao;
import org.university.dao.building_dao.BuildingCrudDao;
import org.university.dao.invoice_dao.InvoiceCrudDao;
import org.university.dao.invoice_dao.InvoiceMapper;
import org.university.dto.InvoiceListDto;
import org.university.dto.InvoiceWithDetailsDto;
import org.university.entity.Apartment;
import org.university.entity.Building;
import org.university.entity.Employee;
import org.university.entity.Invoice;
import org.university.service.contract.calculate_fee_service.ApartmentPricingSystemService;
import org.university.service.contract.invoice_service.InvoiceCrudService;
import org.university.service.impl.calculate_fee_service_impl.ApartmentPricingSystemServiceImpl;
import org.university.util.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InvoiceCrudServiceImpl implements InvoiceCrudService {

    private final ApartmentCrudDao apartmentDao = new ApartmentCrudDao();
    private final InvoiceCrudDao invoiceDao = new InvoiceCrudDao();
    private final InvoiceMapper invoiceMapper = new InvoiceMapper();
    private final ApartmentPricingSystemService apartmentPricingSystemService = new ApartmentPricingSystemServiceImpl();
    private final BuildingCrudDao buildingDao = new BuildingCrudDao();

    @Override
    public void createInvoiceForApartment(Long apartmentId, YearMonth billingMonth) {
        if (apartmentId == null) {
            throw new IllegalArgumentException("Apartment id cannot be null");
        }
        if (billingMonth == null) {
            throw new IllegalArgumentException("Billing month cannot be null");
        }

        Apartment apartment = apartmentDao.getApartmentWithBuildingAndEmployee(apartmentId);
        if (apartment == null) {
            throw new IllegalArgumentException("Apartment with id " + apartmentId + " does not exist");
        }

        Invoice existing = invoiceDao.getInvoiceByApartmentAndMonth(apartmentId, billingMonth);
        if (existing != null) {
            throw new IllegalArgumentException(
                    "Invoice for apartment with id " + apartmentId + " and billing month " + billingMonth + " already exists"
            );
        }

        Building building = apartment.getBuilding();
        if (building == null) {
            throw new IllegalArgumentException("Apartment must belong to a building");
        }

        Employee employee = building.getEmployee();
        if (employee == null) {
            throw new IllegalArgumentException("Building must have an employee to determine due date");
        }

        LocalDate collectingDate = employee.getFeeCollectingDate();
        if (collectingDate == null) {
            throw new IllegalArgumentException("Employee fee collecting date cannot be null");
        }

        int dayOfMonth = collectingDate.getDayOfMonth();
        LocalDate dueDate = billingMonth.atDay(Math.min(dayOfMonth, billingMonth.lengthOfMonth()));

        BigDecimal totalAmount = apartmentPricingSystemService.calculateFee(apartment);
        if (totalAmount == null) {
            throw new IllegalArgumentException("Calculated total amount cannot be null");
        }

        Invoice invoice = new Invoice();
        invoice.setApartment(apartment);
        invoice.setBillingMonth(billingMonth);
        invoice.setTotalAmount(totalAmount);
        invoice.setPaymentStatus(PaymentStatus.NOT_PAID);
        invoice.setDueDate(dueDate);

        invoiceDao.createInvoice(invoice);
    }

    @Override
    public void createInvoicesForBuilding(Long buildingId, YearMonth billingMonth) {
        if(buildingId == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }
        if(billingMonth == null){
            throw new IllegalArgumentException("Billing month cannot be null");
        }

        Building building = buildingDao.getBuildingWithApartmentsAndEmployee(buildingId);
        if(building == null){
            throw new IllegalArgumentException("Building with id " + buildingId + " does not exist");
        }

        if(building.getEmployee() == null){
            throw new IllegalArgumentException("Building with id " + buildingId + " does not have an employee");
        }

        if(building.getApartmentList() == null || building.getApartmentList().isEmpty()){
            return;
        }

        List<Invoice> invoiceList = invoiceDao.getInvoicesByBuildingAndMonth(buildingId, billingMonth);

        Set<Long> apartmentIdsWithInvoice = invoiceList.stream()
                .filter(invoice -> invoice != null && invoice.getApartment() != null
                && invoice.getApartment().getId() != null)
                .map(invoice -> invoice.getApartment().getId())
                .collect(Collectors.toSet());

        for(Apartment apartment : building.getApartmentList()){
            if(apartment == null || apartment.getId() == null) continue;
            if(apartmentIdsWithInvoice.contains(apartment.getId())) continue;
            try{
                createInvoiceForApartment(apartment.getId(), billingMonth);
            }catch (IllegalArgumentException e){
                if(e.getMessage() != null && e.getMessage().contains("already exists")){
                    continue;
                }
                throw new IllegalArgumentException("Error creating invoice for apartment with id " + apartment.getId(), e);
            }
        }
    }

    @Override
    public InvoiceWithDetailsDto getInvoiceById(Long invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice id cannot be null");
        }

        Invoice invoice = invoiceDao.getInvoiceWithDetails(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice with id " + invoiceId + " does not exist");
        }

        return invoiceMapper.toDetailsDto(invoice);
    }

    @Override
    public InvoiceWithDetailsDto getInvoiceByApartmentAndMonth(Long apartmentId, YearMonth billingMonth) {
        if (apartmentId == null) {
            throw new IllegalArgumentException("Apartment id cannot be null");
        }
        if (billingMonth == null) {
            throw new IllegalArgumentException("Billing month cannot be null");
        }

        Invoice invoice = invoiceDao.getInvoiceByApartmentAndMonth(apartmentId, billingMonth);
        if (invoice == null) {
            throw new IllegalArgumentException(
                    "Invoice for apartment with id " + apartmentId + " and billing month " + billingMonth + " does not exist"
            );
        }

        return invoiceMapper.toDetailsDto(invoice);
    }

    @Override
    public List<InvoiceListDto> getInvoicesByApartment(Long apartmentId) {
        if (apartmentId == null) {
            throw new IllegalArgumentException("Apartment id cannot be null");
        }

        Apartment apartment = apartmentDao.getApartmentById(apartmentId);
        if (apartment == null) {
            throw new IllegalArgumentException("Apartment with id " + apartmentId + " does not exist");
        }

        return invoiceDao.getInvoicesByApartment(apartmentId)
                .stream()
                .map(invoiceMapper::toListDto)
                .toList();
    }

    @Override
    public List<InvoiceListDto> getInvoicesByBuilding(Long buildingId) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building id cannot be null");
        }

        return invoiceDao.getInvoicesByBuilding(buildingId)
                .stream()
                .map(invoiceMapper::toListDto)
                .toList();
    }

    @Override
    public List<InvoiceListDto> getAllInvoices() {
        return invoiceDao.getAllInvoices()
                .stream()
                .map(invoiceMapper::toListDto)
                .toList();
    }


    @Override
    public void updateInvoice(Long invoiceId, Invoice invoice) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice id cannot be null");
        }
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }

        if (invoice.getId() != null && !invoiceId.equals(invoice.getId())) {
            throw new IllegalArgumentException(
                    "Invoice id mismatch: id=" + invoiceId + ", invoice.id=" + invoice.getId()
            );
        }

        invoice.setId(invoiceId);

        invoiceDao.updateInvoice(invoiceId, invoice);
    }

    @Override
    public void deleteInvoice(Long invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice id cannot be null");
        }

        Invoice invoice = invoiceDao.getInvoiceById(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice with id " + invoiceId + " does not exist");
        }

        invoiceDao.deleteInvoice(invoiceId);
    }
}
