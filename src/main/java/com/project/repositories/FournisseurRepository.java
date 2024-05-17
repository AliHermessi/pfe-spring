package com.project.repositories;

import com.project.models.Categorie;
import com.project.models.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    Optional<Fournisseur> findFournisseurByNom(String fournisseurName);
}
