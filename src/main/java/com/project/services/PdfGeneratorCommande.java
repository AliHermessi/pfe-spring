package com.project.services;

import com.project.dto.CommandeDTO;
import com.project.dto.FactureDTO;
import com.project.models.Entreprise;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;

import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.project.dto.CommandeDTO;
import com.project.dto.ElementFactureDTO;
import com.project.models.Entreprise;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY;
public class PdfGeneratorCommande {
    public static byte[] generatePdf(CommandeDTO commande, Entreprise entreprise, String user) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        int numElementsPerPage = 15;
        // Calculate the number of pages needed (we assume the same layout as before)
        int numElements = commande.getElementsFacture().size();
        int numPages = (int) Math.ceil((double) numElements / numElementsPerPage);

        // Add content to each page
        for (int page = 1; page <= numPages; page++) {
            // Add a new page
            if (page > 1) {
                document.add(new AreaBreak());
            }
            // Add commande info
            addCommandeInfo(document, commande, entreprise, user);

            // Add element factures
            int startIndex = (page - 1) * numElementsPerPage;
            int endIndex = Math.min(startIndex + numElementsPerPage, numElements);
            List<ElementFactureDTO> pageElements = commande.getElementsFacture().subList(startIndex, endIndex);
            addElementFactures(document, pageElements);
            addTotalInfo(document,commande);
            // Add footer
            addFooter(document, page);
        }

        document.close();
        return outputStream.toByteArray();
    }
    private static void addCommandeInfo(Document document, CommandeDTO commande, Entreprise entreprise, String user) {
        try {
            PdfFont font = PdfFontFactory.createFont();
            Color LIGHT_GRAY = WebColors.getRGBColor("#000000");

            // Create a table for the commande info
            Table commandeInfoTable = new Table(new float[]{500, 500});
            commandeInfoTable.setWidth(500);
            commandeInfoTable.setBorder(new SolidBorder(LIGHT_GRAY, 0.5f, 1));

            // Add content to the left column
            Cell leftCell = new Cell();
            leftCell.add(new Paragraph("Eclairage moderne").setFontSize(20).setBold().setHorizontalAlignment(HorizontalAlignment.CENTER));
            //leftCell.add(new Paragraph("Commande: " + commande.getCodeCommande())); // Don't include codeCommande
            //leftCell.add(new Paragraph("Fournisseur: " + commande.getFournisseurName())); // Don't include client/fournisseur part
            commandeInfoTable.addCell(leftCell);

            // Add content to the right column
            Cell rightCell = new Cell();
            rightCell.add(new Paragraph("DEVIS").setFontSize(20).setBold().setHorizontalAlignment(HorizontalAlignment.CENTER));
            rightCell.add(new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))));
            commandeInfoTable.addCell(rightCell);

            commandeInfoTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // Create a table for the operator and date
            Table operatorTable = new Table(new float[]{500});
            operatorTable.setWidth(500);
            operatorTable.addCell(new Cell().add(new Paragraph("Operateur: " + user).setFontSize(12)).setWidth(250));
            operatorTable.addCell(new Cell().add(new Paragraph("Date: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
                    .setFontSize(12)).setWidth(250));
            operatorTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // Create an empty table for spacing
           // Table emptyTable = new Table(new float[]{500, 500, 500, 500});
          //  for (int i = 0; i < 4; i++) {
          //      emptyTable.addCell(new Paragraph(" "));
          //  }
        //    emptyTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
         //   emptyTable.setWidth(500);

            // Add tables to the document
            document.add(commandeInfoTable);
            document.add(new Paragraph(" ")); // Add an empty paragraph for spacing
            document.add(operatorTable);
            document.add(new Paragraph(" ")); // Add an empty paragraph for spacing
           // document.add(emptyTable);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void addElementFactures(Document document, List<ElementFactureDTO> elements) {
        try {
            PdfFont font = PdfFontFactory.createFont();
            Color LIGHT_GRAY = WebColors.getRGBColor("D3D3D3");
            document.add(new Paragraph("\n\n").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));


            int numElements = elements.size();
            int numEmptyLines = 12 - numElements;
            System.out.println(numElements);
            System.out.println(numEmptyLines);

            Table table = new Table(new float[]{4,5, 4, 4, 4, 4, 4, 4});

            Cell cell = new Cell().add(new Paragraph("ref.").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Libelle").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Quant.").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Prix").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Rem.").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Net HT").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Tax").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Net TTC").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            for (ElementFactureDTO element : elements) {

                cell = new Cell().add(new Paragraph(element.getRefProduit()).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(element.getLibelle()).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                cell =new Cell().add(new Paragraph(String.valueOf(element.getQuantity())).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(String.valueOf(element.getPrix())).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(String.valueOf(element.getRemise())).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);


                cell = new Cell().add(new Paragraph(String.valueOf(element.getNetHT())).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(String.valueOf(element.getTax())).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(String.valueOf(element.getNetTTC())).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY,0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);


            }
            for (int i = 0; i < numEmptyLines; i++) {
                cell = new Cell().add(new Paragraph("\n").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderBottom(i == numEmptyLines - 1 ? new SolidBorder(DeviceGray.GRAY, 0.5f) : Border.NO_BORDER); // Add bottom border only to the last empty row
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add left border
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add right border
                table.addCell(cell);

                cell = new Cell();
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderBottom(i == numEmptyLines - 1 ? new SolidBorder(DeviceGray.GRAY, 0.5f) : Border.NO_BORDER); // Add bottom border only to the last empty row
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add right border
                table.addCell(cell);

                cell = new Cell();
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderBottom(i == numEmptyLines - 1 ? new SolidBorder(DeviceGray.GRAY, 0.5f) : Border.NO_BORDER); // Add bottom border only to the last empty row
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add right border
                table.addCell(cell);

                cell = new Cell();
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderBottom(i == numEmptyLines - 1 ? new SolidBorder(DeviceGray.GRAY, 0.5f) : Border.NO_BORDER); // Add bottom border only to the last empty row
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add right border
                table.addCell(cell);

                cell = new Cell();
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderBottom(i == numEmptyLines - 1 ? new SolidBorder(DeviceGray.GRAY, 0.5f) : Border.NO_BORDER); // Add bottom border only to the last empty row
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add right border
                table.addCell(cell);

                cell = new Cell();
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderBottom(i == numEmptyLines - 1 ? new SolidBorder(DeviceGray.GRAY, 0.5f) : Border.NO_BORDER); // Add bottom border only to the last empty row
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f)); // No left border
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add right border
                table.addCell(cell);

                cell = new Cell();
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderBottom(i == numEmptyLines - 1 ? new SolidBorder(DeviceGray.GRAY, 0.5f) : Border.NO_BORDER); // Add bottom border only to the last empty row
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f)); // No left border
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add right border
                table.addCell(cell);

                cell = new Cell();
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderBottom(i == numEmptyLines - 1 ? new SolidBorder(DeviceGray.GRAY, 0.5f) : Border.NO_BORDER); // Add bottom border only to the last empty row
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f)); // No left border
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f)); // Add right border
                table.addCell(cell);

            }



            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setWidth(500);
            document.add(table);
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("\n\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addTotalInfo(Document document, CommandeDTO facture) {
        try {
            PdfFont font = PdfFontFactory.createFont();

            Table totalTable = new Table(new float[]{10, 10, 10, 10});
            totalTable.setWidth(500);
            totalTable.addCell(new Cell().add(new Paragraph("Montant total HT: " + facture.getMontantTotalht())).setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK));
            totalTable.addCell(new Cell().add(new Paragraph("Total TVA: " + facture.getTotalTax() )).setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK));
            totalTable.addCell(new Cell().add(new Paragraph("Total remise: " + facture.getTotalRemise())).setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK));
            totalTable.addCell(new Cell().add(new Paragraph("Total TTC: " + facture.getMontantTotalttc())).setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK));
            totalTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(totalTable);     } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void addFooter(Document document, int pageNumber) {
        try {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Page number info
            String pageInfo = "Page " + pageNumber + " / " + document.getPdfDocument().getNumberOfPages();

            // Create a table for the footer
            Table table = new Table(1);
            table.setWidth(500);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setBorder(Border.NO_BORDER);

            // Add content to the table
            table.addCell(new Cell().add(new Paragraph(pageInfo).setFont(font).setFontSize(8))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER));

            // Set the table at the bottom of the page
            table.setFixedPosition(0, 20, 500); // 20 units from the bottom of the page

            document.add(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
