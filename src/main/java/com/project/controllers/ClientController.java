package com.project.controllers;

import com.project.dto.ClientDTO;
import com.project.models.Client;
import com.project.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

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
        System.out.println(client.getNom());
        System.out.println(client.getAddress());
        clientRepository.save(client);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody Client newClient) {
        Optional<Client> existingClientOpt = clientRepository.findById(id);
        if (existingClientOpt.isPresent()) {
            Client existingClient = existingClientOpt.get();

            existingClient.setNom(newClient.getNom());
            existingClient.setEmail(newClient.getEmail());
            existingClient.setNumero_telephone(newClient.getNumero_telephone());
            existingClient.setAddress(newClient.getAddress());

            clientRepository.save(existingClient);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return ResponseEntity.noContent().build();
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

