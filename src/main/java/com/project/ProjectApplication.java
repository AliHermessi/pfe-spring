package com.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.repositories.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.project.repositories")
public class ProjectApplication {
    public static void main(String[] args) {

        SpringApplication.run(ProjectApplication.class, args);
        String rawPassword = "pass";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Encoded Password: " + encodedPassword);




    }
}

