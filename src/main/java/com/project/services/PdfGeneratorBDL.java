package com.project.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
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

public class PdfGeneratorBDL {
    public static byte[] generatePdf(CommandeDTO commande, Entreprise entreprise, String user) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        int numElementsPerPage = 12;

        int numElements = commande.getElementsFacture().size();
        int numPages = (int) Math.ceil((double) numElements / numElementsPerPage);

        for (int page = 1; page <= numPages; page++) {
            if (page > 1) {
                document.add(new AreaBreak());
            }

            addCommandeInfo(document, commande, entreprise, user);

            int startIndex = (page - 1) * numElementsPerPage;
            int endIndex = Math.min(startIndex + numElementsPerPage, numElements);
            List<ElementFactureDTO> pageElements = commande.getElementsFacture().subList(startIndex, endIndex);
            addElementFactures(document, pageElements);

            addFooter(document,page);
        }

        document.close();
        return outputStream.toByteArray();
    }

    private static void addCommandeInfo(Document document, CommandeDTO commande, Entreprise entreprise, String user) {
        try {
            PdfFont font = PdfFontFactory.createFont();
            Color LIGHT_GRAY = WebColors.getRGBColor("#000000");

            Table commandeInfoTable = new Table(new float[]{500, 500});
            commandeInfoTable.setWidth(500);
            commandeInfoTable.setBorder(new SolidBorder(LIGHT_GRAY, 0.5f, 1));

            Cell leftCell = new Cell();
            leftCell.add(new Paragraph("Eclairage moderne").setFontSize(20).setBold().setHorizontalAlignment(HorizontalAlignment.CENTER));
            //leftCell.add(new Paragraph("Commande: " + commande.getCodeCommande()));
            leftCell.add(new Paragraph("Fournisseur: " + commande.getFournisseurName()));
            commandeInfoTable.addCell(leftCell);

            Cell rightCell = new Cell();
            rightCell.add(new Paragraph("Bon de livraison").setFontSize(20).setBold().setHorizontalAlignment(HorizontalAlignment.CENTER));
            rightCell.add(new Paragraph("Date: " + commande.getDateCommande().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))));
            commandeInfoTable.addCell(rightCell);

            commandeInfoTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            Table operatorTable = new Table(new float[]{500});
            operatorTable.setWidth(500);
            operatorTable.addCell(new Cell().add(new Paragraph("Operateur: " + user).setFontSize(12)).setWidth(250));
            operatorTable.addCell(new Cell().add(new Paragraph("Date: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
                    .setFontSize(12)).setWidth(250));
            operatorTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

            document.add(commandeInfoTable);
            document.add(new Paragraph(" ")); // Add an empty paragraph for spacing
            document.add(operatorTable);
            document.add(new Paragraph(" ")); // Add an empty paragraph for spacing

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addElementFactures(Document document, List<ElementFactureDTO> elements) {
        try {
            PdfFont font = PdfFontFactory.createFont();
            Color LIGHT_GRAY = WebColors.getRGBColor("D3D3D3");

            Table table = new Table(new float[]{1, 2, 1});
            table.setWidth(500);

            // Header row
            Cell cell = new Cell().add(new Paragraph("ref.").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Libelle").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            cell = new Cell().add(new Paragraph("Quant.").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
            cell.setBackgroundColor(DeviceGray.GRAY);
            cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderTop(Border.NO_BORDER);
            cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
            cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
            table.addCell(cell);

            int addedElements = 0;
            for (ElementFactureDTO element : elements) {
                cell = new Cell().add(new Paragraph(element.getRefProduit()).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(element.getLibelle()).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(String.valueOf(element.getQuantity())).setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderTop(Border.NO_BORDER);
                cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                table.addCell(cell);

                addedElements++;
            }

            // Fill the remaining rows with empty cells if there are fewer than 12 elements
            while (addedElements < 12) {
                for (int i = 0; i < 3; i++) {
                    cell = new Cell().add(new Paragraph("\n").setFont(font).setFontSize(12).setFontColor(DeviceGray.BLACK));
                    cell.setBorderBottom(new SolidBorder(DeviceGray.GRAY, 0.5f));
                    cell.setBorderTop(Border.NO_BORDER);
                    cell.setBorderLeft(new SolidBorder(DeviceGray.GRAY, 0.5f));
                    cell.setBorderRight(new SolidBorder(DeviceGray.GRAY, 0.5f));
                    table.addCell(cell);
                }
                addedElements++;
            }

            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(table);
            document.add(new Paragraph("\n\n"));
        } catch (IOException e) {
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
