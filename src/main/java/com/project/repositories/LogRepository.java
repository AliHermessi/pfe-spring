package com.project.repositories;

import com.project.models.log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LogRepository extends JpaRepository<log,Long> {

@Query("SELECT l from log l where l.action = :action")
List<log> findByAction (String action);

}
