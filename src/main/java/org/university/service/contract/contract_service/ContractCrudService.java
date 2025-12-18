package org.university.service.contract.contract_service;

import org.university.dto.ContractListDto;
import org.university.dto.ContractWithDetailsDto;
import org.university.entity.Contract;
import org.university.exception.DAOException;
import org.university.exception.NotFoundException;

import java.util.List;

public interface ContractCrudService {
    void createContract(Contract contract) throws DAOException, NotFoundException;
    ContractWithDetailsDto getContractById(Long id) throws DAOException, NotFoundException;
    List<ContractListDto> getAllContracts();
    void updateContract(Contract contract) throws DAOException, NotFoundException;
    void deleteContract(Long id) throws DAOException, NotFoundException;
}
