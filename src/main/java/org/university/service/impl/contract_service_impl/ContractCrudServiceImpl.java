package org.university.service.impl.contract_service_impl;

import org.university.dao.contract_dao.ContractCrudDao;
import org.university.dao.contract_dao.ContractMapper;
import org.university.dto.ContractListDto;
import org.university.dto.ContractWithDetailsDto;
import org.university.entity.Contract;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;
import org.university.service.contract.contract_service.ContractCrudService;

import java.time.LocalDate;
import java.util.List;

public class ContractCrudServiceImpl implements ContractCrudService {
    private final ContractCrudDao contractDao = new ContractCrudDao();
    private final ContractMapper contractMapper = new ContractMapper();
    @Override
    public void createContract(Contract contract) throws DAOException, NotFoundException {
        if(contract == null){
            throw new IllegalArgumentException("Contract cannot be null");
        }

        if(contract.getEmployee() == null || contract.getEmployee().getId() == null){
            throw new IllegalArgumentException("Employee cannot be null");
        }

        if(contract.getBuilding() == null || contract.getBuilding().getId() == null){
            throw new IllegalArgumentException("Building cannot be null");
        }

        if(contractDao.existsByBuildingId(contract.getBuilding().getId())){
            throw new IllegalArgumentException("Building with id " + contract.getBuilding().getId() + " already has a contract");
        }

        long totalCountOfContracts = contractDao.getCountOfContracts();

        int contractNumber = 1000 + (int) totalCountOfContracts + 1;
        String apNumberToString = String.valueOf("Contract: " + contractNumber);

        contract.setIssueDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setNumber(apNumberToString);

        contractDao.createContract(contract);
    }

    @Override
    public ContractWithDetailsDto getContractById(Long id) throws DAOException, NotFoundException {
        Contract contract = contractDao.getContractById(id);
        if(contract == null){
            throw new NotFoundException("Contract with id " + id + " does not exist");
        }
        return contractMapper.toDetailsDto(contract);
    }

    @Override
    public List<ContractListDto> getAllContracts() {
        return contractDao.getAllContracts()
                .stream()
                .map(contractMapper::toListDto)
                .toList();
    }

    @Override
    public void updateContract(Contract contract) throws DAOException, NotFoundException {
        if(contract == null || contract.getId() == null){
            throw new IllegalArgumentException("Contract cannot be null");
        }
        contractDao.updateContract(contract.getId(), contract);
    }

    @Override
    public void deleteContract(Long id) throws DAOException, NotFoundException {
        ContractWithDetailsDto contract = getContractById(id);
        if(contract == null){
            throw new NotFoundException("Contract with id " + id + " does not exist");
        }
        contractDao.deleteContract(id);
    }
}
