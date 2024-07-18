package com.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.*;
import com.project.models.*;
import com.project.repositories.*;
import com.project.services.LogService;
import com.project.services.PdfGenerator;
import com.project.services.PdfGeneratorBDL;
import com.project.services.PdfGeneratorCommande;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.models.Facture.generateFactureCode;
import org.json.JSONObject;

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
    private EntrepriseRepository companyrepository;
    @Autowired
    private ProduitRepository produitRepository;
    @Autowired
    private ElementFactureRepository elementFactureRepository;
    @Autowired
    private LogService logService;
    @Autowired
    LogRepository logRepository;
    @GetMapping("/getAll")
    public ResponseEntity<List<CommandeDTO>> getAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        List<CommandeDTO> commandeDTOs = convertToDTOs(commandes);
        return ResponseEntity.ok(commandeDTOs);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<CommandeDTO> getCommandeById(@PathVariable Long id) {
        Optional<Commande> commandeOpt = commandeRepository.findById(id);
        if (commandeOpt.isPresent()) {
            Commande commande = commandeOpt.get();
            CommandeDTO commandeDTO = convertToDTO(commande);
            return ResponseEntity.ok(commandeDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
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
            logService.saveLog("Facture", facture.getId(), "create", null,
                    objectMapper.writeValueAsString(facture));

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
            commande.setMontantTotal(commandeDTO.getMontantTotal());
            commande.setMontantTotalht(commandeDTO.getMontantTotalht());
            commande.setMontantTotalttc(commandeDTO.getMontantTotalttc());
            commande.setTotalRemise(commandeDTO.getTotalRemise());
            commande.setTotalTax(commandeDTO.getTotalRemise());
            commande.setAddressLivraison(commandeDTO.getAdresse());
            commande.setBDLisPrinted(false);
            commandeRepository.save(commande);

            commande.setDateCommande(null);
            try {
                if(commandeDTO.getId()!= null) {
                    logService.saveLog("Commande", commande.getId(),
                            "Modifier commande", null, objectMapper.writeValueAsString(commande));
                } else {
                    logService.saveLog("Commande", commande.getId(),
                            "Enregistrer commande", null, objectMapper.writeValueAsString(commande));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Handle exception properly
            }

            return ResponseEntity.ok(commande.getCodeCommande());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/ConvertirCommandeEtCreerFacture")
    public ResponseEntity<Long> convertirCommande(@RequestBody Long id) {
        try {
            // Retrieve Commande entity by ID
            Optional<Commande> commandeOpt = commandeRepository.findById(id);
            if (commandeOpt.isEmpty()) {
                // Handle case where Commande with given ID is not found
                return ResponseEntity.notFound().build();
            }

            Commande commande = commandeOpt.get();

            
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
            logService.saveLog("Facture", id, "facture a été créer d'après le commande" +
                            "de code ="+ commande.getCodeCommande(), null,
                    objectMapper.writeValueAsString(facture));

            return ResponseEntity.ok(facture.getId());
        } catch (Exception e) {
            // Handle JSON processing exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




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
        }

        if (commande.getFournisseur() != null) {
            commandeDTO.setFournisseur_id(commande.getFournisseur().getId());
            commandeDTO.setFournisseurName(commande.getFournisseur().getNom());
            commandeDTO.setType_commande("IN");
            commandeDTO.setNumero(commande.getFournisseur().getNumero());
        }

        List<ElementFacture> elementFacturesAll = elementFactureRepository.findByCommande(commande);
        List<ElementFactureDTO> elementFacturesDTO = new ArrayList<>();
        ElementFactureDTO elementFactureDTO;
        for (ElementFacture elementFacture : elementFacturesAll){

                elementFactureDTO=convertElementFactureToDTO(elementFacture);
                elementFacturesDTO.add(elementFactureDTO);

        }
        commandeDTO.setBDLisPrinted(commande.isBDLisPrinted());
        commandeDTO.setAdresse(commande.getAddressLivraison());
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
        elementFactureDTO.setElementFactureId(elementFacture.getId());
        elementFactureDTO.setProduitId(elementFacture.getProduit().getId());
        elementFactureDTO.setRefProduit(elementFacture.getRefcode());
        elementFactureDTO.setLibelle(elementFacture.getLibelle());
        elementFactureDTO.setPrix(elementFacture.getPrix());
        elementFactureDTO.setQuantity(elementFacture.getQuantity());
        elementFactureDTO.setRemise(elementFacture.getRemise());
        elementFactureDTO.setTax(elementFacture.getTax());
        return elementFactureDTO;
    }

    @GetMapping("/generatePdfBDL/{id}")
    public ResponseEntity<byte[]> generatePdfBDL (@PathVariable Long id, @RequestParam String user) {
        try {
            Commande commande = commandeRepository.getCommandeById(id);
            CommandeDTO commandeDTO = convertToDTO(commande);
            Entreprise entreprise = (companyrepository.findById(Long.valueOf(1))).get();
            byte[] pdfBytes = PdfGeneratorBDL.generatePdf(commandeDTO,entreprise,user);
            //commande.setBDLisPrinted(true);
           // commandeRepository.save(commande);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "commande.pdf");
            headers.setContentLength(pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/generatePdfCommande/{id}")
    public ResponseEntity<byte[]> generatePdfCommande(@PathVariable Long id, @RequestParam String user) {
        try {
            Commande commande = commandeRepository.getCommandeById(id);
            CommandeDTO commandeDTO = convertToDTO(commande);
            Entreprise entreprise = (companyrepository.findById(Long.valueOf(1))).get();
            byte[] pdfBytes = PdfGeneratorCommande.generatePdf(commandeDTO,entreprise,user);
            // Send PDF bytes as response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "commande.pdf");
            headers.setContentLength(pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/updateStatusAndQuantity")
    public ResponseEntity<?> updateStatusAndQuantity(@RequestBody UpdateRequest updateRequest) throws JsonProcessingException {
        Optional<Commande> optionalCommande = commandeRepository.findById(updateRequest.getCommandeId());
        if (!optionalCommande.isPresent()) {
            return ResponseEntity.badRequest().body("Commande not found");
        }

        Commande commande = optionalCommande.get();
        Optional<ElementFacture> optionalElementFacture = elementFactureRepository.findById(updateRequest.getElementFactureId());
        if (!optionalElementFacture.isPresent()) {
            return ResponseEntity.badRequest().body("ElementFacture not found");
        }

        ElementFacture elementFacture = optionalElementFacture.get();
        int newQuantity = elementFacture.getQuantity() - updateRequest.getQuantity();
        if (newQuantity < 0) {
            return ResponseEntity.badRequest().body("Insufficient quantity");
        }
        elementFacture.setQuantity(newQuantity);

        ElementFactureDTO elementFactureDTO = convertElementFactureToDTO(elementFacture);
        System.out.println(elementFactureDTO.getProduitId());
        Optional<Produit> optionalProduit = produitRepository.findById(elementFactureDTO.getProduitId());
        if (optionalProduit.isEmpty()) {
            return ResponseEntity.badRequest().body("Produit not found");
        }

        Produit produit = optionalProduit.get();
        if(!Objects.equals(updateRequest.getStatus(), "")) {
            produit.setStatus(updateRequest.getStatus());
        }
        if(!Objects.equals(updateRequest.getDescription(), "")) {
            produit.setDescription(updateRequest.getDescription());
        }
        produit.setQuantite(produit.getQuantite() + updateRequest.getQuantity());
        elementFactureRepository.save(elementFacture);
        produitRepository.save(produit);
        logService.saveLog("Produit",produit.getId(),"Produit retourné",objectMapper.writeValueAsString(updateRequest),"");
        return ResponseEntity.ok("Product updated successfully");
    }

    @GetMapping("/produit-retourné")
    public ResponseEntity<?> getProduits() {
        List<log> logs = logRepository.findByAction("Produit retourné");
        System.out.println(logs.size());
        List<ProcessedLogDTO> processedLogs = new ArrayList<>();

        for (log loge : logs) {
            // Extract JSON string from oldValue starting from first { to matching }
            String oldValue = extractJsonFromLog(loge.getOldValue());

            if (oldValue != null) {
                // Remove backslashes from the captured JSON string
                String cleanedJson = oldValue.replace("\\", "");
                // Parse the cleaned JSON string
                JSONObject oldValueJson = new JSONObject(cleanedJson);
                // Extract quantity, status, and description from JSON
                int quantity = oldValueJson.getInt("quantity");
                String status = oldValueJson.getString("status");
                String description = oldValueJson.getString("description");

                // Extract Commande ID and ElementFacture ID
                Long commandeId = oldValueJson.getLong("commandeId");
                Long elementFactureId = oldValueJson.getLong("elementFactureId");

                // Fetch Commande from repository
                Commande commande = commandeRepository.findById(commandeId).orElse(null);

                if (commande != null) {
                    // Fetch ElementFacture from Commande
                    ElementFacture elementFacture = null;
                    for (ElementFacture ef : commande.getElementsFacture()) {
                        if (ef.getId().equals(elementFactureId)) {
                            elementFacture = ef;
                            break;
                        }
                    }

                    if (elementFacture != null) {
                        // Convert ElementFacture entity to DTO
                        ElementFactureDTO elementFactureDTO = convertElementFactureToDTO(elementFacture);

                        // Fetch Produit details from repository
                        Produit produit = produitRepository.findById(elementFacture.getProduit().getId()).orElse(null);

                        if (produit != null) {
                            // Prepare data for table


                            ProcessedLogDTO processedLog = new ProcessedLogDTO();
                            processedLog.setLogId(loge.getId());
                            processedLog.setProduitLibelle(produit.getLibelle());
                            processedLog.setProduitBarcode(produit.getBarcode());
                            processedLog.setCommandeId(commande.getId());
                            processedLog.setDescription(description); // Set description from JSON
                            processedLog.setStatus(status); // Set status from JSON
                            processedLog.setQuantity(quantity); // Set quantity from JSON
                            processedLog.setElementFactureId(elementFacture.getId());

                            // Add processed log to list
                            processedLogs.add(processedLog);
                        }
                    }
                }
            }
        }
        System.out.println(processedLogs);
        return ResponseEntity.ok(processedLogs);
    }

    // Method to extract JSON string starting from first { to matching }
    private String extractJsonFromLog(String oldValue) {
        int start = oldValue.indexOf("{");
        if (start != -1) {
            int end = oldValue.lastIndexOf("}");
            if (end != -1) {
                return oldValue.substring(start, end + 1);
            }
        }
        return null; // Return null if no valid JSON found
    }

    @PostMapping("/update-log")
    public ResponseEntity<String> updateLog(@RequestBody UpdateRequest request) throws JsonProcessingException {
        Long logId = request.getLogId();
        int quantityToAdd = request.getQuantity();
        String status = request.getStatus();
        String description = request.getDescription();
        Long elementId = request.getElementFactureId();
        logRepository.delete(logRepository.findById(logId).get());
        ElementFacture elementFacture = elementFactureRepository.findById(elementId).get();
        if (elementFacture != null) {
            // Update quantity
            elementFacture.setQuantity(elementFacture.getQuantity() + quantityToAdd);
            elementFactureRepository.save(elementFacture);

            // Update Produit
            Produit produit = produitRepository.findById(elementFacture.getProduit().getId()).orElse(null);
            if (produit != null) {
                // Update quantity in Produit
                produit.setQuantite(produit.getQuantite() + quantityToAdd);
                // Update description and status
                produit.setDescription(description);
                produit.setStatus(status);
                produitRepository.save(produit);
            } else {
                return ResponseEntity.notFound().build();
            }

            // Create new log entry
           // logService.saveLog("Produit", produit.getId(),"Produit non retourné","",
           //         objectMapper.writeValueAsString(request) ); // Assuming logService.saveLog() exists

            return ResponseEntity.ok("Log updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    }

