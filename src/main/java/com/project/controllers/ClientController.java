package com.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.ClientDTO;
import com.project.models.Client;
import com.project.repositories.ClientRepository;
import com.project.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {
    @Autowired
    private LogService logService;
    @Autowired
    private ClientRepository clientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/getAll")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        List<ClientDTO> clientDTOs = convertToDTOs(clients);
        return ResponseEntity.ok(clientDTOs);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        Optional<Client> clientOpt = clientRepository.findById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            ClientDTO clientDTO = convertToDTO(client);
            return ResponseEntity.ok(clientDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addClient(@RequestBody Client client) {
        try {
            clientRepository.save(client);
            logService.saveLog("Client", client.getId(), "add", null, null);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody Client newClient) {
        Optional<Client> existingClientOpt = clientRepository.findById(id);
        if (existingClientOpt.isPresent()) {
            Client existingClient = existingClientOpt.get();

            String oldValue = existingClient.toString(); // Get string representation of the old client

            existingClient.setNom(newClient.getNom());
            existingClient.setEmail(newClient.getEmail());
            existingClient.setNumero_telephone(newClient.getNumero_telephone());
            existingClient.setAddress(newClient.getAddress());

            clientRepository.save(existingClient);

            String newValue = existingClient.toString(); // Get string representation of the updated client
            logService.saveLog("Client", id, "update", oldValue, newValue); // Log the changes

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (clientRepository.existsById(id)) {
            try {
                Client client = clientRepository.findById(id).orElseThrow();
                String oldValue = client.toString(); // Get string representation of the client before deletion

                clientRepository.deleteById(id);
                logService.saveLog("Client", id, "delete", oldValue, null); // Log the deletion

                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private ClientDTO convertToDTO(Client client) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(client.getId());
        clientDTO.setNom(client.getNom());
        clientDTO.setEmail(client.getEmail());
        clientDTO.setNumero_telephone(client.getNumero_telephone());
        clientDTO.setAddress(client.getAddress());
        // Assuming commandes conversion if needed
        return clientDTO;
    }

    private List<ClientDTO> convertToDTOs(List<Client> clients) {
        return clients.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}

