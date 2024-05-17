package com.project.repositories;

import com.project.models.Commande;
import com.project.models.ElementFacture;
import com.project.models.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ElementFactureRepository extends JpaRepository<ElementFacture, Long> {


    List<ElementFacture> findAll();

    @Query("SELECT e from ElementFacture e where e.commande=:commande")
    List<ElementFacture> findByCommande(Commande commande);

    @Query("SELECT e from ElementFacture e where e.facture=:facture")
    List<ElementFacture> findByFacture(Facture facture);
}
