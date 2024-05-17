package com.project.controllers;

import com.project.dto.userActivityDTO;
import com.project.models.Produit;
import com.project.models.Role;
import com.project.models.User;
import com.project.models.UserActivity;
import com.project.repositories.ProduitRepository;
import com.project.repositories.UserActivityRepository;
import com.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/userActivity")
public class userActivityController {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @GetMapping("/getAll")
    public ResponseEntity<List<userActivityDTO>> getAllUserActivities() {
        List<UserActivity> userActivities = userActivityRepository.findAll();
        List<userActivityDTO> userActivityDTOs = convertToDTOs(userActivities);
        return ResponseEntity.ok(userActivityDTOs);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<userActivityDTO> getUserActivityById(@PathVariable Long id) {
        Optional<UserActivity> userActivityOpt = userActivityRepository.findById(id);
        if (userActivityOpt.isPresent()) {
            UserActivity userActivity = userActivityOpt.get();
            userActivityDTO userActivityDTO = convertToDTO(userActivity);
            return ResponseEntity.ok(userActivityDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private userActivityDTO convertToDTO(UserActivity userActivity) {
        userActivityDTO userActivityDTO = new userActivityDTO();
        userActivityDTO.setId(userActivity.getId());
        userActivityDTO.setUserId(userActivity.getUser().getId());
        userActivityDTO.setUserName(userActivity.getUser().getUsername());

        List<String> roleNames = new ArrayList<>();
        for (Role role : userActivity.getUser().getRole()) {
            roleNames.add(role.getRoleName());
        }
        String combinedRoles = String.join(" ", roleNames);
        userActivityDTO.setUserRole(combinedRoles);

        userActivityDTO.setLibelle(userActivity.getProduit().getLibelle());
        userActivityDTO.setPrix(userActivity.getProduit().getPrix());
        userActivityDTO.setCout(userActivity.getProduit().getCout());
        userActivityDTO.setAction(userActivity.getAction());
        userActivityDTO.setQuantite(String.valueOf(userActivity.getProduit().getQuantite()));
        userActivityDTO.setTimestamp(userActivity.getTimestamp());
        userActivityDTO.setDescription(userActivity.getProduit().getDescription());

        return userActivityDTO;
    }

    private List<userActivityDTO> convertToDTOs(List<UserActivity> userActivities) {
        List<userActivityDTO> userActivityDTOs = new ArrayList<>();
        for (UserActivity userActivity : userActivities) {
            userActivityDTOs.add(convertToDTO(userActivity));
        }
        return userActivityDTOs;
    }
}
