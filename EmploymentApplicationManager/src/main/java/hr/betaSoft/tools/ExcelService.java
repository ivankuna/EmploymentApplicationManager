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
import java.util.List;

@Service
public class ExcelService {

    public String generateExcel(List<Employee> employeeList, boolean allApps) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        String sheetName = allApps ? "AllAppExport" : "PendingAppExport";
        Sheet sheet = workbook.createSheet(sheetName);

        String[] columns;

        if (allApps) {
            columns = new String[]{"Tvrtka", "Prezime", "Ime", "OIB", "Broj naloga", "Datum slanja", "Vrijeme slanja"};
        } else {
            columns = new String[]{"Tvrtka", "Prezime", "Ime", "OIB", "Vrsta naloga"};
        }

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
            row.createCell(4).setCellValue(employee.getNumApp());
            if (allApps) {
                row.createCell(5).setCellValue(employee.getDateAppReal().toString());
                row.createCell(6).setCellValue(employee.getTimeApp().toString());
            }
        }

        Path path = Paths.get("xlsx");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String pathname = allApps ? "xlsx/allAppExport.xlsx" : "xlsx/pendingAppExport.xlsx";

        File file = new File(pathname);

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }

        workbook.close();

        return pathname;
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