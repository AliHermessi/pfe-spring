package com.project.services;

import com.project.models.Notification;
import com.project.models.Produit;
import com.project.models.Role;
import com.project.repositories.NotificationRepository;
import com.project.repositories.ProduitRepository;
import com.project.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final List<SseEmitter> emitters = new ArrayList<>();

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationSseService notificationSseService;

    @Autowired
    private RoleRepository roleRepository;

    public Notification createNotification(Notification notification) {
        notificationSseService.sendNotification(notification);
        return notificationRepository.save(notification);

    }


    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onTimeout(() -> removeEmitter(emitter));
        emitter.onError((e) -> removeEmitter(emitter));
    }

    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public void sendNotification(Notification notification) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(notification);
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getByRoleNotifications(String roleName) {
        return notificationRepository.findByRoles(roleName);
    }

    public void deleteNotificationById(Long id) {
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void deleteNotificationsForProduit() {
        notificationRepository.deleteByEntityName("Produit");
    }
@Transactional
    public void checkProducts() {
        try {
            List<Produit> produits = produitRepository.findAll();
            Role role1 = roleRepository.findByRoleName("STOCK").get();
            Role role2 = roleRepository.findByRoleName("ADMIN").get();
            List<Role> roles = new ArrayList<>();
            roles.add(role1);
            roles.add(role2);
            deleteNotificationsForProduit();
            for (Produit produit : produits) {
                StringBuilder message = new StringBuilder();
                boolean notify = false;

                if (produit.getQuantite() == 0) {
                    message.append("Quantity is null. ");
                    notify = true;
                } else {
                    if (produit.getQuantite() < produit.getMinStock()) {
                        message.append("Quantity is below minStock. ");
                        notify = true;
                    }
                    if (produit.getQuantite() > produit.getMaxStock()) {
                        message.append("Quantity is above maxStock. ");
                        notify = true;
                    }
                }

                if (isNullOrEmpty(produit.getLibelle())) {
                    message.append("Libelle is empty. ");
                    notify = true;
                }

                if (isNullOrEmpty(produit.getDescription())) {
                    message.append("Description is empty. ");
                    notify = true;
                }

                if (produit.getPrix() <= 0) {
                    message.append("Prix is invalid. ");
                    notify = true;
                }

                if (produit.getCout() == null || produit.getCout() <= 0) {
                    message.append("Cout is invalid or null. ");
                    notify = true;
                }

                if (produit.getUnite() <= 0) {
                    message.append("Unite is invalid. ");
                    notify = true;
                }

                if (produit.getTax() < 0) {
                    message.append("Tax is invalid. ");
                    notify = true;
                }

                if (isNullOrEmpty(produit.getDate_arrivage())) {
                    message.append("Date_arrivage is empty. ");
                    notify = true;
                }

                if (isNullOrEmpty(produit.getLast_update())) {
                    message.append("Last_update is empty. ");
                    notify = true;
                }

                if (isNullOrEmpty(produit.getStatus())) {
                    message.append("Status is empty. ");
                    notify = true;
                }

                if (isNullOrEmpty(produit.getBarcode())) {
                    message.append("Barcode is empty. ");
                    notify = true;
                }

                if (isNullOrEmpty(produit.getBrand())) {
                    message.append("Brand is empty. ");
                    notify = true;
                }

                if (produit.getCategorie() == null) {
                    message.append("Categorie is null. ");
                    notify = true;
                }

                if (produit.getFournisseur() == null) {
                    message.append("Fournisseur is null. ");
                    notify = true;
                }

                if (notify) {
                    Notification notification = new Notification();
                    notification.setMessage("Product issue: " + produit.getLibelle() + " - " + message.toString());
                    notification.setEntityName("Produit");
                    notification.setDate(LocalDateTime.now());
                    notification.setRoles(roles);
                    createNotification(notification);
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while checking products: " + e);
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

