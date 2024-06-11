package com.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.FournisseurDTO;
import com.project.models.Fournisseur;
import com.project.repositories.FournisseurRepository;
import com.project.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import com.project.*;

@RestController
@RequestMapping("/fournisseurs")
public class    FournisseurController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private FournisseurRepository fournisseurRepository;
    @Autowired
    private LogService logService;
    @GetMapping("/getAll")
    public ResponseEntity<List<FournisseurDTO>> getAllFournisseurs() {
        List<Fournisseur> fournisseurs = fournisseurRepository.findAll();

        List<FournisseurDTO> fournisseurDTOs = fournisseurs.stream()
                .map(fournisseur -> {
                    FournisseurDTO dto = new FournisseurDTO();
                    dto.setId(fournisseur.getId());
                    dto.setNom(fournisseur.getNom());
                    dto.setAddress(fournisseur.getAddress());
                    dto.setNumero(fournisseur.getNumero());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(fournisseurDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FournisseurDTO> getFournisseurById(@PathVariable Long id) {
        return fournisseurRepository.findById(id)
                .map(fournisseur -> {
                    FournisseurDTO dto = new FournisseurDTO();
                    dto.setId(fournisseur.getId());
                    dto.setNom(fournisseur.getNom());
                    dto.setAddress(fournisseur.getAddress());
                    dto.setNumero(fournisseur.getNumero());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<FournisseurDTO> addFournisseur(@RequestBody FournisseurDTO fournisseurDTO) {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setNom(fournisseurDTO.getNom());
        fournisseur.setAddress(fournisseurDTO.getAddress());
        fournisseur.setNumero(fournisseurDTO.getNumero());

        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);
        fournisseurDTO.setId(savedFournisseur.getId());

        try {
            String newValue = objectMapper.writeValueAsString(fournisseurDTO);
            logService.saveLog("Fournisseur", savedFournisseur.getId(), "add", null, newValue);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(fournisseurDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<FournisseurDTO> updateFournisseur(@PathVariable Long id, @RequestBody FournisseurDTO fournisseurDTO) {
        return fournisseurRepository.findById(id)
                .map(existingFournisseur -> {

                    String oldValue = null;
                    try {
                        oldValue = objectMapper.writeValueAsString(existingFournisseur);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    existingFournisseur.setNom(fournisseurDTO.getNom());
                        existingFournisseur.setAddress(fournisseurDTO.getAddress());
                        existingFournisseur.setNumero(fournisseurDTO.getNumero());

                        Fournisseur updatedFournisseur = fournisseurRepository.save(existingFournisseur);

                        FournisseurDTO updatedDto = new FournisseurDTO();
                        updatedDto.setId(updatedFournisseur.getId());
                        updatedDto.setNom(updatedFournisseur.getNom());
                        updatedDto.setAddress(updatedFournisseur.getAddress());
                        updatedDto.setNumero(updatedFournisseur.getNumero());

                    String newValue = null;
                    try {
                        newValue = objectMapper.writeValueAsString(updatedDto);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    logService.saveLog("Fournisseur", updatedFournisseur.getId(), "update", oldValue, newValue);

                        return ResponseEntity.ok(updatedDto);



                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable Long id) {
        if (fournisseurRepository.existsById(id)) {
            fournisseurRepository.findById(id).ifPresent(existingFournisseur -> {
                try {
                    String oldValue = objectMapper.writeValueAsString(existingFournisseur);
                    fournisseurRepository.deleteById(id);
                    logService.saveLog("Fournisseur", id, "delete", oldValue, null);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

