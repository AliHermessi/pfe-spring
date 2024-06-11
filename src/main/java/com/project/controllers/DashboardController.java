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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                    System.out.println("Produit ID: " + produit.getId());
                    System.out.println("Quantity: " + quantity);
                    System.out.println("Cout Value: " + coutValue);
                    System.out.println("Net HT: " + netht);
                    System.out.println("Total Cout: " + totalCout);

                    pve.setTotalCout(totalCout);
                    pve.setTotal(netht - totalCout);

                    // Additional debug statement to check for negative values
                    if (pve.getTotal() < 0) {
                        System.out.println("Warning: Negative Total Value for Produit ID: " + produit.getId());
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
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<CommandeDTO> ListCommaneDTO = convertCommandesToDTOs(commandeRepository.findAll());

        // Filter the list of commandeDTOs based on the start and end date
        List<CommandeDTO> filteredCommandeDTOs = ListCommaneDTO.stream()
                .filter(commande -> commande.getDateCommande() != null)
                .filter(commande -> startDate == null || !commande.getDateCommande().isBefore(startDate.atStartOfDay()))
                .filter(commande -> endDate == null || commande.getDateCommande().isBefore(endDate.plusDays(1).atStartOfDay()))
                .collect(Collectors.toList());

        TotalValues totalvalues = new TotalValues();
        totalvalues.setTotalProduits(produitRepository.findAll().size());
        totalvalues.setTotalCommande(filteredCommandeDTOs.size());
        totalvalues.setTotalFacture(factureRepository.findAll().size());

        for (CommandeDTO commande : filteredCommandeDTOs) {
            totalvalues.setTotalSales(totalvalues.getTotalSales()+commande.getElementsFacture().size());
            for (ElementFactureDTO element : commande.getElementsFacture()) {
                double cost = 0;
                if (commande.getType_commande().equals("IN")) {
                    cost = element.getPrix() * element.getQuantity();
                    totalvalues.setTotalCost(totalvalues.getTotalCost() + cost);
                } else if (commande.getType_commande().equals("OUT")) {
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

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @GetMapping("/pourcentagePVE")
    public ResponseEntity<Double> comparePVE(
            @RequestParam String start1,
            @RequestParam String end1,
            @RequestParam String start2,
            @RequestParam String end2) {

        LocalDateTime startDateTime1 = LocalDateTime.parse(start1, formatter);
        LocalDateTime endDateTime1 = LocalDateTime.parse(end1, formatter);
        LocalDateTime startDateTime2 = LocalDateTime.parse(start2, formatter);
        LocalDateTime endDateTime2 = LocalDateTime.parse(end2, formatter);

        List<PVE> records1 = getRecordProduitsInInterval(startDateTime1, endDateTime1);
        List<PVE> records2 = getRecordProduitsInInterval(startDateTime2, endDateTime2);

        double totalNetHT1 = records1.stream().mapToDouble(PVE::getTotal).sum();
        double totalNetHT2 = records2.stream().mapToDouble(PVE::getTotal).sum();

        if (totalNetHT1 == 0 || totalNetHT2 == 0) {
            return ResponseEntity.badRequest().body(0.0); // Avoid division by zero
        }

        double growthPercentage = ((totalNetHT2 - totalNetHT1) / totalNetHT1) * 100;

        return ResponseEntity.ok(growthPercentage);
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
