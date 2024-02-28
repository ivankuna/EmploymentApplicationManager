package hr.betaSoft.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hr.betaSoft.model.Employee;
import java.io.FileOutputStream;

public class PDFGenerator {

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
    }

    private static void addRows(PdfPTable table, Employee employee) {
        String[] employeeData = new String[9];
        employeeData[0] = employee.getOib();
        employeeData[1] = employee.getFirstName();
        employeeData[2] = employee.getLastName();
        employeeData[3] = employee.getDateOfSignUp().toString();
        employeeData[4] = employee.getDateOfUpdate().toString();
        employeeData[5] = employee.getDateOfSignOut().toString();
        employeeData[6] = employee.getDateOfSignOutReal().toString();
        employeeData[7] = employee.getReasonForSignOut();
        employeeData[8] = employee.getNoteSignOut();

        for (int i = 1; i <= 9; i++) {
            table.addCell(Integer.toString(i)); // Column 1: Numbers 1 to 8

            String[] column2Data = {"OIB", "Ime", "Prezime", "Datum prijave", "Datum zadnje promjene",
                    "Datum odjave", "Datum stvarne odjave", "Razlog odjave", "Napomena"};

            table.addCell(column2Data[i - 1]); // Column 2: Strings in order

            table.addCell(employeeData[i - 1]); // Column 3: Data from Employee object

//            if (i <= employeeList.size()) {
//                Employee employee = employeeList.get(i - 1);
//                table.addCell(employee.toString()); // Column 3: Data from Employee object
//            } else {
//                table.addCell(""); // If no data for the current row
//            }
        }
    }

    public static void main(String[] args) {

        // Provide the file path where you want to save the PDF
        String filePath = "pdf/employee.pdf";

        // Generate the PDF
        generatePDF(new Employee(), filePath);
    }
}

