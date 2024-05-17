package com.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.models.Produit;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Query("SELECT p FROM Produit p WHERE UPPER(p.libelle) LIKE CONCAT('%', UPPER(:query), '%') OR " +
            "UPPER(p.barcode) LIKE CONCAT('%', UPPER(:query), '%') OR " +
            "UPPER(p.description) LIKE CONCAT('%', UPPER(:query), '%')")
    List<Produit> searchProduits(@Param("query") String query);

    @Query("SELECT p.barcode FROM Produit p WHERE UPPER(p.barcode) LIKE CONCAT('%', UPPER(:query), '%')")
    List<String> searchProduitsBarCodes(@Param("query") String query);

    @Query("SELECT p from Produit p where p.barcode=:refProduit")
    Produit findByBarCode(String refProduit);

    @Query("SELECT p FROM Produit p where p.barcode=:refProduit and p.libelle=:libelle")
    Produit findByBarCodeAndLibelle(String refProduit,String libelle);



}
