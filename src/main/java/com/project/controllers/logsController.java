package com.project.controllers;

import com.project.models.log;
import com.project.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class logsController {

@Autowired
    LogRepository logRepository;



    @GetMapping("/getAll")
    public ResponseEntity<List<log>> getAllLogs() {
        List<log> logs = logRepository.findAll();
        return ResponseEntity.ok(logs);
    }















}
