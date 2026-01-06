package org.university.service.impl.file_manage_service_impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.university.dto.FileDto;
import org.university.util.PaymentStatus;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceImplTest {

    private final FileServiceImpl service = new FileServiceImpl();
    private final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    private final java.util.List<File> createdFiles = new java.util.ArrayList<>();

    @AfterEach
    void cleanupFiles() {
        for (File f : createdFiles) {
            try {
                if (f != null && f.exists()) Files.deleteIfExists(f.toPath());
            } catch (Exception ignored) {
            }
        }
        createdFiles.clear();
    }

    private String dirPath() {
        try {
            Field f = FileServiceImpl.class.getDeclaredField("DIR_PATH");
            f.setAccessible(true);
            return (String) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String fullPath(Long invoiceId, YearMonth billingMonth, Long buildingId) {
        return dirPath() + "\\" + "Paid Invoice - "
                + " Invoice#" + invoiceId
                + " - Building#" + buildingId
                + " - (" + billingMonth + ")"
                + " - " + ".ser";
    }

    @Test
    void saveToFile_and_readFile_roundTrip() throws Exception {
        Assumptions.assumeTrue(isWindows);

        Files.createDirectories(Path.of(dirPath()));

        FileDto dto = new FileDto(
                1L,
                YearMonth.of(2025, 1),
                10L,
                "Company",
                "Ivan",
                "Ivanov",
                "Building",
                "Address",
                "Room: 1001",
                new BigDecimal("99.99"),
                LocalDateTime.now(),
                PaymentStatus.PAID
        );

        service.saveToFile(dto);

        File f = new File(fullPath(dto.getInvoiceId(), dto.getBillingMonth(), dto.getBuildingId()));
        createdFiles.add(f);
        assertTrue(f.exists());

        FileDto read = service.readFile(dto.getInvoiceId(), dto.getBillingMonth(), dto.getBuildingId());
        assertNotNull(read);
        assertEquals(dto.getInvoiceId(), read.getInvoiceId());
        assertEquals(dto.getBillingMonth(), read.getBillingMonth());
        assertEquals(dto.getBuildingId(), read.getBuildingId());
        assertEquals(dto.getCompanyName(), read.getCompanyName());
        assertEquals(dto.getApartmentNumber(), read.getApartmentNumber());
        assertEquals(0, dto.getAmount().compareTo(read.getAmount()));
    }

    @Test
    void saveToFile_whenFileExists_throws() throws Exception {
        Assumptions.assumeTrue(isWindows);

        Files.createDirectories(Path.of(dirPath()));

        FileDto dto = new FileDto(
                2L,
                YearMonth.of(2025, 2),
                20L,
                "Company",
                "Ivan",
                "Ivanov",
                "Building",
                "Address",
                "Room: 1002",
                new BigDecimal("10.00"),
                LocalDateTime.now(),
                PaymentStatus.PAID
        );

        service.saveToFile(dto);

        File f = new File(fullPath(dto.getInvoiceId(), dto.getBillingMonth(), dto.getBuildingId()));
        createdFiles.add(f);
        assertTrue(f.exists());

        assertThrows(IllegalArgumentException.class, () -> service.saveToFile(dto));
    }

    @Test
    void saveToFile_validation() {
        assertThrows(IllegalArgumentException.class, () -> service.saveToFile(null));

        FileDto dto = new FileDto(null, YearMonth.of(2025, 1), 1L, "c", "f", "l", "b", "a", "n",
                new BigDecimal("1.00"), LocalDateTime.now(), PaymentStatus.PAID);
        assertThrows(IllegalArgumentException.class, () -> service.saveToFile(dto));

        FileDto dto2 = new FileDto(1L, null, 1L, "c", "f", "l", "b", "a", "n",
                new BigDecimal("1.00"), LocalDateTime.now(), PaymentStatus.PAID);
        assertThrows(IllegalArgumentException.class, () -> service.saveToFile(dto2));

        FileDto dto3 = new FileDto(1L, YearMonth.of(2025, 1), null, "c", "f", "l", "b", "a", "n",
                new BigDecimal("1.00"), LocalDateTime.now(), PaymentStatus.PAID);
        assertThrows(IllegalArgumentException.class, () -> service.saveToFile(dto3));
    }

    @Test
    void readFile_validation() {
        assertThrows(IllegalArgumentException.class, () -> service.readFile(null, YearMonth.of(2025, 1), 1L));
        assertThrows(IllegalArgumentException.class, () -> service.readFile(1L, null, 1L));
        assertThrows(IllegalArgumentException.class, () -> service.readFile(1L, YearMonth.of(2025, 1), null));
    }
}
