package com.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.CommandeDTO;
import com.project.dto.ElementFactureDTO;
import com.project.dto.ProduitDTO;
import com.project.models.*;
import com.project.repositories.CategorieRepository;
import com.project.repositories.FournisseurRepository;
import com.project.repositories.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

import static com.project.models.Produit.generateBarcodeWithReturn;

@RestController
@RequestMapping("/produits")
public class ProduitController {

    @Autowired
    private ProduitRepository produitRepository;
    @Autowired
    private FournisseurRepository fournisseurRepository;
    @Autowired
    private CategorieRepository categorieRepository;

    @GetMapping("/getAll")
    public ResponseEntity<List<ProduitDTO>> getAllProduits() {
        List<Produit> produits = produitRepository.findAll();
        List<ProduitDTO> produitDTOs = convertToDTOs(produits);
        return ResponseEntity.ok(produitDTOs);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProduitDTO> getProduitById(@PathVariable Long id) {
        Optional<Produit> produitOpt = produitRepository.findById(id);
        if (produitOpt.isPresent()) {
            Produit produit = produitOpt.get();
            ProduitDTO produitDTO = convertToDTO(produit);
            return ResponseEntity.ok(produitDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduit(@RequestBody Produit produit) {
        // Log the incoming produit data
        System.out.println("Received produit: " + produit);

        // Validate the foreign keys before saving
        if (produit.getCategorie() == null || produit.getCategorie().getId() == null) {
            return ResponseEntity.badRequest().body("Invalid categorie_id");
        }

        if (produit.getFournisseur() == null || produit.getFournisseur().getId() == null) {
            return ResponseEntity.badRequest().body("Invalid fournisseur_id");
        }

        // Check if the categorie and fournisseur exist in the database
        if (!categorieRepository.existsById(produit.getCategorie().getId())) {
            return ResponseEntity.badRequest().body("Categorie does not exist");
        }

        if (!fournisseurRepository.existsById(produit.getFournisseur().getId())) {
            return ResponseEntity.badRequest().body("Fournisseur does not exist");
        }

        // Generate barcode if not provided
        if (produit.getBarcode() == null || produit.getBarcode().trim().isEmpty()) {
            produit.setBarcode(generateBarcodeWithReturn(produit));
        }

        // Set last_update and date_arrivage properly
        produit.setLast_update(LocalDateTime.now().toString());
        produit.setDate_arrivage(LocalDateTime.now().toString());

        // Save the produit
        produitRepository.save(produit);

        // Return a success response
        return ResponseEntity.ok().build();
    }




    @PutMapping("/update/{id}")
    public ResponseEntity<ProduitDTO> updateProduit(@PathVariable Long id, @RequestBody Produit newProduit) {
        Optional<Produit> existingProduitOpt = produitRepository.findById(id);
        if (existingProduitOpt.isPresent()) {
            Produit existingProduit = existingProduitOpt.get();

            existingProduit.setLibelle(newProduit.getLibelle());
            existingProduit.setDescription(newProduit.getDescription());
            existingProduit.setPrix(newProduit.getPrix());
            existingProduit.setTax(newProduit.getTax());
            existingProduit.setQuantite(newProduit.getQuantite());
            existingProduit.setDate_arrivage(newProduit.getDate_arrivage());
            existingProduit.setCategorie(newProduit.getCategorie());
            existingProduit.setFournisseur(newProduit.getFournisseur());
            existingProduit.setLast_update(newProduit.getLast_update());
            existingProduit.setStatus(newProduit.getStatus());
            existingProduit.setBarcode(newProduit.getBarcode());
            existingProduit.setBrand(newProduit.getBrand());
            existingProduit.setCout(newProduit.getCout());
            //logs
            produitRepository.save(existingProduit);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ProduitDTO convertToDTO(Produit produit) {
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        if (produitRepository.existsById(id)) {
            produitRepository.deleteById(id);
            //logs
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private List<ProduitDTO> convertToDTOs(List<Produit> produits) {
        return produits.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @PostMapping("/search")
    public ResponseEntity<List<ProduitDTO>> searchProduits(@RequestBody String query) {
        try {
            List<Produit> produits = produitRepository.searchProduits(query);
            return ResponseEntity.ok(convertToDTOs(produits));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/searchProduitByLibelle")
    public ResponseEntity<List<String>> searchProduitsByLibelle(@RequestBody Map<String, String> requestBody) {
        try {
            String query = requestBody.get("query");
            System.out.println(query);
            System.out.println(query);
            System.out.println(query);
            List<Produit> produits = produitRepository.searchProduits(query);
            List<String> libelles = produits.stream()
                    .map(Produit::getLibelle)
                    .collect(Collectors.toList());
            System.out.println(libelles);
            System.out.println(libelles);
            System.out.println(libelles);
            return ResponseEntity.ok(libelles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/searchBarCodes")
    public ResponseEntity<List<String>> searchBarCodes(@RequestBody Map<String, String> requestBody) {
        try {
            String query = requestBody.get("query");


            List<String> barCodes = produitRepository.searchProduitsBarCodes(query);


            return ResponseEntity.ok(barCodes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/updateReturned")
    public ResponseEntity<String> updateReturned(@RequestParam("refProduit") String refProduit,
                                                 @RequestParam("libelle") String libelle,
                                                 @RequestParam("quantite") int quantite,
                                                 @RequestParam("ch") String ch) {
        Produit existingProduit = produitRepository.findByBarCode(refProduit);
        if (existingProduit == null) {
            existingProduit = produitRepository.findByBarCodeAndLibelle(refProduit, libelle);
        }
        existingProduit.setQuantite(existingProduit.getQuantite() + quantite);
        if (!ch.isEmpty()) {
            existingProduit.setStatus(ch);
        }
        produitRepository.save(existingProduit);
        return ResponseEntity.ok().build();
    }

}
