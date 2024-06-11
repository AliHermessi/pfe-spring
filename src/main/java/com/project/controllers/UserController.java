package com.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.UserDTO;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.RoleRepository;
import com.project.repositories.UserRepository;
import com.project.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LogService logService;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/getAll")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = convertToDTOs(users);
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            UserDTO userDTO = convertToDTO(user);
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        User user = new User();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user.setCin(userDTO.getCin());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encrypt the password

        List<Role> roles = userDTO.getRole().stream()
                .map(roleName -> roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toList());
        user.setRole(roles);

        User savedUser = userRepository.save(user);

        try {
            String newValue = objectMapper.writeValueAsString(savedUser);
            logService.saveLog("User", savedUser.getId(), "add", "", newValue); // Log the addition with no old value
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build(); // Return response indicating success
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO updatedUserDTO) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            String oldValue = "";
            String newValue = "";
            try {
                oldValue = objectMapper.writeValueAsString(existingUser);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            existingUser.setUsername(updatedUserDTO.getUsername());
            existingUser.setCin(updatedUserDTO.getCin());

            List<Role> roles = updatedUserDTO.getRole().stream()
                    .map(roleName -> roleRepository.findByRoleName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toList());
            existingUser.setRole(roles);

            userRepository.save(existingUser);

            try {
                newValue = objectMapper.writeValueAsString(existingUser);
                logService.saveLog("User", existingUser.getId(), "update", oldValue, newValue);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            UserDTO userDTO = convertToDTO(existingUser);
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            String oldValue = "";
            try {
                oldValue = objectMapper.writeValueAsString(existingUser);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            userRepository.deleteById(id);

            logService.saveLog("User", id, "delete", oldValue, "");

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private UserDTO convertToDTO(User user) {
        List<String> roleNames = new ArrayList<>();
        for (Role role : user.getRole()) {
            roleNames.add(role.getRoleName());
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getCin(),
                user.getPassword(),
                roleNames
        );
    }

    private List<UserDTO> convertToDTOs(List<User> users) {
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
