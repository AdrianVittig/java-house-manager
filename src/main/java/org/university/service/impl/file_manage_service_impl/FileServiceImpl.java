package org.university.service.impl.file_manage_service_impl;

import org.university.dto.FileDto;
import org.university.service.contract.file_manage_service.FileService;

import java.io.*;
import java.time.YearMonth;

public class FileServiceImpl implements FileService {
    private static final String DIR_PATH
                = "C:\\Users\\adria\\Desktop\\java-course-work\\java-house-manager\\src\\main\\java\\org\\university\\files";

    @Override
    public void saveToFile(FileDto dto) {
        if(dto == null){
            throw new IllegalArgumentException("FileDto cannot be null");
        }
        if(dto.getInvoiceId() == null){
            throw new IllegalArgumentException("Invoice id cannot be null");
        }

        if(dto.getBillingMonth() == null){
            throw new IllegalArgumentException("Billing month cannot be null");
        }

        if(dto.getBuildingId() == null){
            throw new IllegalArgumentException("Building id cannot be null");
        }

        String fullPath = getFullPath(dto.getInvoiceId(), dto.getBillingMonth(), dto.getBuildingId());

        File f = new File(fullPath);
        if(f.exists()){
            throw new IllegalArgumentException("File already exists: " + fullPath);
        }

        try(FileOutputStream fos = new FileOutputStream(fullPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(dto);
        }catch(IOException e){
            throw new RuntimeException("Error while saving file: ", e);
        }
    }

    @Override
    public FileDto readFile(Long invoiceId, YearMonth billingMonth, Long buildingId) {
        if(invoiceId == null || billingMonth == null || buildingId == null){
            throw new IllegalArgumentException("Invoice id, billing month or building id cannot be null");
        }

        String fullPath = getFullPath(invoiceId, billingMonth, buildingId);

        try(FileInputStream fis = new FileInputStream(fullPath);
            ObjectInputStream ois = new ObjectInputStream(fis)){

            Object obj = ois.readObject();
            if(!(obj instanceof FileDto fDto)){
                throw new RuntimeException("File is corrupted: " + fullPath);
            }

            return fDto;

        }catch(IOException | ClassNotFoundException e){
            throw new RuntimeException("Error while reading file: " + fullPath, e);
        }
    }

    private String getFullPath(Long invoiceId, YearMonth billingMonth, Long buildingId){
        return DIR_PATH + "\\" + "Paid Invoice - "
                +" Invoice#" + invoiceId
                + " - Building#" + buildingId +
                " - (" + billingMonth + ")" +
                " - " + ".ser";
    }
}
