package com.project.repositories;

import com.project.models.log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<log,Long> {
}
