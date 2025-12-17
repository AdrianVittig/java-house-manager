package org.university.dao.contract_dao;

import org.university.dto.ContractListDto;
import org.university.dto.ContractWithDetailsDto;
import org.university.entity.Contract;

public class ContractMapper {
    public ContractListDto toListDto(Contract contract){
        ContractListDto contractListDto = new ContractListDto();
        contractListDto.setId(contract.getId());
        contractListDto.setNumber(contract.getNumber());
        contractListDto.setIssueDate(contract.getIssueDate());
        contractListDto.setEndDate(contract.getEndDate());
        return contractListDto;
    }

    public ContractWithDetailsDto toDetailsDto(Contract contract){
        ContractWithDetailsDto contractWithDetailsDto = new ContractWithDetailsDto();
        contractWithDetailsDto.setId(contract.getId());
        contractWithDetailsDto.setNumber(contract.getNumber());
        contractWithDetailsDto.setIssueDate(contract.getIssueDate());
        contractWithDetailsDto.setEndDate(contract.getEndDate());

        if(contract.getBuilding() != null){
            contractWithDetailsDto.setBuildingId(contract.getBuilding().getId());
        }

        if(contract.getEmployee() != null){
            contractWithDetailsDto.setEmployeeId(contract.getEmployee().getId());
        }

        return contractWithDetailsDto;
    }
}
