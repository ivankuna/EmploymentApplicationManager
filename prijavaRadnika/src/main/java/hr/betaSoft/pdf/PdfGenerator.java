//package hr.betaSoft.pdf;
//
//import hr.betaSoft.model.Employee;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.PDRectangle;
//import org.apache.pdfbox.pdmodel.common.PDTable;
//import org.apache.pdfbox.pdmodel.common.PDTableCell;
//import org.apache.pdfbox.pdmodel.common.PDTableRow;
//import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;
//
//import java.io.IOException;
//import java.util.List;
//
//public class PdfGenerator {
//
//    public static void generatePdf(List<Employee> dataList) {
//        try {
//            // Create a new PDF document
//            PDDocument document = new PDDocument();
//            PDPage page = new PDPage(PDRectangle.A4);
//            document.addPage(page);
//
//            // Set up the table
//            float margin = 50;
//            float yStart = page.getMediaBox().getHeight() - margin;
//            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
//            float yPosition = yStart;
//            float bottomMargin = 70;
//            float yStartNewPage = page.getMediaBox().getHeight() - margin;
//
//            int rowsPerPage = 10;
//            int numRows = dataList.size();
//            int numPages = (numRows / rowsPerPage) + 1;
//
//            // Iterate over each page
//            for (int currentPage = 0; currentPage < numPages; currentPage++) {
//                if (currentPage > 0) {
//                    // Add a new page for the next set of rows
//                    page = new PDPage(PDRectangle.A4);
//                    document.addPage(page);
//                    yStartNewPage = page.getMediaBox().getHeight() - margin;
//                    yPosition = yStartNewPage;
//                }
//
//                // Set up the content stream for the page
//                PDPageContentStream contentStream = new PDPageContentStream(document, page);
//                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//                float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("Table Header") / 1000 * 12;
//
//                // Draw the table headers
//                drawTableHeader(contentStream, margin, yPosition, tableWidth);
//
//                // Draw the table content
//                drawTableContent(contentStream, margin, yPosition - 15, tableWidth, rowsPerPage, dataList, currentPage);
//
//                // Close the content stream
//                contentStream.close();
//            }
//
//            // Save the document to a file
//            document.save("output.pdf");
//
//            // Close the document
//            document.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void drawTableHeader(PDPageContentStream contentStream, float xStart, float yStart, float tableWidth)
//            throws IOException {
//        float margin = 15;
//        float yPosition = yStart;
//        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//        contentStream.setLineWidth(1f);
//        contentStream.moveTo(xStart, yPosition);
//        contentStream.lineTo(xStart + tableWidth, yPosition);
//        contentStream.stroke();
//
//        float columnWidth = tableWidth / 3f;
//        float yPositionTop = yPosition - 15;
//        contentStream.beginText();
//        contentStream.newLineAtOffset(xStart + margin, yPositionTop);
//        contentStream.showText("Column 1");
//        contentStream.endText();
//
//        contentStream.beginText();
//        contentStream.newLineAtOffset(xStart + margin + columnWidth, yPositionTop);
//        contentStream.showText("Column 2");
//        contentStream.endText();
//
//        contentStream.beginText();
//        contentStream.newLineAtOffset(xStart + margin + 2 * columnWidth, yPositionTop);
//        contentStream.showText("Column 3");
//        contentStream.endText();
//    }
//
//    private static void drawTableContent(PDPageContentStream contentStream, float xStart, float yStart, float tableWidth,
//                                         int rowsPerPage, List<Employee> dataList, int currentPage)
//            throws IOException {
//        float margin = 15;
//        float yPosition = yStart;
//        float columnWidth = tableWidth / 3f;
//        float tableHeight = 20f;
//
//        for (int row = 0; row < rowsPerPage; row++) {
//            if ((currentPage * rowsPerPage + row) >= dataList.size()) {
//                break;
//            }
//
//            Employee employee = dataList.get(currentPage * rowsPerPage + row);
//
//            contentStream.setLineWidth(1f);
//            contentStream.moveTo(xStart, yPosition);
//            contentStream.lineTo(xStart + tableWidth, yPosition);
//            contentStream.stroke();
//
//            // Draw data in the first column
//            contentStream.beginText();
//            contentStream.newLineAtOffset(xStart + margin, yPosition - 15);
//            contentStream.showText(Integer.toString(row + 1));
//            contentStream.endText();
//
//            // Draw data in the second column
//            contentStream.beginText();
//            contentStream.newLineAtOffset(xStart + margin + columnWidth, yPosition - 15);
//            contentStream.showText(employee.getOib() + "\n" + employee.getFirstName() + "\n" + employee.getLastName() + "\n" +
//                    employee.getDateOfSignUp() + "\n" + employee.getDateOFuPDATE() + "\n" + data.getDatumOdjaveIzPrijave());
//            contentStream.endText();
//
//            // Draw data in the third column
//            contentStream.beginText();
//            contentStream.newLineAtOffset(xStart + margin + 2 * columnWidth, yPosition - 15);
//            contentStream.showText(data.getDatumOdjaveStvarni() + "\n" + data.getRazlogOdjave() + "\n" + data.getNapomena());
//            contentStream.endText();
//
//            yPosition -= tableHeight;
//        }
//    }
//
//    public static void main(String[] args) {
//        // Assuming you have a List<YourEntity> dataList with your data
//        List<YourEntity> dataList = /* populate your list */;
//        generatePdf(dataList);
//    }
//}
//
