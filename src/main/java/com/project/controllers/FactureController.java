package com.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.ElementFactureDTO;
import com.project.dto.FactureDTO;
import com.project.models.Commande;
import com.project.models.ElementFacture;
import com.project.models.Entreprise;
import com.project.models.Facture;
import com.project.repositories.*;
import com.project.services.LogService;
import com.project.services.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/factures")
public class FactureController {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private EntrepriseRepository companyrepository;

@Autowired
private ElementFactureRepository elementFactureRepository;
    @Autowired
    private LogService logService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping("/get/{id}")
    public ResponseEntity<FactureDTO> getFactureById(@PathVariable Long id) {
        Optional<Facture> factureOpt = factureRepository.findById(id);
        if (factureOpt.isPresent()) {
            Facture facture = factureOpt.get();
            FactureDTO factureDTO = convertToDTO(facture);

            return ResponseEntity.ok(factureDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/generatePdf/{id}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id, @RequestParam String user) {
        try {

            Facture factureRetrieved = factureRepository.getFactureById(id);
            FactureDTO facture = convertToDTO(factureRetrieved);
            Entreprise entreprise = (companyrepository.findById(Long.valueOf(1))).get();
            byte[] pdfBytes = PdfGenerator.generatePdf(facture,entreprise,user);
            // Send PDF bytes as response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "facture.pdf");
            headers.setContentLength(pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addFacture(@RequestBody FactureDTO factureDTO) {
        try {
            Facture facture = convertToEntity(factureDTO);
            for (ElementFactureDTO element : factureDTO.getElementFactures()) {
                double netht = (element.getPrix() * element.getQuantity()) -
                        (element.getPrix() * element.getQuantity() * (element.getRemise() / 100));
                double totalRemise = 0;
                totalRemise = totalRemise + (element.getPrix() * element.getQuantity() * (element.getRemise() / 100));
                double montantht = 0;
                montantht = montantht + netht;
                double netttc = netht + (netht * (element.getTax() / 100));
                double totalTax = 0;
                totalTax = totalTax + (netht * (element.getTax() / 100));
                double montantttc = 0;
                montantttc = montantttc + netttc;
                facture.setTotalTax(totalTax);
                facture.setTotalRemise(totalRemise);
                facture.setMontantTotalht(montantht);
                facture.setMontantTotalttc(montantttc);
                facture.setDateFacture(LocalDateTime.now());
            }
            factureRepository.save(facture);
            String newValue = objectMapper.writeValueAsString(factureDTO);
            logService.saveLog("Facture", null, "add", null, newValue);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        Optional<Facture> factureOpt = factureRepository.findById(id);
        if (factureOpt.isPresent()) {
            Facture facture = factureOpt.get();
            try {
                String oldValue = objectMapper.writeValueAsString(facture);
                factureRepository.deleteById(id);
                logService.saveLog("Facture", id, "delete", oldValue, null);
                return ResponseEntity.noContent().build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }




    @GetMapping("/getAll")
    public ResponseEntity<List<FactureDTO>> getAllFactures() {
        List<Facture> factures = factureRepository.findAll();
        List<FactureDTO> factureDTOs = convertToDTOs(factures);
        List<FactureDTO> generatedFactureDTO = new ArrayList<>();
        for (FactureDTO facture : factureDTOs){
            if (facture.isGenerated() == Boolean.TRUE)
            {
                generatedFactureDTO.add(facture);
            }
        }
        return ResponseEntity.ok(generatedFactureDTO);
    }

    private List<FactureDTO> convertToDTOs(List<Facture> factures) {
        return factures.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private FactureDTO convertToDTO(Facture facture) {
        FactureDTO factureDTO = new FactureDTO();
        factureDTO.setId(facture.getId());
        factureDTO.setCode(facture.getCode());
        factureDTO.setMontantTotal(facture.getMontantTotal());
        factureDTO.setGenerated(facture.getGenerated());
        factureDTO.setTotalRemise(facture.getTotalRemise());
        factureDTO.setTotalTax(facture.getTotalTax());
        factureDTO.setMontantTotalttc(facture.getMontantTotalttc());
        factureDTO.setMontantTotalht(facture.getMontantTotalht());
        factureDTO.setDateFacture(facture.getDateFacture());

        Optional<Commande> Optcommande = commandeRepository.findCommandeByFacture(facture);
        if (Optcommande.isPresent()) {
            Commande commande = Optcommande.get();

            if (commande.getClient() != null) {
                factureDTO.setNomClient(commande.getClient().getNom());
                factureDTO.setNomFournisseur("");
                factureDTO.setType_facture("OUT");
                factureDTO.setAddress(commande.getClient().getAddress());
            } else if (commande.getFournisseur() != null) {
                factureDTO.setNomFournisseur(commande.getFournisseur().getNom());
                factureDTO.setNomClient("");
                factureDTO.setType_facture("IN");
                factureDTO.setAddress(commande.getFournisseur().getAddress());
            }


        }
        List<ElementFacture> elementFacturesAll = elementFactureRepository.findByFacture(facture);
        List<ElementFactureDTO> elementFacturesDTO = new ArrayList<>();
         ElementFactureDTO elementFactureDTO;
        for (ElementFacture elementFacture : elementFacturesAll){

                elementFactureDTO=convertElementFactureToDTO(elementFacture);
                elementFacturesDTO.add(elementFactureDTO);

        }


        factureDTO.setElementFactures(elementFacturesDTO);


        return factureDTO;
    }


    private Facture convertToEntity(FactureDTO factureDTO) {
        Facture facture = new Facture();
        if(factureDTO.getId() != null) {
            facture.setId(factureDTO.getId());
        }
        facture.setCode(factureDTO.getCode());
        facture.setMontantTotal(factureDTO.getMontantTotal());
        facture.setMontantTotalht(facture.getMontantTotalht());
        facture.setMontantTotalttc(facture.getMontantTotalttc());
        facture.setTotalRemise(facture.getTotalRemise());
        facture.setTotalTax(facture.getTotalTax());
        facture.setDateFacture(factureDTO.getDateFacture());

        facture.setCommande(factureDTO.getCommande());
       // if(factureDTO.getElementFactures() != null) {
          //  facture.setElementsFacture(factureDTO.getElementFactures());
       // }
       // else {
        //    facture.setElementsFacture(factureDTO.getCommande().getElementsFacture());
       // }
        return facture;
    }

    private ElementFactureDTO convertElementFactureToDTO(ElementFacture elementFacture){
        ElementFactureDTO elementFactureDTO =new ElementFactureDTO() ;
        elementFactureDTO.setRefProduit(elementFacture.getRefcode());
        elementFactureDTO.setLibelle(elementFacture.getLibelle());
        elementFactureDTO.setPrix(elementFacture.getPrix());
        elementFactureDTO.setQuantity(elementFacture.getQuantity());
        elementFactureDTO.setRemise(elementFacture.getRemise());
        elementFactureDTO.setTax(elementFacture.getTax());
        return elementFactureDTO;
    }




}

