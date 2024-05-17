package com.project.services;

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
import com.project.dto.ElementFactureDTO;
import com.project.dto.FactureDTO;

import java.awt.*;

import com.itextpdf.kernel.colors.Color;
import com.project.models.Entreprise;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY;

public class PdfGenerator {


    public static byte[] generatePdf(FactureDTO facture, Entreprise entreprise,String user) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        int numElementsPerPage = 12;
        // Calculate the number of pages needed
        int numElements = facture.getElementFactures().size();
        int numPages = (int) Math.ceil((double) numElements / numElementsPerPage);

        // Add content to each page
        for (int page = 1; page <= numPages; page++) {
            // Add a new page
             if (page > 1) {
            document.add(new AreaBreak());
        }
            // Add facture info
            addFactureInfo(document, facture, entreprise,user);

            // Add element factures
            int startIndex = (page - 1) * numElementsPerPage;
            int endIndex = Math.min(startIndex + numElementsPerPage, numElements);
            List<ElementFactureDTO> pageElements = facture.getElementFactures().subList(startIndex, endIndex);
            addElementFactures(document, pageElements);

            // Add total info
            addTotalInfo(document, facture);
            addFooter(document,entreprise);
        }

        document.close();
        return outputStream.toByteArray();
    }



    private static void addFactureInfo(Document document, FactureDTO facture, Entreprise entreprise, String user) {
        try {
            Color LIGHT_GRAY = WebColors.getRGBColor("LIGHT_GRAY");
            PdfFont font = PdfFontFactory.createFont();

            // Left block for Facture and Date
            Paragraph leftBlock = new Paragraph()
                    .setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK);
            leftBlock.add(new Text("Facture: ").setBold());
            leftBlock.add(facture.getCode() + "\n");
            leftBlock.add(new Text("Date: ").setBold());
            leftBlock.add(facture.getDateFacture() + "\n");

            // Right block for Client/Fournisseur
            String clientOrFournisseur = facture.getNomClient() != null ? "Client: " + facture.getNomClient() : "Fournisseur: " + facture.getNomFournisseur();
            Paragraph rightBlock = new Paragraph(clientOrFournisseur)
                    .setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK);

            // Separation line
            LineSeparator line = new LineSeparator(new SolidLine());
            line.setStrokeColor(LIGHT_GRAY);

            // Small block for Operator and Date
            Paragraph smallBlock = new Paragraph()
                    .setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK).setBorder(new SolidBorder(DeviceRgb.BLACK, 1));
            smallBlock.add(new Text("Operateur: ").setBold());
            smallBlock.add(user + "\n");
            smallBlock.add(new Text("Date: ").setBold());
            smallBlock.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

            // Adding blocks to document
            document.add(leftBlock);
            document.add(rightBlock);
            document.add(line);
            document.add(smallBlock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    private static void addElementFactures(Document document, List<ElementFactureDTO> elements) {
        try {
            PdfFont font = PdfFontFactory.createFont();
            Color LIGHT_GRAY = WebColors.getRGBColor("LIGHT_GRAY");
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
                cell = new Cell().add(new Paragraph("").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
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

    private static void addTotalInfo(Document document, FactureDTO facture) {
        try {
            PdfFont font = PdfFontFactory.createFont();

            Table totalTable = new Table(new float[]{10, 10, 10, 10});
            totalTable.setWidth(500);
            totalTable.addCell(new Cell().add(new Paragraph("Montant total HT: " + facture.getMontantTotalht())).setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK));
            totalTable.addCell(new Cell().add(new Paragraph("Total TVA: " + facture.getTotalTax() )).setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK));
            totalTable.addCell(new Cell().add(new Paragraph("Total remise: " + facture.getTotalRemise())).setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK));
            totalTable.addCell(new Cell().add(new Paragraph("Total TTC: " + facture.getMontantTotalttc())).setFont(font).setFontSize(12).setFontColor(DeviceRgb.BLACK));
            totalTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            document.add(totalTable);     } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFooter(Document document, Entreprise entreprise) {
        try {
            PageSize pageSize = document.getPdfDocument().getDefaultPageSize();
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Footer text
            String address = "Address: " + entreprise.getAdresse();
            String codePostal = "Code Postal: " + entreprise.getCodePostal();
            String pageNumber = "Page " + document.getPdfDocument().getPageNumber(document.getPdfDocument().getLastPage()) + " / " + document.getPdfDocument().getNumberOfPages();
            String identifiant = "Identifiant Unique: " + entreprise.getIdentifiantUnique();
            String ccb = "C.C.B: " + entreprise.getCompteCourant();

            // Create a table for the footer
            Table table = new Table(new float[]{200, 100, 200});
            table.setWidth(500);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setBorder(Border.NO_BORDER);

            // Add cells for footer information
            Cell leftCell = new Cell().add(new Paragraph(address + "\n" + codePostal).setFont(font).setFontSize(7));
            leftCell.setBorder(Border.NO_BORDER);
            Cell centerCell = new Cell().add(new Paragraph(pageNumber).setFont(font).setFontSize(9));
            centerCell.setBorder(Border.NO_BORDER);
            Cell rightCell = new Cell().add(new Paragraph(identifiant + "\n" + ccb).setFont(font).setFontSize(7));
            rightCell.setBorder(Border.NO_BORDER);

            // Add cells to the table
            table.addCell(leftCell);
            table.addCell(centerCell);
            table.addCell(rightCell);

            // Set the fixed position for the table
            table.setFixedLayout();
            table.setFixedPosition(0, pageSize.getBottom() + 30, pageSize.getWidth());

            // Add the table to the document
            document.add(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}