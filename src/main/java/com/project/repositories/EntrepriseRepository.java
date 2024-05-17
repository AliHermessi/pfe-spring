package com.project.repositories;

import com.project.models.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrepriseRepository extends JpaRepository <Entreprise, Long> {

}
