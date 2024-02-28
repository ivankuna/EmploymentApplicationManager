package hr.betaSoft.tools;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlToPdfConverter {

    public static void convertHtmlToPdf(String htmlFilePath, String pdfFilePath) throws IOException, DocumentException {
        // Reading HTML file
        String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)), StandardCharsets.UTF_8);

        // Creating PDF document
        Document pdfDocument = new Document(PageSize.A4);


        // Initializing PdfWriter for writing PDF
        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));

        // Opening the document for writing
        String customFontPath = "font/Roboto-Medium.ttf";
        pdfDocument.open();
        BaseFont customBaseFont = BaseFont.createFont(customFontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font customFont = new Font(customBaseFont, 12);

        // Creating a new XMLWorkerHelper
        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

        // Parsing and adding HTML content to the document
        worker.parseXHtml(pdfWriter, pdfDocument, Files.newInputStream(Paths.get(htmlFilePath)), StandardCharsets.UTF_8);

        // Closing the document
        pdfDocument.close();
    }

    public static void main(String[] args) {
        try {
            String htmlFilePath = "html/input.html";
            String pdfFilePath = "pdf/output.pdf";
            convertHtmlToPdf(htmlFilePath, pdfFilePath);
            System.out.println("Conversion completed. PDF file is created at: " + pdfFilePath);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }
}
