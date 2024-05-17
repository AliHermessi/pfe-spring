package com.project.repositories;

import com.project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u.cin from User u where u.cin = :cin")
    String findByCinString(String cin);

    @Query("select u from User u where u.cin =:cin")
    User findByCin(String cin);
}
