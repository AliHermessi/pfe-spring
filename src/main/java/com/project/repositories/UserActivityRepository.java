package com.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.models.*;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

}
