package com.project.repositories;

import com.project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u.cin from User u where u.cin = :cin")
    String findByCinString(String cin);

    @Query("select u from User u where u.cin =:cin")
    User findByCin(String cin);


    @Query("SELECT u from User u where u.username = :username")
    User findByUsername(String username);

}
