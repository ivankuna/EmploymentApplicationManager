package hr.betaSoft.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.itextpdf.text.*;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.jsoup.Jsoup;



public class HtmlToPdfConverter {

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

        // Applying dotted style to table cells
//        org.jsoup.select.Elements tableCells = doc.select("th, td");
//        for (org.jsoup.nodes.Element element : tableCells) {
//            element.attr("style", "border-style: dotted;");
//            element.attr("style", "border: 0.1px dotted black;");
//        }

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
}


