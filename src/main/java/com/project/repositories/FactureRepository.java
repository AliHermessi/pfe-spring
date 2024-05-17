package com.project.repositories;

import com.project.dto.FactureDTO;
import com.project.models.Commande;
import com.project.models.Facture;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FactureRepository extends JpaRepository<Facture, Long> {


     Optional<Facture> getFactureByCode (String code);

    // @Query (" SELECT Facture.commande from Facture f where f.id = :factureId ")
    // Commande findCommandeByFacture (Long factureId);
     @Modifying
     @Transactional
     @Query("UPDATE Facture set commande = :commande where id = :factureId")
     void updateFactureCommandeId (Long factureId, Commande commande);

     @Query("Select f from Facture f where f.id=:id")
    Facture getFactureById(Long id);
}
