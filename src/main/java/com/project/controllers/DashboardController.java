package com.project.controllers;

import com.project.dto.*;
import com.project.models.Commande;
import com.project.models.ElementFacture;
import com.project.models.Produit;
import com.project.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ProduitRepository produitRepository;
    @Autowired
    private ElementFactureRepository elementFactureRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    @GetMapping("AllPVE")
  public ResponseEntity<List<PVE>> AllPVE (){
        System.out.println(getAllRecordProduits().size());
      return ResponseEntity.ok(getAllRecordProduits());
  }

    public List<PVE> getAllRecordProduits() {
        List<CommandeDTO> ListDTOExistCommande = convertCommandesToDTOs(commandeRepository.findAll());
        List<PVE> pves = new ArrayList<>();

        for (CommandeDTO commande : ListDTOExistCommande) {
            for (ElementFactureDTO element : commande.getElementsFacture()) {
                PVE pve = new PVE();
                String barcode = element.getRefProduit();
                int quantity = element.getQuantity();
                String fournisseurName;
                String clientName;

                Optional<Produit> produitOpt = produitRepository.findById(element.getProduitId());
                if (produitOpt.isPresent()) {
                    Produit produit = produitOpt.get();
                    String categorieName = produit.getCategorie().getNom();
                    Double cout;
                    if ("IN".equals(commande.getType_commande())) {
                        fournisseurName = commande.getFournisseurName();
                        clientName = "";
                        pve.setType_commande("IN");
                        if (produit.getPrix() == 0) {
                            continue;
                        }
                        cout = element.getPrix();
                    } else {
                        clientName = commande.getClientName();
                        fournisseurName = "";
                        pve.setType_commande("OUT");
                        cout = produit.getCout();
                    }


                    double coutValue = (cout != null) ? cout : 0.0;

                    pve.setProduitId(produit.getId());
                    pve.setBarcode(barcode);
                    pve.setLibelle(produit.getLibelle());
                    pve.setQuantity(quantity);
                    pve.setPrix(produit.getPrix());
                    pve.setCout(coutValue);
                    pve.setNetht(element.getNetHT());

                    double totalCout = quantity * coutValue;
                    double netht = element.getNetHT();

                    // Debugging statements


                    pve.setTotalCout(totalCout);
                    pve.setTotal(netht - totalCout);

                    // Additional debug statement to check for negative values
                    if (pve.getTotal() < 0) {
                    }

                    pve.setDate(commande.getDateCommande());
                    pve.setFournisseurName(fournisseurName);
                    pve.setCategorieName(categorieName);
                    pve.setClientName(clientName);

                    pves.add(pve);
                } else {
                    System.out.println("Produit with ID " + element.getProduitId() + " not found.");
                }
            }
        }
        System.out.println(pves.size());
        return pves;
    }

    @GetMapping("TotalValues")
    public ResponseEntity<TotalValues> GeneralTotalValues(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDate endDate) {

        List<CommandeDTO> ListCommaneDTO = convertCommandesToDTOs(commandeRepository.findAll());

        // Filter the list of commandeDTOs based on the start and end date


        TotalValues totalvalues = new TotalValues();
        totalvalues.setTotalProduits(produitRepository.findAll().size());
        totalvalues.setTotalCommande(commandeRepository.findAll().size());
        totalvalues.setTotalFacture(factureRepository.findAll().size());
totalvalues.setTotalClient(clientRepository.findAll().size());
totalvalues.setTotalFournisseur(fournisseurRepository.findAll().size());
        for (CommandeDTO commande : ListCommaneDTO) {
            for (ElementFactureDTO element : commande.getElementsFacture()) {
                double cost = 0;
                if (commande.getType_commande().equals("IN")) {
                    cost = element.getPrix() * element.getQuantity();
                    totalvalues.setTotalCost(totalvalues.getTotalCost() + cost);
                } else if (commande.getType_commande().equals("OUT")) {
                    totalvalues.setTotalSales(totalvalues.getTotalSales()+element.getQuantity());

                    cost = produitRepository.findById(element.getProduitId()).get().getCout() * element.getQuantity();
                    totalvalues.setTotalRevenu(totalvalues.getTotalRevenu() + (element.getNetHT() - cost));
                }
            }
        }

        return ResponseEntity.ok(totalvalues);
    }



    @PostMapping("AllPVEperDate")
    public List<PVE> getRecordProduitsInInterval(LocalDateTime start, LocalDateTime end) {
        List<PVE> allRecords = getAllRecordProduits();

        // Filter records based on the specified time interval
        List<PVE> filteredRecords = allRecords.stream()
                .filter(record -> record.getDate().isAfter(start) && record.getDate().isBefore(end))
                .collect(Collectors.toList());

        return filteredRecords;
    }

    public List<PVE> mergeRecords(List<PVE> records) {
        List<PVE> mergedRecords = new ArrayList<>();

        for (PVE record : records) {
            // Check if the produitId already exists in the merged list
            boolean found = false;
            for (PVE mergedRecord : mergedRecords) {
                if (mergedRecord.getProduitId() == record.getProduitId()) {
                    // If found, update the quantities and netHT
                    mergedRecord.setQuantity(mergedRecord.getQuantity() + record.getQuantity());
                    mergedRecord.setNetHT(mergedRecord.getNetHT() + record.getNetHT());
                    // Update TOTAL
                    mergedRecord.setTotal((mergedRecord.getQuantity() * mergedRecord.getCout()) - mergedRecord.getNetHT());
                    found = true;
                    break;
                }
            }
            if (!found) {
                // If not found, add a new record to the merged list
                PVE newRecord = new PVE();
                newRecord.setProduitId(record.getProduitId());
                newRecord.setBarcode(record.getBarcode());
                newRecord.setLibelle(record.getLibelle());
                newRecord.setQuantity(record.getQuantity());
                newRecord.setPrix(record.getPrix());
                newRecord.setCout(record.getCout());
                newRecord.setNetHT(record.getNetHT());
                // Calculate TOTAL for the new record
                newRecord.setTotalCout(newRecord.getQuantity() * newRecord.getCout());
                newRecord.setTotal((newRecord.getQuantity() * newRecord.getCout()) - newRecord.getNetHT());
                mergedRecords.add(newRecord);
            }
        }

        return mergedRecords;
    }


    @GetMapping("/pourcentagePVE")
    public  ResponseEntity<Map<String, Double>>  comparePVE() {
        System.out.println("Comparing last 90 days with previous 90 days:");

        // Calculate the current 90-day period (June, May, April)
        LocalDateTime currentEndDateTime = LocalDateTime.now().minusDays(1); // End of the current period
        LocalDateTime currentStartDateTime = currentEndDateTime.minusDays(89); // Start of the current period

        // Calculate the previous 90-day period (March, February, January)
        LocalDateTime previousEndDateTime = currentStartDateTime.minusDays(1); // End of the previous period
        LocalDateTime previousStartDateTime = previousEndDateTime.minusDays(89); // Start of the previous period
        List<PVE> pveList1 = new ArrayList<>();
        List<PVE> pveList2 = new ArrayList<>();

        // Adding sample data initialization
        // Recent 90 days (5 records)
        pveList1.add(new PVE(1L, "1234567890", "Produit A", "OUT", 5, 29.99, 15.0, 24.99, 75.0, 149.95,
                LocalDateTime.of(2024, 6, 30, 0, 0), "Fournisseur A", "Catégorie A", "Client A"));
        pveList1.add(new PVE(2L, "9876543210", "Produit B", "OUT", 3, 19.99, 12.0, 17.99, 36.0, 59.97,
                LocalDateTime.of(2024, 6, 29, 0, 0), "Fournisseur B", "Catégorie B", "Client B"));
        pveList1.add(new PVE(3L, "1357924680", "Produit C", "OUT", 7, 14.99, 8.0, 12.99, 56.0, 104.93,
                LocalDateTime.of(2024, 6, 28, 0, 0), "Fournisseur C", "Catégorie C", "Client C"));
        pveList1.add(new PVE(4L, "2468013579", "Produit D", "OUT", 2, 39.99, 20.0, 34.99, 40.0, 79.98,
                LocalDateTime.of(2024, 6, 27, 0, 0), "Fournisseur D", "Catégorie D", "Client D"));
        pveList1.add(new PVE(5L, "1122334455", "Produit E", "OUT", 4, 49.99, 25.0, 44.99, 100.0, 199.96,
                LocalDateTime.of(2024, 6, 26, 0, 0), "Fournisseur E", "Catégorie E", "Client E"));

        // Previous 90 days (5 records)
        pveList2.add(new PVE(6L, "9988776655", "Produit F", "OUT", 1, 9.99, 5.0, 8.99, 5.0, 9.99,
                LocalDateTime.of(2024, 4, 10, 0, 0), "Fournisseur F", "Catégorie F", "Client F"));
        pveList2.add(new PVE(7L, "7766554433", "Produit G", "OUT", 3, 29.99, 15.0, 24.99, 45.0, 89.97,
                LocalDateTime.of(2024, 4, 9, 0, 0), "Fournisseur G", "Catégorie G", "Client G"));
        pveList2.add(new PVE(8L, "5544332211", "Produit H", "OUT", 6, 19.99, 12.0, 17.99, 72.0, 119.94,
                LocalDateTime.of(2024, 4, 8, 0, 0), "Fournisseur H", "Catégorie H", "Client H"));
        pveList2.add(new PVE(9L, "1212121212", "Produit I", "OUT", 2, 14.99, 8.0, 12.99, 16.0, 29.98,
                LocalDateTime.of(2024, 4, 7, 0, 0), "Fournisseur I", "Catégorie I", "Client I"));
        pveList2.add(new PVE(10L, "1231231234", "Produit J", "OUT", 8, 9.99, 5.0, 8.99, 40.0, 79.92,
                LocalDateTime.of(2024, 4, 6, 0, 0), "Fournisseur J", "Catégorie J", "Client J"));


        // Retrieve records for the respective intervals
        List<PVE> records1 = getRecordProduitsInInterval(previousStartDateTime, previousEndDateTime);
        List<PVE> records2 = getRecordProduitsInInterval(currentStartDateTime, currentEndDateTime);

        double totalNetHT1 = pveList1.stream().mapToDouble(PVE::getTotal).sum();
        double totalNetHT2 = pveList2.stream().mapToDouble(PVE::getTotal).sum();

        // Calculate total cost for both periods
        double totalCost1 = pveList1.stream().mapToDouble(PVE::getTotalCout).sum();
        double totalCost2 = pveList2.stream().mapToDouble(PVE::getTotalCout).sum();

        // Calculate total sales (quantity sold) for both periods
        int totalSales1 = pveList1.stream().mapToInt(PVE::getQuantity).sum();
        int totalSales2 = pveList2.stream().mapToInt(PVE::getQuantity).sum();

        // Calculate growth percentage
        double growthPercentage = ((totalNetHT2 - totalNetHT1) / totalNetHT1) * 100;

        // Calculate cost percentage
        double costPercentage = ((totalCost2 - totalCost1) / totalCost1) * 100;

        // Calculate sales percentage
        double salesPercentage = ((totalSales2 - totalSales1) / (double) totalSales1) * 100;

        // Prepare response with all three percentages
        Map<String, Double> percentages = new HashMap<>();
        percentages.put("growthPercentage", growthPercentage);
        percentages.put("costPercentage", costPercentage);
        percentages.put("salesPercentage", salesPercentage);
System.out.println(percentages);
        return ResponseEntity.ok(percentages);
    }







    private PVE findPVEByBarcode(List<PVE> pves, String barcode) {
        for (PVE pve : pves) {
            if (pve.getBarcode().equals(barcode)) {
                return pve;
            }
        }
        return null;
    }






    private List<ProduitDTO> convertProduitsToDTOs(List<Produit> produits) {
        return produits.stream().map(this::convertProduitToDTO).collect(Collectors.toList());
    }
    private ProduitDTO convertProduitToDTO(Produit produit) {
        ProduitDTO produitDTO = new ProduitDTO();
        produitDTO.setId(produit.getId());
        produitDTO.setLibelle(produit.getLibelle());
        produitDTO.setDescription(produit.getDescription());
        produitDTO.setPrix(produit.getPrix());
        produitDTO.setTax(produit.getTax());
        produitDTO.setQuantite(produit.getQuantite());
        produitDTO.setDate_arrivage(produit.getDate_arrivage());
        produitDTO.setCategorieId(produit.getCategorie().getId());
        produitDTO.setCategorieName(produit.getCategorie().getNom());
        produitDTO.setFournisseurId(produit.getFournisseur().getId());
        produitDTO.setFournisseurName(produit.getFournisseur().getNom());
        produitDTO.setLast_update(produit.getLast_update());
        produitDTO.setStatus(produit.getStatus());
        produitDTO.setBarcode(produit.getBarcode());
        produitDTO.setBrand(produit.getBrand());
        produitDTO.setCout(produit.getCout());
        produitDTO.setMaxStock(produit.getMaxStock());
        produitDTO.setMinStock(produit.getMinStock());
        return produitDTO;
    }

    private CommandeDTO convertCommandeToDTO(Commande commande) {
        CommandeDTO commandeDTO = new CommandeDTO();
        commandeDTO.setId(commande.getId());
        commandeDTO.setCodeFacture(commande.getFacture().getCode());
        commandeDTO.setDateCommande(commande.getDateCommande());
        commandeDTO.setMontantTotal(commande.getMontantTotal());
        commandeDTO.setType_commande(commande.getType_commande());
        commandeDTO.setCodeCommande(commande.getCodeCommande());
        commandeDTO.setFactureGenerated(commande.getFacture().getGenerated());
        if (commande.getClient() != null) {
            commandeDTO.setClient_id(commande.getClient().getId());
            commandeDTO.setClientName(commande.getClient().getNom());
            commandeDTO.setType_commande("OUT");
            commandeDTO.setNumero(commande.getClient().getNumero_telephone());
            commandeDTO.setAdresse(commande.getClient().getAddress());
        }

        if (commande.getFournisseur() != null) {
            commandeDTO.setFournisseur_id(commande.getFournisseur().getId());
            commandeDTO.setFournisseurName(commande.getFournisseur().getNom());
            commandeDTO.setType_commande("IN");
            commandeDTO.setNumero(commande.getFournisseur().getNumero());
            commandeDTO.setAdresse(commande.getFournisseur().getAddress());
        }

        List<ElementFacture> elementFacturesAll = elementFactureRepository.findByCommande(commande);
        List<ElementFactureDTO> elementFacturesDTO = new ArrayList<>();
        ElementFactureDTO elementFactureDTO;
        for (ElementFacture elementFacture : elementFacturesAll){

            elementFactureDTO=convertElementFactureToDTO(elementFacture);
            elementFacturesDTO.add(elementFactureDTO);

        }
        commandeDTO.setElementsFacture(elementFacturesDTO);
        return commandeDTO;
    }
    private List<CommandeDTO> convertCommandesToDTOs(List<Commande> commandes) {
        return commandes.stream().map(this::convertCommandeToDTO).collect(Collectors.toList());
    }
    private ElementFactureDTO convertElementFactureToDTO(ElementFacture elementFacture){
        ElementFactureDTO elementFactureDTO =new ElementFactureDTO() ;
        elementFactureDTO.setProduitId(elementFacture.getProduit().getId());
        elementFactureDTO.setRefProduit(elementFacture.getRefcode());
        elementFactureDTO.setLibelle(elementFacture.getLibelle());
        elementFactureDTO.setPrix(elementFacture.getPrix());
        elementFactureDTO.setQuantity(elementFacture.getQuantity());
        elementFactureDTO.setRemise(elementFacture.getRemise());
        elementFactureDTO.setTax(elementFacture.getTax());
        elementFactureDTO.setNetHT(elementFacture.getNetHT());
        elementFactureDTO.setNetTTC(elementFacture.getNetTTC());
        return elementFactureDTO;
    }
}
