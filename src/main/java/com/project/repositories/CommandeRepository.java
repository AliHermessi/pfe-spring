package com.project.repositories;

import com.project.models.Commande;
import com.project.models.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande,Long> {

@Query("Select c from Commande c where c.facture= :facture ")
    Optional<Commande> findCommandeByFacture (Facture facture) ;



    @Query("SELECT c FROM Commande c WHERE UPPER(c.codeCommande) LIKE CONCAT('%', UPPER(:query), '%')")
    List<Commande> searchCommandes(String query);
}
