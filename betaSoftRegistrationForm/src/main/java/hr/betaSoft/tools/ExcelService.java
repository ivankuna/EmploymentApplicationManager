package hr.betaSoft.tools;

import hr.betaSoft.model.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExcelService {

    public String generateExcel(List<Employee> employeeList, String appType) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        String sheetName = switch (appType) {
            case "allApps" -> "AllAppExport";
            case "pendingApps" -> "PendingAppExport";
            case "activeApps" -> "ActiveAppExport";
            case "expiryApps" -> "ToExpireAppExprot";
            case "fixedTermApps" -> "FixedTermAppExport";
            default -> throw new IllegalStateException("Unexpected value: " + appType);
        };

        Sheet sheet = workbook.createSheet(sheetName);

        String[] columns = switch (appType) {
            case "allApps" -> new String[]{"Tvrtka", "Prezime", "Ime", "OIB", "Broj naloga", "Datum slanja", "Vrijeme slanja"};
            case "pendingApps" -> new String[]{"Tvrtka", "Prezime", "Ime", "OIB", "Vrsta naloga"};
            case "activeApps" -> new String[]{"Tvrtka", "Prezime", "Ime", "OIB", "Datum prijave"};
            case "expiryApps" -> new String[]{"Tvrtka", "Prezime", "Ime", "OIB", "Datum prijave", "Datum isteka radne dozvole"};
            case "fixedTermApps" -> new String[]{"Tvrtka", "Prezime", "Ime", "OIB", "Datum prijave", "Datum odjave - iz Prijave"};
            default -> throw new IllegalStateException("Unexpected value: " + appType);
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Employee employee : employeeList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(employee.getCompany());
            row.createCell(1).setCellValue(employee.getLastName());
            row.createCell(2).setCellValue(employee.getFirstName());
            row.createCell(3).setCellValue(employee.getOib());

            switch (appType) {
                case "allApps" -> {
                    row.createCell(4).setCellValue(employee.getNumApp());
                    row.createCell(5).setCellValue(checkDate(employee.getDateAppReal()));
                    row.createCell(6).setCellValue(employee.getTimeApp());
                }
                case "pendingApps" -> row.createCell(4).setCellValue(employee.getNumApp());
                case "activeApps" -> row.createCell(4).setCellValue(checkDate(employee.getDateOfSignUp()));
                case "expiryApps" -> {
                    row.createCell(4).setCellValue(checkDate(employee.getDateOfSignUp()));
                    row.createCell(5).setCellValue(checkDate(employee.getExpiryDateOfWorkPermit()));
                }
                case "fixedTermApps" -> {
                    row.createCell(4).setCellValue(checkDate(employee.getDateOfSignUp()));
                    row.createCell(5).setCellValue(checkDate(employee.getDateOfSignOut()));
                }
            }
        }

        Path path = Paths.get("xlsx");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String pathname = switch (appType) {
            case "allApps" -> "xlsx/Pregled_Svih_Naloga_.xlsx";
            case "pendingApps" -> "xlsx/Pregled_Svih_Naloga_U_Pripremi_.xlsx";
            case "activeApps" -> "xlsx/Popis_Radnika_U_Radnom_Odnosu_.xlsx";
            case "expiryApps" -> "xlsx/Izvjestaj_O_Isteku_Radne_Dozvole_.xlsx";
            case "fixedTermApps" -> "xlsx/Izvjestaj_O_Isteku_Rada_Na_Odredjeno_.xlsx";
            default -> throw new IllegalStateException("Unexpected value: " + appType);
        };

        File file = new File(pathname);

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }

        workbook.close();

        return pathname;
    }

    private String checkDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");

        return (date != null) ? sdf.format(date) : "";
    }

    private void openExcelFile(File file) {
        String filePath = file.getAbsolutePath();

        String[] command;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command = new String[]{"cmd", "/c", "start", "\"\"", filePath};
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            command = new String[]{"open", filePath};
        } else if (System.getProperty("os.name").toLowerCase().contains("nux") || System.getProperty("os.name").toLowerCase().contains("nix")) {
            command = new String[]{"xdg-open", filePath};
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + System.getProperty("os.name"));
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.start();
        } catch (IOException e) {
            System.err.println("Error opening file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}