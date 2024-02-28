package hr.betaSoft.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hr.betaSoft.model.Employee;
import java.io.FileOutputStream;

public class PdfSignOutLose {

    public static void generatePDF(Employee employee, String filePath) {
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            addTableHeader(table);
            addRows(table, employee);

            document.add(table);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTableHeader(PdfPTable table) {
        String[] header = {"#", "Information", "Employee Data"};

        for (String columnName : header) {
            PdfPCell cell = new PdfPCell(new Paragraph(columnName));
            table.addCell(cell);
        }
//        PdfPCell cell = new PdfPCell(new Paragraph("PODACI O RADNIKU"));
//        table.addCell(cell);
    }

    private static void addRows(PdfPTable table, Employee employee) {
        String[] employeeData = new String[9];
        employeeData[0] = employee.getOib();
        employeeData[1] = employee.getFirstName();
        employeeData[2] = employee.getLastName();
        if (employee.getDateOfSignUp() != null) {
            employeeData[3] = employee.getDateOfSignUp().toString();
        } else {
            employeeData[3] = "";
        }
        if (employee.getDateOfUpdate() != null) {
            employeeData[4] = employee.getDateOfUpdate().toString();
        } else {
            employeeData[4] = "";
        }
        if (employee.getDateOfSignOut() != null) {
            employeeData[5] = employee.getDateOfSignOut().toString();
        } else {
            employeeData[5] = "";
        }
        if (employee.getDateOfSignOutReal() != null) {
            employeeData[6] = employee.getDateOfSignOutReal().toString();
        } else {
            employeeData[6] = "";
        }
        employeeData[7] = employee.getReasonForSignOut();
        employeeData[8] = employee.getNoteSignOut();

        for (int i = 1; i <= 9; i++) {
            table.addCell(i + "."); // Column 1: Numbers 1 to 8

            String[] column2Data = {"OIB", "Ime", "Prezime", "Datum prijave", "Datum zadnje promjene",
                    "Datum odjave", "Datum stvarne odjave", "Razlog odjave", "Napomena"};

            table.addCell(column2Data[i - 1]); // Column 2: Strings in order

            table.addCell(employeeData[i - 1]); // Column 3: Data from Employee object
        }
    }
}

