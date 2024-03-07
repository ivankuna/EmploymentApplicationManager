package hr.betaSoft.tools;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.itextpdf.text.*;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;


public class HtmlToPdfConverter {

//    public static void convertHtmlPathToPdf(String htmlFilePath, String pdfFilePath) throws IOException, DocumentException {
//
//
//        StringBuilder modifiedHtmlContent = new StringBuilder();
//        try (BufferedReader reader = Files.newBufferedReader(Paths.get(htmlFilePath), StandardCharsets.UTF_8)) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Make the necessary replacements
//                line = line.replace("UTF-8\">", "UTF-8\"/>")
//                        .replace("<br>", "<br/>");
//                modifiedHtmlContent.append(line).append(System.lineSeparator());
//            }
//        }
//
//        // Save the modified HTML content back to its original path
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(htmlFilePath), StandardCharsets.UTF_8)) {
//            writer.write(modifiedHtmlContent.toString());
//        }
//
//        // Creating PDF document
//        Document pdfDocument = new Document(PageSize.A4);
//
//        // Initializing PdfWriter for writing PDF
//        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
//
//        // Opening the document for writing
//        pdfDocument.open();
//
//        // Creating a new XMLWorkerHelper
//        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//        InputStream inputStream = Files.newInputStream(Paths.get(htmlFilePath));
//
//
//        // Parsing and adding HTML content to the document
//        worker.parseXHtml(pdfWriter, pdfDocument, inputStream, StandardCharsets.UTF_8);
//
//        // Closing the document
//        pdfDocument.close();
//    }

//    public static void convertHtmlUrlToPdf(URL htmlUrl, String pdfFilePath) throws IOException, DocumentException {
//
//
//        StringBuilder modifiedHtmlContent = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(htmlUrl.openStream(), StandardCharsets.UTF_8))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Make the necessary replacements
//                line = line.replace("UTF-8\">", "UTF-8\"/>")
//                        .replace("<br>", "<br/>");
//                modifiedHtmlContent.append(line).append(System.lineSeparator());
//            }
//        }
//
//        // Creating PDF document
//        Document pdfDocument = new Document(PageSize.A4);
//
//        // Initializing PdfWriter for writing PDF
//        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
//
//        // Opening the document for writing
//        pdfDocument.open();
//
//        // Creating a new XMLWorkerHelper
//        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//        InputStream inputStream = new ByteArrayInputStream(modifiedHtmlContent.toString().getBytes(StandardCharsets.UTF_8));
//
//        // Parsing and adding HTML content to the document
//        worker.parseXHtml(pdfWriter, pdfDocument, inputStream, StandardCharsets.UTF_8);
//
//        // Closing the document
//        pdfDocument.close();
//    }


//    public static void convertHtmlToPdf(String htmlContent, String pdfFilePath) throws IOException, DocumentException {
//        // Make the necessary replacements
//        String modifiedHtmlContent = htmlContent.replace("UTF-8\">", "UTF-8\"/>")
//                .replace("<br>", "<br/>");
//
//        // Parsing HTML using Jsoup
//        Document doc = Jsoup.parse(modifiedHtmlContent);
//
//        // Formatting dates in the desired format
//        Elements dateElements = doc.select("td:matches(\\d{4}-\\d{2}-\\d{2})");
//        for (Element element : dateElements) {
//            String originalText = element.text();
//            String formattedDate = formatDate(originalText);
//            element.text(formattedDate);
//        }
//
//        // Creating PDF document
//        Document pdfDocument = new Document(PageSize.A4);
//
//        // Initializing PdfWriter for writing PDF
//        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
//
//        // Opening the document for writing
//        pdfDocument.open();
//
//        // Creating a new XMLWorkerHelper
//        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//
//        // Parsing and adding HTML content to the document
//        try (InputStream inputStream = new ByteArrayInputStream(doc.outerHtml().getBytes())) {
//            worker.parseXHtml(pdfWriter, pdfDocument, inputStream, null);
//        }
//
//        // Closing the document
//        pdfDocument.close();
//    }


//    public static void convertHtmlContentToPdf(String htmlContent, String pdfFilePath) throws IOException, DocumentException {
//
//        // Make the necessary replacements
//        String modifiedHtmlContent = htmlContent.replace("true", "Da")
//                .replace("false", "Ne");
//
//
//        org.jsoup.nodes.Document doc = Jsoup.parse(modifiedHtmlContent);
//
//        // Formatting dates in the desired format
//        org.jsoup.select.Elements dateElements = doc.select("td:matches(\\d{4}-\\d{2}-\\d{2})");
//        for (org.jsoup.nodes.Element element : dateElements) {
//            String originalText = element.text();
//            String formattedDate = formatDate(originalText);
//            element.text(formattedDate);
//        }
//        org.jsoup.select.Elements metaElements = doc.select("meta");
//        for (org.jsoup.nodes.Element element : metaElements) {
//
//            String newMeta = "<meta http-equiv=\"content-type\" content=\"application/xhtml+xml; charset=UTF-8\"/>";
//            element.text(newMeta);
//        }
//
//
//        // Creating PDF document
//        Document pdfDocument = new Document(PageSize.A4);
//
//        // Initializing PdfWriter for writing PDF
//        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
//
//        // Opening the document for writing
//        pdfDocument.open();
//
//        // Creating a new XMLWorkerHelper
//        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//
//        // Parsing and adding HTML content to the document
//        try (InputStream inputStream = new ByteArrayInputStream(doc.outerHtml().getBytes(StandardCharsets.UTF_8))) {
//            worker.parseXHtml(pdfWriter, pdfDocument, inputStream, StandardCharsets.UTF_8);
//        }
//
//        // Closing the document
//        pdfDocument.close();
//    }


//    public static void convertHtmlContentToPdf(String htmlContent, String pdfFilePath) throws IOException, DocumentException {
//        // Make the necessary replacements
//        String modifiedHtmlContent = htmlContent.replace("true", "Da")
//                .replace("false", "Ne");
//
//        org.jsoup.nodes.Document doc = Jsoup.parse(modifiedHtmlContent);
//
//        // Formatting dates in the desired format
//        org.jsoup.select.Elements dateElements = doc.select("td:matches(\\d{4}-\\d{2}-\\d{2})");
//        for (org.jsoup.nodes.Element element : dateElements) {
//            String originalText = element.text();
//            String formattedDate = formatDate(originalText);
//            element.text(formattedDate);
//        }
//        org.jsoup.select.Elements metaElements = doc.select("meta");
//        for (org.jsoup.nodes.Element element : metaElements) {
//            String newMeta = "<meta http-equiv=\"content-type\" content=\"application/xhtml+xml; charset=UTF-8\"/>";
//            element.text(newMeta);
//        }
//
//        // Creating PDF document
//        Document pdfDocument = new Document(PageSize.A4);
//
//        // Initializing PdfWriter for writing PDF
//        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));
//
//        // Opening the document for writing
//        pdfDocument.open();
//
//        // Creating a new XMLWorkerHelper
//        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
//
//        // Defining font
//        BaseFont bf = BaseFont.createFont("prijavaRadnika/font/Roboto-Medium.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        Font font = new Font(bf, 8);
//
//        // Setting default font for HTMLWorker
//        FontFactory.setFontImp(new FontFactoryImp() {
//            @Override
//            public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color) {
//                return font;
//            }
//        });
//
//        // Parsing and adding HTML content to the document
//        try (InputStream inputStream = new ByteArrayInputStream(doc.outerHtml().getBytes(StandardCharsets.UTF_8))) {
//            worker.parseXHtml(pdfWriter, pdfDocument, inputStream, StandardCharsets.UTF_8);
//        }
//
//        // Closing the document
//        pdfDocument.close();
//    }


    public static void convertHtmlContentToPdf(String htmlContent, String pdfFilePath) throws IOException, DocumentException {
        // Make the necessary replacements
        String modifiedHtmlContent = htmlContent.replace("true", "Da")
                .replace("false", "Ne")
                .replace("<br>", "<br/>");

        org.jsoup.nodes.Document doc = Jsoup.parse(modifiedHtmlContent);

        // Formatting dates in the desired format
        org.jsoup.select.Elements dateElements = doc.select("td:matches(\\d{4}-\\d{2}-\\d{2})");
        for (org.jsoup.nodes.Element element : dateElements) {
            String originalText = element.text();
            String formattedDate = formatDate(originalText);
            element.text(formattedDate);
        }
        org.jsoup.select.Elements metaElements = doc.select("meta");
        for (org.jsoup.nodes.Element element : metaElements) {
            String newMeta = "<meta http-equiv=\"content-type\" content=\"application/xhtml+xml; charset=UTF-8\"/>";
            element.text(newMeta);
        }

        // Creating PDF document
        Document pdfDocument = new Document(PageSize.A4);

        // Initializing PdfWriter for writing PDF
        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));

        // Opening the document for writing
        pdfDocument.open();

        // Creating a new XMLWorkerHelper
        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

        // Defining font
        BaseFont bf = BaseFont.createFont("prijavaRadnika/font/Calibri-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(bf, 11);

        // Parsing and adding HTML content to the document
        try (InputStream inputStream = new ByteArrayInputStream(doc.outerHtml().getBytes(StandardCharsets.UTF_8))) {
            worker.parseXHtml(pdfWriter, pdfDocument, inputStream, StandardCharsets.UTF_8, new FontFactoryImp() {
                @Override
                public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color) {
                    return font;
                }
            });
        }

        // Closing the document
        pdfDocument.close();
    }


    private static String formatDate(String originalDate) {
        String[] parts = originalDate.split("-");
        if (parts.length == 3) {
            return parts[2] + "." + parts[1] + "." + parts[0] + ".";
        }
        return originalDate;
    }


    public static void main(String[] args) throws DocumentException, IOException {
//        String htmlFilePath = "prijavaRadnika/html/test.html";
//        String pdfFilePath = "prijavaRadnika/pdf/test.pdf";
//        convertHtmlToPdf(htmlFilePath, pdfFilePath);
//        System.out.println("Conversion completed. PDF file is created at: " + pdfFilePath);
//        String htmlUrlString = "http://example.com/test.html"; // Promijenite URL prema vašem slučaju
//        String htmlUrlString = "http://localhost:8080/employees/pdf/1"; // Promijenite URL prema vašem slučaju
//        URL htmlUrl = new URL(htmlUrlString);
//        String pdfFilePath = "prijavaRadnika/pdf/fromhtmltest.pdf";
//        convertHtmlUrlToPdf(htmlUrl, pdfFilePath);
//        System.out.println("Conversion completed. PDF file is created at: " + pdfFilePath);

    }
}


