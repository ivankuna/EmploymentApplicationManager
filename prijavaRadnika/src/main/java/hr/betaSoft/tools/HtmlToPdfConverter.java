package hr.betaSoft.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class HtmlToPdfConverter {

    public static void convertHtmlToPdf(String htmlFilePath, String pdfFilePath) throws IOException, DocumentException {
        // Reading HTML file
//        String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)), StandardCharsets.UTF_8);

        StringBuilder modifiedHtmlContent = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(htmlFilePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Make the necessary replacements
                line = line.replace("UTF-8\">", "UTF-8\"/>")
                        .replace("<br>", "<br/>");
                modifiedHtmlContent.append(line).append(System.lineSeparator());
            }
        }

        // Save the modified HTML content back to its original path
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(htmlFilePath), StandardCharsets.UTF_8)) {
            writer.write(modifiedHtmlContent.toString());
        }

        // Creating PDF document
        Document pdfDocument = new Document(PageSize.A4);

        // Initializing PdfWriter for writing PDF
        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));

        // Opening the document for writing
        pdfDocument.open();

        // Creating a new XMLWorkerHelper
        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
        InputStream inputStream = Files.newInputStream(Paths.get(htmlFilePath));


        // Parsing and adding HTML content to the document
        worker.parseXHtml(pdfWriter, pdfDocument, inputStream, StandardCharsets.UTF_8);

        // Closing the document
        pdfDocument.close();
    }
    public static void main(String[] args) throws DocumentException, IOException {
        String htmlFilePath = "prijavaRadnika/html/test.html";
        String pdfFilePath = "prijavaRadnika/pdf/test.pdf";
        convertHtmlToPdf(htmlFilePath, pdfFilePath);
        System.out.println("Conversion completed. PDF file is created at: " + pdfFilePath);
    }
}


