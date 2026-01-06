package org.university;

import org.university.configuration.SessionFactoryUtil;
import org.university.dto.*;
import org.university.entity.*;
import org.university.service.contract.apartment_service.ApartmentCrudService;
import org.university.service.contract.building_service.BuildingCrudService;
import org.university.service.contract.calculate_fee_service.ApartmentPricingSystemService;
import org.university.service.contract.calculate_fee_service.BuildingPricingSystemService;
import org.university.service.contract.company_service.CompanyCrudService;
import org.university.service.contract.contract_service.ContractCrudService;
import org.university.service.contract.employee_service.EmployeeAllocationService;
import org.university.service.contract.employee_service.EmployeeCrudService;
import org.university.service.contract.file_manage_service.FileService;
import org.university.service.contract.invoice_service.InvoiceCrudService;
import org.university.service.contract.manage_data_service.FilteringService;
import org.university.service.contract.manage_data_service.SortingService;
import org.university.service.contract.payment_service.FeeCollectionService;
import org.university.service.contract.payment_service.PaymentCrudService;
import org.university.service.contract.person_service.PersonCrudService;
import org.university.service.contract.report_service.ReportService;
import org.university.service.contract.resident_service.ResidentCrudService;
import org.university.service.impl.apartment_service_impl.ApartmentCrudServiceImpl;
import org.university.service.impl.building_service_impl.BuildingCrudServiceImpl;
import org.university.service.impl.calculate_fee_service_impl.ApartmentPricingSystemServiceImpl;
import org.university.service.impl.calculate_fee_service_impl.BuildingPricingSystemServiceImpl;
import org.university.service.impl.company_service_impl.CompanyCrudServiceImpl;
import org.university.service.impl.contract_service_impl.ContractCrudServiceImpl;
import org.university.service.impl.employee_service_impl.EmployeeAllocationServiceImpl;
import org.university.service.impl.employee_service_impl.EmployeeCrudServiceImpl;
import org.university.service.impl.file_manage_service_impl.FileServiceImpl;
import org.university.service.impl.invoice_service_impl.InvoiceCrudServiceImpl;
import org.university.service.impl.manage_data_service_impl.FilteringServiceImpl;
import org.university.service.impl.manage_data_service_impl.SortingServiceImpl;
import org.university.service.impl.payment_service_impl.FeeCollectionServiceImpl;
import org.university.service.impl.payment_service_impl.PaymentCrudServiceImpl;
import org.university.service.impl.person_service_impl.PersonCrudServiceImpl;
import org.university.service.impl.report_service_impl.ReportServiceImpl;
import org.university.service.impl.resident_service_impl.ResidentCrudServiceImpl;
import org.university.util.PaymentStatus;
import org.university.util.ResidentRole;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Scanner;

public class Runner {

    private final ApartmentCrudService apartmentCrudService = new ApartmentCrudServiceImpl();
    private final BuildingCrudService buildingCrudService = new BuildingCrudServiceImpl();
    private final ApartmentPricingSystemService apartmentPricingSystemService = new ApartmentPricingSystemServiceImpl();
    private final BuildingPricingSystemService buildingPricingSystemService = new BuildingPricingSystemServiceImpl();
    private final CompanyCrudService companyCrudService = new CompanyCrudServiceImpl();
    private final EmployeeCrudService employeeCrudService = new EmployeeCrudServiceImpl();
    private final EmployeeAllocationService employeeAllocationService = new EmployeeAllocationServiceImpl();
    private final ContractCrudService contractCrudService = new ContractCrudServiceImpl();
    private final InvoiceCrudService invoiceCrudService = new InvoiceCrudServiceImpl();
    private final PaymentCrudService paymentCrudService = new PaymentCrudServiceImpl();
    private final FeeCollectionService feeCollectionService = new FeeCollectionServiceImpl();
    private final FileService fileService = new FileServiceImpl();
    private final FilteringService filteringService = new FilteringServiceImpl();
    private final SortingService sortingService = new SortingServiceImpl();
    private final ReportService reportService = new ReportServiceImpl();
    private final ResidentCrudService residentCrudService = new ResidentCrudServiceImpl();
    private final PersonCrudService personCrudService = new PersonCrudServiceImpl();

    private final Scanner sc = new Scanner(System.in);

    public void run() {
        try {
            printMainMenu();
            while (true) {
                String cmd = read("\ncmd (menu): ");
                try {
                    switch (cmd) {
                        case "menu" -> printMainMenu();
                        case "0", "exit", "quit" -> { return; }

                        case "10" -> createCompany();
                        case "11" -> getCompanyById();
                        case "12" -> listCompanies();
                        case "13" -> updateCompany();
                        case "14" -> deleteCompany();

                        case "20" -> createEmployee();
                        case "21" -> getEmployeeById();
                        case "22" -> listEmployees();
                        case "23" -> getEmployeeWithLeastBuildings();
                        case "24" -> updateEmployee();
                        case "25" -> deleteEmployee();

                        case "30" -> createBuilding();
                        case "31" -> getBuildingById();
                        case "32" -> listBuildings();
                        case "33" -> updateBuilding();
                        case "34" -> deleteBuilding();
                        case "35" -> allocateEmployeeToBuilding();
                        case "36" -> reallocateEmployeeBuildings();

                        case "40" -> createApartment();
                        case "41" -> getApartmentById();
                        case "42" -> listApartments();
                        case "43" -> updateApartment();
                        case "44" -> deleteApartment();

                        case "50" -> createResident();
                        case "51" -> getResidentById();
                        case "52" -> listResidents();
                        case "53" -> updateResident();
                        case "54" -> deleteResident();

                        case "60" -> createPerson();
                        case "61" -> getPersonById();
                        case "62" -> listPeople();
                        case "63" -> updatePerson();
                        case "64" -> deletePerson();

                        case "70" -> createContract();
                        case "71" -> getContractById();
                        case "72" -> listContracts();
                        case "73" -> updateContract();
                        case "74" -> deleteContract();

                        case "80" -> calcApartmentFee();
                        case "81" -> calcBuildingFee();

                        case "90" -> createInvoiceForApartment();
                        case "91" -> createInvoicesForBuilding();
                        case "92" -> getInvoiceById();
                        case "93" -> getInvoiceByApartmentAndMonth();
                        case "94" -> listInvoicesByApartment();
                        case "95" -> listInvoicesByBuilding();
                        case "96" -> listAllInvoices();
                        case "97" -> updateInvoice();
                        case "98" -> deleteInvoice();

                        case "100" -> createPayment();
                        case "101" -> getPaymentById();
                        case "102" -> getPaymentByInvoiceId();
                        case "103" -> listPaymentsByApartment();
                        case "104" -> listPaymentsByBuilding();
                        case "105" -> listPaymentsByBuildingAndMonth();
                        case "106" -> listAllPayments();
                        case "107" -> updatePayment();
                        case "108" -> deletePayment();

                        case "110" -> payInvoiceAndSerialize();
                        case "111" -> collectFeesForBuilding();

                        case "120" -> readPaidInvoiceFile();

                        case "130" -> filterCompaniesByMinCollectedFees();
                        case "131" -> filterEmployeesByCompanyName();
                        case "132" -> filterEmployeesByCompanyWithMinBuildings();
                        case "133" -> filterResidentsByBuildingByName();
                        case "134" -> filterResidentsByBuildingByAge();

                        case "140" -> sortCompaniesByCollectedFeesDesc();
                        case "141" -> sortEmployeesByCompanyByName();
                        case "142" -> sortEmployeesByCompanyByBuildingsCountDesc();
                        case "143" -> sortResidentsByBuildingByName();
                        case "144" -> sortResidentsByBuildingByAge();

                        case "150" -> reportBuildingsCountByEmployeesForCompany();
                        case "151" -> reportCountBuildingsByEmployee();
                        case "152" -> reportBuildingsByEmployee();
                        case "153" -> reportCountApartmentsByBuilding();
                        case "154" -> reportApartmentsByBuilding();
                        case "155" -> reportCountResidentsByBuilding();
                        case "156" -> reportResidentsByBuilding();
                        case "157" -> reportAmountsToPayByCompany();
                        case "158" -> reportAmountsToPayByBuilding();
                        case "159" -> reportAmountsToPayByEmployee();
                        case "160" -> reportPaidAmountsByCompany();
                        case "161" -> reportPaidAmountsByBuilding();
                        case "162" -> reportPaidAmountsByEmployee();

                        default -> System.out.println("Unknown cmd. Type 'menu'.");
                    }
                } catch (Exception e) {
                    printError(e);
                }
            }
        } finally {
            try { SessionFactoryUtil.getSessionFactory().close(); } catch (Exception ignored) {}
        }
    }


    private void printMainMenu() {
        System.out.println("""
                ========= COMMANDS (type number) =========
                0  exit

                Company:  10 create | 11 getById | 12 list | 13 update | 14 delete
                Employee: 20 create | 21 getById | 22 list | 23 leastBuildings | 24 update | 25 delete
                Building: 30 create | 31 getById | 32 list | 33 update | 34 delete
                Allocation: 35 allocateEmployeeToBuilding | 36 reallocateEmployeeBuildings

                Apartment: 40 create | 41 getById | 42 list | 43 update | 44 delete
                Resident:  50 create | 51 getById | 52 list | 53 update | 54 delete
                Person:    60 create | 61 getById | 62 list | 63 update | 64 delete
                Contract:  70 create | 71 getById | 72 list | 73 update | 74 delete

                Pricing:   80 apartmentFee | 81 buildingFee

                Invoice:   90 createForApartment | 91 createForBuilding | 92 getById | 93 byApartment+Month
                          94 listByApartment | 95 listByBuilding | 96 listAll | 97 update | 98 delete

                Payment:   100 create | 101 getById | 102 getByInvoiceId | 103 listByApartment
                          104 listByBuilding | 105 listByBuilding+Month | 106 listAll | 107 update | 108 delete

                FeeCollection: 110 payInvoice (creates Payment + serializes FileDto) | 111 collectFeesForBuilding
                File:          120 readFile (invoiceId + billingMonth + buildingId)

                Filter:    130 companiesByMinCollectedFees | 131 employeesByCompanyName | 132 employeesByCompanyMinBuildings
                          133 residentsByBuildingName | 134 residentsByBuildingAge
                Sort:      140 companiesByCollectedFeesDesc | 141 employeesByCompanyName | 142 employeesByCompanyBuildingsCountDesc
                          143 residentsByBuildingName | 144 residentsByBuildingAge
                Reports:   150 buildingsCountByEmployeesForCompany | 151 countBuildingsByEmployee | 152 buildingsByEmployee
                          153 countApartmentsByBuilding | 154 apartmentsByBuilding | 155 countResidentsByBuilding | 156 residentsByBuilding
                          157 toPayByCompany | 158 toPayByBuilding | 159 toPayByEmployee
                          160 paidByCompany | 161 paidByBuilding | 162 paidByEmployee
                ==========================================
                """);
    }


    private String read(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private Long readLong(String prompt) {
        while (true) {
            try { return Long.parseLong(read(prompt)); }
            catch (Exception e) { System.out.println("Invalid number."); }
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(read(prompt)); }
            catch (Exception e) { System.out.println("Invalid integer."); }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try { return new BigDecimal(read(prompt)); }
            catch (Exception e) { System.out.println("Invalid decimal."); }
        }
    }

    private LocalDate readLocalDate(String prompt) {
        while (true) {
            try { return LocalDate.parse(read(prompt)); }
            catch (Exception e) { System.out.println("Invalid date. Use YYYY-MM-DD."); }
        }
    }

    private YearMonth readYearMonth(String prompt) {
        while (true) {
            try { return YearMonth.parse(read(prompt)); }
            catch (Exception e) { System.out.println("Invalid month. Use YYYY-MM."); }
        }
    }

    private boolean readBoolean(String prompt) {
        while (true) {
            String s = read(prompt).toLowerCase();
            if (s.equals("true") || s.equals("t") || s.equals("yes") || s.equals("y") || s.equals("1")) return true;
            if (s.equals("false") || s.equals("f") || s.equals("no") || s.equals("n") || s.equals("0")) return false;
            System.out.println("Invalid boolean. Use true/false.");
        }
    }

    private PaymentStatus readPaymentStatus(String prompt) {
        while (true) {
            String s = read(prompt).toUpperCase();
            if ("PAID".equals(s)) return PaymentStatus.PAID;
            if ("NOT_PAID".equals(s)) return PaymentStatus.NOT_PAID;
            System.out.println("Invalid status. Use PAID or NOT_PAID.");
        }
    }

    private void printList(List<?> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        for (Object o : list) System.out.println(o);
    }

    private void printError(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) msg = e.toString();
        System.out.println("Error: " + msg);
    }

    private void createCompany() throws Exception {
        String name = read("Company name: ");
        Company c = new Company();
        c.setName(name);
        companyCrudService.createCompany(c);
        System.out.println("OK");
    }

    private void getCompanyById() throws Exception {
        Long id = readLong("Company id: ");
        CompanyWithDetailsDto dto = companyCrudService.getCompanyById(id);
        System.out.println(dto);
    }

    private void listCompanies() {
        List<CompanyListDto> list = companyCrudService.getAllCompanies();
        printList(list);
    }

    private void updateCompany() throws Exception {
        Long id = readLong("Company id: ");
        String name = read("New company name: ");
        Company c = new Company();
        c.setId(id);
        c.setName(name);
        companyCrudService.updateCompany(c);
        System.out.println("OK");
    }

    private void deleteCompany() throws Exception {
        Long id = readLong("Company id: ");
        companyCrudService.deleteCompany(id);
        System.out.println("OK");
    }


    private void createEmployee() throws Exception {
        Long companyId = readLong("Company id: ");
        String firstName = read("First name: ");
        String lastName = read("Last name: ");
        LocalDate collectingDate = readLocalDate("Fee collecting date (YYYY-MM-DD): ");
        int age = readInt("Age: ");


        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setFeeCollectingDate(collectingDate);
        e.setAge(age);

        Company c = new Company();
        c.setId(companyId);
        e.setCompany(c);

        employeeCrudService.createEmployee(e);
        System.out.println("OK");
    }

    private void getEmployeeById() throws Exception {
        Long id = readLong("Employee id: ");
        EmployeeWithDetailsDto dto = employeeCrudService.getEmployeeById(id);
        System.out.println(dto);
    }

    private void listEmployees() {
        List<EmployeeListDto> list = employeeCrudService.getAllEmployees();
        printList(list);
    }

    private void getEmployeeWithLeastBuildings() {
        EmployeeWithDetailsDto dto = employeeCrudService.getEmployeeWithLeastBuildings();
        System.out.println(dto);
    }

    private void updateEmployee() throws Exception {
        Long id = readLong("Employee id: ");
        Long companyId = readLong("Company id: ");
        String firstName = read("First name: ");
        String lastName = read("Last name: ");
        LocalDate collectingDate = readLocalDate("Fee collecting date (YYYY-MM-DD): ");

        Employee e = new Employee();
        e.setId(id);
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setFeeCollectingDate(collectingDate);

        Company c = new Company();
        c.setId(companyId);
        e.setCompany(c);

        employeeCrudService.updateEmployee(e);
        System.out.println("OK");
    }

    private void deleteEmployee() throws Exception {
        Long id = readLong("Employee id: ");
        employeeCrudService.deleteEmployee(id);
        System.out.println("OK");
    }

    private void createBuilding() throws Exception {
        String name = read("Building name: ");
        String address = read("Building address: ");
        int apPerFloor = readInt("Apartments per floor: ");
        int floors = readInt("Count of floors: ");
        LocalDate builtDate = readLocalDate("Built date (YYYY-MM-DD): ");
        BigDecimal builtUpArea = readBigDecimal("Built up area: ");

        Building b = new Building();
        b.setName(name);
        b.setAddress(address);
        b.setApartmentsPerFloor(apPerFloor);
        b.setCountOfFloors(floors);
        b.setBuiltDate(builtDate);
        b.setBuiltUpArea(builtUpArea);
        b.setCommonAreasPercentageOfBuiltUpArea(new BigDecimal("0.2"));

        buildingCrudService.createBuilding(b);
        System.out.println("OK");
    }

    private void getBuildingById() throws Exception {
        Long id = readLong("Building id: ");
        BuildingWithDetailsDto dto = buildingCrudService.getBuildingById(id);
        System.out.println(dto);
    }

    private void listBuildings() {
        List<BuildingListDto> list = buildingCrudService.getAllBuildings();
        printList(list);
    }

    private void updateBuilding() throws Exception {
        Long id = readLong("Building id: ");
        String name = read("Building name: ");
        String address = read("Building address: ");
        int apPerFloor = readInt("Apartments per floor: ");
        int floors = readInt("Count of floors: ");
        LocalDate builtDate = readLocalDate("Built date (YYYY-MM-DD): ");

        Building b = new Building();
        b.setId(id);
        b.setName(name);
        b.setAddress(address);
        b.setApartmentsPerFloor(apPerFloor);
        b.setCountOfFloors(floors);
        b.setBuiltDate(builtDate);

        buildingCrudService.updateBuilding(b);
        System.out.println("OK");
    }

    private void deleteBuilding() throws Exception {
        Long id = readLong("Building id: ");
        buildingCrudService.deleteBuilding(id);
        System.out.println("OK");
    }

    private void allocateEmployeeToBuilding() {
        Long buildingId = readLong("Building id: ");
        EmployeeBuildingsManagementDto dto = employeeAllocationService.allocateEmployeeToBuilding(buildingId);
        System.out.println(dto);
    }

    private void reallocateEmployeeBuildings() {
        Long employeeId = readLong("Employee id (to leave): ");
        Long companyId = readLong("Company id: ");
        employeeAllocationService.reallocateEmployeeBuildings(employeeId, companyId);
        System.out.println("OK");
    }

    private void createApartment() {
        Long buildingId = readLong("Building id: ");
        BigDecimal area = readBigDecimal("Area: ");
        boolean hasPet = readBoolean("Has pet? (true/false): ");

        Apartment a = new Apartment();
        a.setArea(area);
        a.setHasPet(hasPet);

        Building b = new Building();
        b.setId(buildingId);
        a.setBuilding(b);

        apartmentCrudService.createApartment(a);
        System.out.println("OK");
    }

    private void getApartmentById() {
        Long id = readLong("Apartment id: ");
        ApartmentWithDetailsDto dto = apartmentCrudService.getApartmentById(id);
        System.out.println(dto);
    }

    private void listApartments() {
        List<ApartmentListDto> list = apartmentCrudService.getAllApartments();
        printList(list);
    }

    private void updateApartment() {
        Long id = readLong("Apartment id: ");
        Long buildingId = readLong("Building id: ");
        BigDecimal area = readBigDecimal("Area: ");
        boolean hasPet = readBoolean("Has pet? (true/false): ");

        Apartment a = new Apartment();
        a.setId(id);
        a.setArea(area);
        a.setHasPet(hasPet);

        Building b = new Building();
        b.setId(buildingId);
        a.setBuilding(b);

        apartmentCrudService.updateApartment(a);
        System.out.println("OK");
    }

    private void deleteApartment() {
        Long id = readLong("Apartment id: ");
        apartmentCrudService.deleteApartment(id);
        System.out.println("OK");
    }

    private void createResident() throws Exception {
        Long apartmentId = readLong("Apartment id: ");
        String firstName = read("First name: ");
        String lastName = read("Last name: ");
        int age = readInt("Age: ");
        boolean usesElevator = readBoolean("Uses elevator? (true/false): ");
        String role = read("Role: (OWNER, TENANT, FAMILY_MEMBER)").toUpperCase();

        Resident r = new Resident();
        r.setFirstName(firstName);
        r.setLastName(lastName);
        r.setAge(age);
        r.setUsesElevator(usesElevator);
        r.setRole(role.equals("OWNER") ? ResidentRole.OWNER : role.equals("TENANT") ? ResidentRole.TENANT : ResidentRole.FAMILY_MEMBER);

        Apartment a = new Apartment();
        a.setId(apartmentId);
        r.setApartment(a);

        residentCrudService.createResident(r);
        System.out.println("OK");
    }

    private void getResidentById() throws Exception {
        Long id = readLong("Resident id: ");
        ResidentWithDetailsDto dto = residentCrudService.getResidentById(id);
        System.out.println(dto);
    }

    private void listResidents() {
        List<ResidentListDto> list = residentCrudService.getAllResidents();
        printList(list);
    }

    private void updateResident() throws Exception {
        Long id = readLong("Resident id: ");
        Long apartmentId = readLong("Apartment id: ");
        String firstName = read("First name: ");
        String lastName = read("Last name: ");
        int age = readInt("Age: ");
        boolean usesElevator = readBoolean("Uses elevator? (true/false): ");

        Resident r = new Resident();
        r.setId(id);
        r.setFirstName(firstName);
        r.setLastName(lastName);
        r.setAge(age);
        r.setUsesElevator(usesElevator);

        Apartment a = new Apartment();
        a.setId(apartmentId);
        r.setApartment(a);

        residentCrudService.updateResident(r);
        System.out.println("OK");
    }

    private void deleteResident() throws Exception {
        Long id = readLong("Resident id: ");
        residentCrudService.deleteResident(id);
        System.out.println("OK");
    }

    private void createPerson() throws Exception {
        String firstName = read("First name: ");
        String lastName = read("Last name: ");
        int age = readInt("Age: ");

        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setAge(age);

        personCrudService.createPerson(p);
        System.out.println("OK");
    }

    private void getPersonById() throws Exception {
        Long id = readLong("Person id: ");
        PersonDto dto = personCrudService.getPersonById(id);
        System.out.println(dto);
    }

    private void listPeople() {
        List<PersonDto> list = personCrudService.getAllPeople();
        printList(list);
    }

    private void updatePerson() throws Exception {
        Long id = readLong("Person id: ");
        String firstName = read("First name: ");
        String lastName = read("Last name: ");
        int age = readInt("Age: ");

        Person p = new Person();
        p.setId(id);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setAge(age);

        personCrudService.updatePerson(p);
        System.out.println("OK");
    }

    private void deletePerson() throws Exception {
        Long id = readLong("Person id: ");
        personCrudService.deletePerson(id);
        System.out.println("OK");
    }


    private void createContract() throws Exception {
        Long employeeId = readLong("Employee id: ");
        Long buildingId = readLong("Building id: ");

        Contract c = new Contract();
        Employee e = new Employee();
        e.setId(employeeId);
        Building b = new Building();
        b.setId(buildingId);
        c.setEmployee(e);
        c.setBuilding(b);

        contractCrudService.createContract(c);
        System.out.println("OK");
    }

    private void getContractById() throws Exception {
        Long id = readLong("Contract id: ");
        ContractWithDetailsDto dto = contractCrudService.getContractById(id);
        System.out.println(dto);
    }

    private void listContracts() {
        List<ContractListDto> list = contractCrudService.getAllContracts();
        printList(list);
    }

    private void updateContract() throws Exception {
        Long id = readLong("Contract id: ");
        Long employeeId = readLong("Employee id: ");
        Long buildingId = readLong("Building id: ");

        Contract c = new Contract();
        c.setId(id);

        Employee e = new Employee();
        e.setId(employeeId);
        Building b = new Building();
        b.setId(buildingId);

        c.setEmployee(e);
        c.setBuilding(b);

        contractCrudService.updateContract(c);
        System.out.println("OK");
    }

    private void deleteContract() throws Exception {
        Long id = readLong("Contract id: ");
        contractCrudService.deleteContract(id);
        System.out.println("OK");
    }

    private void calcApartmentFee() throws Exception {
        Long apartmentId = readLong("Apartment id: ");
        Apartment a = new Apartment();
        a.setId(apartmentId);
        BigDecimal fee = apartmentPricingSystemService.calculateFee(a);
        System.out.println("Apartment fee: " + fee);
    }

    private void calcBuildingFee() {
        Long buildingId = readLong("Building id: ");
        Building b = new Building();
        b.setId(buildingId);
        BigDecimal fee = buildingPricingSystemService.calculateFeeForBuilding(b);
        System.out.println("Building total fee: " + fee);
    }

    private void createInvoiceForApartment() throws Exception {
        Long apartmentId = readLong("Apartment id: ");
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        invoiceCrudService.createInvoiceForApartment(apartmentId, ym);
        System.out.println("OK");
    }

    private void createInvoicesForBuilding() throws Exception {
        Long buildingId = readLong("Building id: ");
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        invoiceCrudService.createInvoicesForBuilding(buildingId, ym);
        System.out.println("OK");
    }

    private void getInvoiceById() {
        Long invoiceId = readLong("Invoice id: ");
        InvoiceWithDetailsDto dto = invoiceCrudService.getInvoiceById(invoiceId);
        System.out.println(dto);
    }

    private void getInvoiceByApartmentAndMonth() {
        Long apartmentId = readLong("Apartment id: ");
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        InvoiceWithDetailsDto dto = invoiceCrudService.getInvoiceByApartmentAndMonth(apartmentId, ym);
        System.out.println(dto);
    }

    private void listInvoicesByApartment() {
        Long apartmentId = readLong("Apartment id: ");
        List<InvoiceListDto> list = invoiceCrudService.getInvoicesByApartment(apartmentId);
        printList(list);
    }

    private void listInvoicesByBuilding() {
        Long buildingId = readLong("Building id: ");
        List<InvoiceListDto> list = invoiceCrudService.getInvoicesByBuilding(buildingId);
        printList(list);
    }

    private void listAllInvoices() {
        List<InvoiceListDto> list = invoiceCrudService.getAllInvoices();
        printList(list);
    }

    private void updateInvoice() {
        Long invoiceId = readLong("Invoice id: ");
        Long apartmentId = readLong("Apartment id: ");
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        BigDecimal total = readBigDecimal("Total amount: ");
        LocalDate dueDate = readLocalDate("Due date (YYYY-MM-DD): ");
        PaymentStatus status = readPaymentStatus("Payment status (PAID/NOT_PAID): ");

        Invoice inv = new Invoice();
        inv.setId(invoiceId);
        inv.setBillingMonth(ym);
        inv.setTotalAmount(total);
        inv.setDueDate(dueDate);
        inv.setPaymentStatus(status);

        Apartment a = new Apartment();
        a.setId(apartmentId);
        inv.setApartment(a);

        invoiceCrudService.updateInvoice(invoiceId, inv);
        System.out.println("OK");
    }

    private void deleteInvoice() {
        Long invoiceId = readLong("Invoice id: ");
        invoiceCrudService.deleteInvoice(invoiceId);
        System.out.println("OK");
    }

    private void createPayment() {
        Long invoiceId = readLong("Invoice id: ");

        Payment p = new Payment();
        Invoice inv = new Invoice();
        inv.setId(invoiceId);
        p.setInvoice(inv);

        paymentCrudService.createPayment(p);
        System.out.println("OK");
    }

    private void getPaymentById() {
        Long paymentId = readLong("Payment id: ");
        PaymentWithDetailsDto dto = paymentCrudService.getPaymentWithDetailsById(paymentId);
        System.out.println(dto);
    }

    private void getPaymentByInvoiceId() {
        Long invoiceId = readLong("Invoice id: ");
        PaymentWithDetailsDto dto = paymentCrudService.getPaymentWithDetailsByInvoiceId(invoiceId);
        System.out.println(dto);
    }

    private void listPaymentsByApartment() {
        Long apartmentId = readLong("Apartment id: ");
        List<PaymentListDto> list = paymentCrudService.getPaymentsByApartmentId(apartmentId);
        printList(list);
    }

    private void listPaymentsByBuilding() {
        Long buildingId = readLong("Building id: ");
        List<PaymentListDto> list = paymentCrudService.getPaymentsByBuildingId(buildingId);
        printList(list);
    }

    private void listPaymentsByBuildingAndMonth() {
        Long buildingId = readLong("Building id: ");
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        List<PaymentListDto> list = paymentCrudService.getPaymentsByBuildingAndMonth(buildingId, ym);
        printList(list);
    }

    private void listAllPayments() {
        List<PaymentListDto> list = paymentCrudService.getAllPayments();
        printList(list);
    }

    private void updatePayment() {
        Long paymentId = readLong("Payment id: ");
        Long invoiceId = readLong("Invoice id: ");

        Payment p = new Payment();
        p.setId(paymentId);

        Invoice inv = new Invoice();
        inv.setId(invoiceId);
        p.setInvoice(inv);

        paymentCrudService.updatePayment(paymentId, p);
        System.out.println("OK");
    }

    private void deletePayment() {
        Long paymentId = readLong("Payment id: ");
        paymentCrudService.deletePayment(paymentId);
        System.out.println("OK");
    }

    private void payInvoiceAndSerialize() {
        Long invoiceId = readLong("Invoice id: ");
        PaymentWithDetailsDto dto = feeCollectionService.payInvoice(invoiceId);
        System.out.println(dto);
        System.out.println("OK (Payment created + FileDto serialized)");
    }

    private void collectFeesForBuilding() {
        Long buildingId = readLong("Building id: ");
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        feeCollectionService.collectFeesForBuilding(buildingId, ym);
        System.out.println("OK");
    }

    private void readPaidInvoiceFile() {
        Long invoiceId = readLong("Invoice id: ");
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        Long buildingId = readLong("Building id: ");
        FileDto dto = fileService.readFile(invoiceId, ym, buildingId);
        System.out.println(dto);
    }

    private void filterCompaniesByMinCollectedFees() {
        BigDecimal min = readBigDecimal("Min collected fees: ");
        List<CompanyRevenueDto> list = filteringService.filterCompaniesByMinCollectedFees(min);
        printList(list);
    }

    private void filterEmployeesByCompanyName() {
        String name = read("Company name: ");
        List<Employee> list = filteringService.filterEmployeesByCompanyName(name);
        printList(list);
    }

    private void filterEmployeesByCompanyWithMinBuildings() {
        Long companyId = readLong("Company id: ");
        int minBuildings = readInt("Min buildings: ");
        List<EmployeeBuildingsCountDto> list =
                filteringService.filterEmployeesByCompanyWithMinBuildings(companyId, minBuildings);
        printList(list);
    }

    private void filterResidentsByBuildingByName() {
        Long buildingId = readLong("Building id: ");
        String firstName = read("First name contains: ");
        List<Resident> list = filteringService.filterResidentsByBuildingByName(buildingId, firstName);
        printList(list);
    }

    private void filterResidentsByBuildingByAge() {
        Long buildingId = readLong("Building id: ");
        int minAge = readInt("Min age: ");
        int maxAge = readInt("Max age: ");
        List<Resident> list = filteringService.filterResidentsByBuildingByAge(buildingId, minAge, maxAge);
        printList(list);
    }

    private void sortCompaniesByCollectedFeesDesc() {
        List<CompanyRevenueDto> list = sortingService.sortCompaniesByCollectedFeesDesc();
        printList(list);
    }

    private void sortEmployeesByCompanyByName() {
        Long companyId = readLong("Company id: ");
        List<Employee> list = sortingService.sortEmployeesByCompanyByName(companyId);
        printList(list);
    }

    private void sortEmployeesByCompanyByBuildingsCountDesc() {
        Long companyId = readLong("Company id: ");
        List<EmployeeBuildingsCountDto> list = sortingService.sortEmployeesByCompanyByBuildingsCountDesc(companyId);
        printList(list);
    }

    private void sortResidentsByBuildingByName() {
        Long buildingId = readLong("Building id: ");
        List<Resident> list = sortingService.sortResidentsByBuildingByName(buildingId);
        printList(list);
    }

    private void sortResidentsByBuildingByAge() {
        Long buildingId = readLong("Building id: ");
        List<Resident> list = sortingService.sortResidentsByBuildingByAge(buildingId);
        printList(list);
    }

    private void reportBuildingsCountByEmployeesForCompany() {
        Long companyId = readLong("Company id: ");
        List<EmployeeBuildingsCountDto> list = reportService.getBuildingsCountByEmployeesForCompany(companyId);
        printList(list);
    }

    private void reportCountBuildingsByEmployee() {
        Long employeeId = readLong("Employee id: ");
        long cnt = reportService.getCountBuildingsByEmployee(employeeId);
        System.out.println("Count: " + cnt);
    }

    private void reportBuildingsByEmployee() {
        Long employeeId = readLong("Employee id: ");
        List<Building> list = reportService.getBuildingsByEmployee(employeeId);
        printList(list);
    }

    private void reportCountApartmentsByBuilding() {
        Long buildingId = readLong("Building id: ");
        long cnt = reportService.countApartmentsByBuilding(buildingId);
        System.out.println("Count: " + cnt);
    }

    private void reportApartmentsByBuilding() {
        Long buildingId = readLong("Building id: ");
        List<Apartment> list = reportService.getApartmentsByBuilding(buildingId);
        printList(list);
    }

    private void reportCountResidentsByBuilding() {
        Long buildingId = readLong("Building id: ");
        long cnt = reportService.countResidentsByBuilding(buildingId);
        System.out.println("Count: " + cnt);
    }

    private void reportResidentsByBuilding() {
        Long buildingId = readLong("Building id: ");
        List<Resident> list = reportService.getResidentsByBuilding(buildingId);
        printList(list);
    }

    private void reportAmountsToPayByCompany() {
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        List<CompanyAmountDto> list = reportService.getAmountsToPayByCompany(ym);
        printList(list);
        System.out.println("Companies count: " + reportService.countCompaniesForAmountsToPay(ym));
    }

    private void reportAmountsToPayByBuilding() {
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        List<BuildingAmountDto> list = reportService.getAmountsToPayByBuilding(ym);
        printList(list);
        System.out.println("Buildings count: " + reportService.countBuildingsForAmountsToPay(ym));
    }

    private void reportAmountsToPayByEmployee() {
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        List<EmployeeAmountDto> list = reportService.getAmountsToPayByEmployee(ym);
        printList(list);
        System.out.println("Employees count: " + reportService.countEmployeesForAmountsToPay(ym));
    }

    private void reportPaidAmountsByCompany() {
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        List<CompanyAmountDto> list = reportService.getPaidAmountsByCompany(ym);
        printList(list);
        System.out.println("Companies count: " + reportService.countCompaniesForPaidAmounts(ym));
    }

    private void reportPaidAmountsByBuilding() {
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        List<BuildingAmountDto> list = reportService.getPaidAmountsByBuilding(ym);
        printList(list);
        System.out.println("Buildings count: " + reportService.countBuildingsForPaidAmounts(ym));
    }

    private void reportPaidAmountsByEmployee() {
        YearMonth ym = readYearMonth("Billing month (YYYY-MM): ");
        List<EmployeeAmountDto> list = reportService.getPaidAmountsByEmployee(ym);
        printList(list);
        System.out.println("Employees count: " + reportService.countEmployeesForPaidAmounts(ym));
    }
}
