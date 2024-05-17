package com.project.controllers;

import com.project.dto.CategorieDTO;
import com.project.models.Categorie;
import com.project.repositories.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        return ResponseEntity.ok(savedCategorieDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategorieDTO> updateCategorie(@PathVariable Long id, @RequestBody CategorieDTO categorieDTO) {
        Optional<Categorie> existingCategorieOptional = categorieRepository.findById(id);
        if (existingCategorieOptional.isPresent()) {
            Categorie existingCategorie = existingCategorieOptional.get();
            existingCategorie.setNom(categorieDTO.getNom());
            existingCategorie.setDescription(categorieDTO.getDescription());

            Categorie updatedCategorie = categorieRepository.save(existingCategorie);
            CategorieDTO updatedCategorieDTO = new CategorieDTO(updatedCategorie.getId(), updatedCategorie.getNom(), updatedCategorie.getDescription());

            return ResponseEntity.ok(updatedCategorieDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        if (categorieRepository.existsById(id)) {
            categorieRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
