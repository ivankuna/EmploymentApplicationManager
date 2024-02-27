package hr.betaSoft.pdf;

import hr.betaSoft.model.Employee;
import hr.betaSoft.service.EmployeeService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import java.io.File;
import java.io.IOException;

public class PdfAppGenerator {
    private final EmployeeService employeeService;

    public PdfAppGenerator(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void generateApplication(long employeeId) {

        // Dohvatite podatke o radniku na temelju employeeId
        Employee employee = employeeService.findById(employeeId);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDType0Font font = PDType0Font.load(document, new File("font/Roboto-Medium.ttf"));

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(font, 12);

            // Postavite položaj za ispis teksta
            float y = page.getMediaBox().getHeight() - 50;

            // Ispišite naslov računa
            contentStream.beginText();
            contentStream.newLineAtOffset(50, y);
//            contentStream.showText(employee.getFirstName());
            contentStream.showText(employee.toString());
            contentStream.endText();
            y -= 20; // manji razmak do linije

            // Zatvorite PDPageContentStream
            contentStream.close();

            // Spremite PDF dokument na odredište
            document.save("pdf/employee.pdf");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

