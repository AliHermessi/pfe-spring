package com.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.CommandeDTO;
import com.project.dto.ElementFactureDTO;
import com.project.dto.ProduitDTO;
import com.project.models.*;
import com.project.repositories.*;
import com.project.services.LogService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.project.models.Facture.generateFactureCode;

@RestController
@RequestMapping("/commandes")
public class CommandeController {
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    @Autowired
    private LogService logService;
    @GetMapping("/getAll")
    public ResponseEntity<List<CommandeDTO>> getAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        List<CommandeDTO> commandeDTOs = convertToDTOs(commandes);
        return ResponseEntity.ok(commandeDTOs);
    }

    @PostMapping("/createFacture")
    public ResponseEntity<Facture> createAndGenerateFacture(@RequestBody Commande commande) {
        try {
            // Save the Commande
            commandeRepository.save(commande);

            // Create and save the Facture
            Facture facture = new Facture();
            facture.setElementsFacture(commande.getElementsFacture());
            facture.setCommande(commande);
            factureRepository.save(facture);

            // Log the creation of the Facture
            logService.saveLog("Facture", facture.getId(), "create", null, null);

            return ResponseEntity.ok(facture);
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/SaveCommande")
    public ResponseEntity<String> SaveCommande(@RequestBody CommandeDTO commandeDTO) {
        try {
            // Create and save Commande entity
            Commande commande;
            if (commandeDTO.getId()!= null) {
                commande = commandeRepository.findById(commandeDTO.getId()).orElseThrow();
            } else {
                commande = new Commande();
            }
            commande.setCodeCommande(commande.generateCodeCommande(commande));
            commande.setDateCommande(LocalDateTime.now());
            commande.setMontantTotal(commandeDTO.getMontantTotal());
            commande.setType_commande(commandeDTO.getType_commande());

            // Set Client or Fournisseur based on type_commande
            if ("OUT".equals(commande.getType_commande())) {
                Optional<Client> clientOptional = clientRepository.findById(commandeDTO.getClient_id());
                if (clientOptional.isPresent()) {
                    Client client = clientOptional.get();
                    commande.setClient(client);
                    client.getCommandes().add(commande);
                } else {
                    throw new RuntimeException("Client not found");
                }
            } else if ("IN".equals(commande.getType_commande())) {
                Optional<Fournisseur> fournisseurOptional = fournisseurRepository.findById(commandeDTO.getFournisseur_id());
                if (fournisseurOptional.isPresent()) {
                    Fournisseur fournisseur = fournisseurOptional.get();
                    commande.setFournisseur(fournisseur);
                    fournisseur.getCommandes().add(commande);
                } else {
                    throw new RuntimeException("Fournisseur not found");
                }
            }

            // Create and save Facture entity
            Facture facture;
            if (commande.getFacture()!= null) {
                facture = commande.getFacture();
            } else {
                facture = new Facture();
                facture.setGenerated(Boolean.FALSE);
                facture.setCode(generateFactureCode());
                commande.setFacture(facture);
            }
            facture.setDateFacture(LocalDateTime.now());
            facture.setMontantTotal(commande.getMontantTotal());
            factureRepository.save(facture);
            commandeRepository.save(commande);
            facture.setCommande(commande);

            // Create and save ElementFacture entities
            List<ElementFacture> elementFactures = new ArrayList<>();
            System.out.println(commandeDTO.getElementsFacture().get(0).getProduitId());
            System.out.println(commandeDTO.getElementsFacture().size());
            for (ElementFactureDTO elementFactureDTO : commandeDTO.getElementsFacture()) {
                ElementFacture elementFacture = new ElementFacture();
                System.out.println(elementFactureDTO.getPrix());
                System.out.println(elementFactureDTO.getProduitId());

                Optional<Produit> produitOptional = produitRepository.findById(elementFactureDTO.getProduitId());
                Produit produit = produitOptional.get();


                if (produitOptional.isPresent()) {
                    produit = produitOptional.get();
                } else {
                    // Product doesn't exist, create a new one
                    produit = new Produit();
                    // Set product details from the ElementFactureDTO

                    produit.setLibelle(elementFactureDTO.getLibelle()); // Assuming the DTO has a product name field
                    produit.setDescription("Added from Commande ID: " + commande.getId()); // Custom description
                    produit.setPrix(elementFactureDTO.getPrix());
                    produit.setCout(elementFactureDTO.getPrix());
                    produit.setTax((int)elementFactureDTO.getTax());
                    produit.setQuantite(elementFactureDTO.getQuantity());
                    produit.setBarcode(Produit.generateBarcodeWithReturn(produit));
                    // Save the new product
                    produit = produitRepository.save(produit);
                    // Log the creation of the new product
                    logService.saveLog("Produit", produit.getId(),
                            "add Produit créé à partir de Commande ID: " + commande.getId(),
                            null, objectMapper.writeValueAsString(produit));
                }





                if ("IN".equals(commande.getType_commande())) {
                    // If it's an incoming command
                    produit.setCout(elementFactureDTO.getPrix()); // Set cout to the prix from ElementFacture
                    produit.setQuantite(produit.getQuantite() + elementFactureDTO.getQuantity()); // Add quantity to existing quantity
                } else if ("OUT".equals(commande.getType_commande())) {
                    // If it's an outgoing command
                    produit.setQuantite(produit.getQuantite() - elementFactureDTO.getQuantity()); // Subtract quantity from existing quantity
                }

                produit.setTax((int)elementFactureDTO.getTax());
                elementFacture.setProduit(produitOptional.get());
                elementFacture.setLibelle(elementFactureDTO.getLibelle());
                elementFacture.setQuantity(elementFactureDTO.getQuantity());
                elementFacture.setPrix(elementFactureDTO.getPrix());
                elementFacture.setTax(elementFactureDTO.getTax());
                elementFacture.setRemise(elementFactureDTO.getRemise());
                elementFacture.setNetHT(elementFactureDTO.getNetHT());
                elementFacture.setNetTTC(elementFactureDTO.getNetTTC());
                elementFacture.setRefcode(elementFactureDTO.getRefProduit());
                // Set Commande and Facture to ElementFacture
                elementFacture.setCommande(commande);
                elementFacture.setFacture(facture);



                    produit.setLast_update(produit.getDate_arrivage());
                    if ("IN".equals(commande.getType_commande())) {
                    produit.setCout(elementFactureDTO.getPrix());
                    }
                    produit.setTax((int)elementFactureDTO.getTax());
                    produit.setQuantite(produit.getQuantite() + elementFactureDTO.getQuantity());
                    // Set other properties like prix, tax, remise, last_update, etc.
                    // Save updated produit
                    produitRepository.save(produit);


                // Save each ElementFacture
                elementFacture = elementFactureRepository.save(elementFacture);
                elementFactures.add(elementFacture);
            }

            // Set ElementFacture list to Facture
            elementFactureRepository.saveAll(elementFactures);

            // Set ElementFacture list to Facture
            facture.setElementsFacture(elementFactures);

            // Update and save the Facture entity with the ElementFacture list
            factureRepository.save(facture);
            factureRepository.updateFactureCommandeId(facture.getId(),commande);
            // Update and save the Commande entity with the Facture entity
            commande.setFacture(facture);
            commandeRepository.save(commande);
            try {
                logService.saveLog("Commande", commande.getId(),
                        "Saving Commande", null, objectMapper.writeValueAsString(commandeDTO));
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Handle exception properly
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/ConvertirCommandeEtCreerFacture")
    public ResponseEntity<Void> convertirCommande(@RequestBody Long id) {
        try {
            // Retrieve Commande entity by ID
            Optional<Commande> commandeOpt = commandeRepository.findById(id);
            if (commandeOpt.isEmpty()) {
                // Handle case where Commande with given ID is not found
                return ResponseEntity.notFound().build();
            }

            Commande commande = commandeOpt.get();

            // Log before converting Commande
           // String oldValue = objectMapper.writeValueAsString(commande);

            // Convert Commande and update Facture
            Facture facture = commande.getFacture();
            facture.setGenerated(Boolean.TRUE);
            facture.setDateFacture(LocalDateTime.now());
            facture.setCode(Facture.generateFactureCode());
            facture.setMontantTotalttc(commande.getMontantTotalttc());
            facture.setMontantTotalht(commande.getMontantTotalht());
            facture.setTotalRemise(commande.getTotalRemise());
            facture.setTotalTax(commande.getTotalTax());
            factureRepository.save(facture);

            // Save updated Commande
            commandeRepository.save(commande);

            // Log after converting Commande
          //  String newValue = objectMapper.writeValueAsString(commande);
            logService.saveLog("Facture", id, "Convertir Commande et Creer Facture", "", "");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Handle JSON processing exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }








    // Method to generate Facture code





    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        try {
            // Check if Commande with given ID exists
            Optional<Commande> existingCommandeOpt = commandeRepository.findById(id);
            if (existingCommandeOpt.isEmpty()) {
                // Handle case where Commande with given ID is not found
                return ResponseEntity.notFound().build();
            }

            // Log before deleting Commande
            Commande existingCommande = existingCommandeOpt.get();
            String oldValue = objectMapper.writeValueAsString(existingCommande);

            // Delete Commande
            commandeRepository.deleteById(id);

            // Log after deleting Commande
            logService.saveLog("Commande", id, "delete", oldValue, null);

            return ResponseEntity.noContent().build();
        } catch (JsonProcessingException e) {
            // Handle JSON processing exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private CommandeDTO convertToDTO(Commande commande) {
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


    private List<CommandeDTO> convertToDTOs(List<Commande> commandes) {
        return commandes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }



    @PostMapping("/search")
    public ResponseEntity<List<CommandeDTO>> searchCommandes(@RequestBody String query) {
        try {
            System.out.println(query);
            List<Commande> commandes = commandeRepository.searchCommandes(query);
            System.out.println(commandes.size());
            return ResponseEntity.ok(convertToDTOs(commandes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ElementFactureDTO convertElementFactureToDTO(ElementFacture elementFacture){
        ElementFactureDTO elementFactureDTO =new ElementFactureDTO() ;
        elementFactureDTO.setProduitId(elementFacture.getId());
        elementFactureDTO.setRefProduit(elementFacture.getRefcode());
        elementFactureDTO.setLibelle(elementFacture.getLibelle());
        elementFactureDTO.setPrix(elementFacture.getPrix());
        elementFactureDTO.setQuantity(elementFacture.getQuantity());
        elementFactureDTO.setRemise(elementFacture.getRemise());
        elementFactureDTO.setTax(elementFacture.getTax());
        return elementFactureDTO;
    }

    @PostMapping("/printBonLivraison")
    public ResponseEntity<String> printBonLivraison(@RequestBody CommandeDTO commandeDTO) {


        return null;
    }




}

