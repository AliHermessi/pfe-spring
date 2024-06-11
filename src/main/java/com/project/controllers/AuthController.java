package com.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.AuthRequest;
import com.project.dto.AuthResponse;
import com.project.models.Entreprise;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.EntrepriseRepository;
import com.project.repositories.UserRepository;
import com.project.services.LogService;
import com.project.services.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    @Autowired
    private LogService logService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) {
        System.out.println("Received login request for cin: " + authenticationRequest.getCin());

        // Authenticate the user
        try {
            authenticate(authenticationRequest.getCin(), authenticationRequest.getPassword());
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Authentication failed");
        }

        // Retrieve user by cin
        User user = userRepository.findByCin(authenticationRequest.getCin());

        if (user == null) {
            System.out.println("User not found for cin: " + authenticationRequest.getCin());
            return ResponseEntity.badRequest().body("User not found");
        }

        List<Role> userRoles = user.getRole();
        List<String> roles = new ArrayList<>();

        for (Role role : userRoles) {
            roles.add(role.getRoleName());
        }

        System.out.println("user and role"+user.getCin()+user.getRole());
        if (roles.isEmpty()) {
            System.out.println("No roles found for user with cin: " + authenticationRequest.getCin());
            return ResponseEntity.badRequest().body("No roles found for the user");
        }

        System.out.println("Roles found for user with cin " + authenticationRequest.getCin() + ": " + roles);
        return ResponseEntity.ok(roles);
    }


    @GetMapping("/Retrieve_Username_ByCin")
    public ResponseEntity<User> retrieveUsernameByCin(@RequestParam String cin) {
        String username = null;
        List<User> users = userRepository.findAll();
        User userr = null;
        for (User user : users) {
            if (user.getCin().equals(cin)) {
                userr = user;
                return ResponseEntity.ok(userr);// Assuming getUsername method exists in User entity
            }
        }
        if (userr != null) {
            System.out.println("Username for Cin " + cin + ": " + username);
            return ResponseEntity.ok(userr);
        } else {
            System.out.println("Username not found for Cin: " + cin);
            return ResponseEntity.notFound().build();
        }
    }






    private void authenticate(String cin, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(cin, password));
        } catch (DisabledException e) {
            System.out.println("disables");
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            System.out.println("wrong info");
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
@Autowired
    EntrepriseRepository entrepriseRepository;
    @GetMapping("/entreprise")
    private ResponseEntity<Entreprise> GetEntreprise (){

        List<Entreprise> entreprise = entrepriseRepository.findAll();
        System.out.print(entreprise.get(0));
        return ResponseEntity.ok(entreprise.get(0));

    }


}




