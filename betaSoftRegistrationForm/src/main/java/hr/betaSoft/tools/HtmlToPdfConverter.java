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

        String modifiedHtmlContent = htmlContent.replace("true", "Da")
                .replace("false", "Ne")
                .replace("<br>", "<br/>");

        org.jsoup.nodes.Document doc = Jsoup.parse(modifiedHtmlContent);
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

        Document pdfDocument = new Document(PageSize.A4);

        PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFilePath));

        pdfDocument.open();

        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

        BaseFont bf = BaseFont.createFont("font/Calibri-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(bf, 11);

        try (InputStream inputStream = new ByteArrayInputStream(doc.outerHtml().getBytes(StandardCharsets.UTF_8))) {
            worker.parseXHtml(pdfWriter, pdfDocument, inputStream, StandardCharsets.UTF_8, new FontFactoryImp() {
                @Override
                public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color) {
                    return font;
                }
            });
        }

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