package com.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.CategorieDTO;
import com.project.models.Categorie;
import com.project.repositories.CategorieRepository;
import com.project.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategorieController {

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private LogService logService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping("/getAll")
    public ResponseEntity<List<CategorieDTO>> getAllCategories() {
        List<Categorie> categories = categorieRepository.findAll();
        List<CategorieDTO> categorieDTOs = categories.stream()
                .map(c -> new CategorieDTO(c.getId(), c.getNom(), c.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categorieDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> getCategorieById(@PathVariable Long id) {
        Optional<Categorie> categorieOptional = categorieRepository.findById(id);
        if (categorieOptional.isPresent()) {
            Categorie categorie = categorieOptional.get();
            CategorieDTO categorieDTO = new CategorieDTO(categorie.getId(), categorie.getNom(), categorie.getDescription());
            return ResponseEntity.ok(categorieDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CategorieDTO> addCategorie(@RequestBody CategorieDTO categorieDTO) {
        Categorie categorie = new Categorie();
        categorie.setNom(categorieDTO.getNom());
        categorie.setDescription(categorieDTO.getDescription());

        Categorie savedCategorie = categorieRepository.save(categorie);
        CategorieDTO savedCategorieDTO = new CategorieDTO(savedCategorie.getId(), savedCategorie.getNom(), savedCategorie.getDescription());

        try {
            String newValue = objectMapper.writeValueAsString(savedCategorieDTO);
            logService.saveLog("Categorie", savedCategorie.getId(), "add", null, newValue);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // Handle exception
        }

        return ResponseEntity.ok(savedCategorieDTO);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<CategorieDTO> updateCategorie(@PathVariable Long id, @RequestBody CategorieDTO categorieDTO) {
        Optional<Categorie> existingCategorieOptional = categorieRepository.findById(id);
        if (existingCategorieOptional.isPresent()) {
            Categorie existingCategorie = existingCategorieOptional.get();
            String oldValue = existingCategorie.toString(); // Get string representation of the old category

            existingCategorie.setNom(categorieDTO.getNom());
            existingCategorie.setDescription(categorieDTO.getDescription());

            Categorie updatedCategorie = categorieRepository.save(existingCategorie);
            CategorieDTO updatedCategorieDTO = new CategorieDTO(updatedCategorie.getId(), updatedCategorie.getNom(), updatedCategorie.getDescription());

            try {
                String newValue = objectMapper.writeValueAsString(updatedCategorieDTO);
                logService.saveLog("Categorie", id, "update", oldValue, newValue); // Log the changes
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                // Handle exception
            }

            return ResponseEntity.ok(updatedCategorieDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        if (categorieRepository.existsById(id)) {
            try {
                Categorie categorie = categorieRepository.findById(id).orElseThrow();
                String oldValue = categorie.toString(); // Get string representation of the category before deletion

                categorieRepository.deleteById(id);
                logService.saveLog("Categorie", id, "delete", oldValue, null); // Log the deletion

                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
