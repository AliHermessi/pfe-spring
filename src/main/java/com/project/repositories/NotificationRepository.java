package com.project.repositories;

import com.project.models.Notification;
import com.project.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE :role MEMBER OF n.roles")
    List<Notification> findByRoles (String role);

    void deleteByEntityName(String produit);
}

