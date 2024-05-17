package com.project.controllers;

import com.project.dto.UserDTO;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.List;
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<Void> addUser(@RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User newUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setUsername(newUser.getUsername());
            existingUser.setCin(newUser.getCin());
            existingUser.setPassword(newUser.getPassword());
            existingUser.setRole(newUser.getRole());  // Assuming roles are stored as a list in User entity
            userRepository.save(existingUser);

            UserDTO userDTO = convertToDTO(existingUser);
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
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
