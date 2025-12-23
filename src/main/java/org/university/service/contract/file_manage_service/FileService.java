package org.university.service.contract.file_manage_service;

import org.university.dto.FileDto;

import java.time.YearMonth;

public interface FileService {
    void saveToFile(FileDto dto);
    FileDto readFile(Long invoiceId, YearMonth billingMonth, Long buildingId);
}
