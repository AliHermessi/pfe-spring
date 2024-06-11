package com.project.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.models.log;
import com.project.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.time.LocalDateTime;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void logChange(String entityName, Long entityId, Object oldValue, Object newValue, String action) {
        log log = new log();
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        try {
            log.setOldValue(objectMapper.writeValueAsString(oldValue));
            log.setNewValue(objectMapper.writeValueAsString(newValue));
        } catch (JsonProcessingException e) {
            // Handle the error appropriately in a real application
            e.printStackTrace();
        }
        logRepository.save(log);
    }

    public void saveLog(String entityName, Long entityId, String action, Object oldValue, Object newValue) {
        log logEntry = new log();
        logEntry.setEntityName(entityName);
        logEntry.setEntityId(entityId);
        logEntry.setAction(action);
        try {
            logEntry.setOldValue(oldValue != null ? objectMapper.writeValueAsString(oldValue) : null);
            logEntry.setNewValue(newValue != null ? objectMapper.writeValueAsString(newValue) : null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logEntry.setTimestamp(LocalDateTime.now());
        logRepository.save(logEntry);
    }

    public <T> T getOldValue(Long logId, Class<T> valueType) {
        log log = logRepository.findById(logId).orElse(null);
        if (log != null && log.getOldValue() != null) {
            try {
                return objectMapper.readValue(log.getOldValue(), valueType);
            } catch (JsonProcessingException e) {
                // Handle the error appropriately in a real application
                e.printStackTrace();
            }
        }
        return null;
    }

    public <T> T getNewValue(Long logId, Class<T> valueType) {
        log log = logRepository.findById(logId).orElse(null);
        if (log != null && log.getNewValue() != null) {
            try {
                return objectMapper.readValue(log.getNewValue(), valueType);
            } catch (JsonProcessingException e) {
                // Handle the error appropriately in a real application
                e.printStackTrace();
            }
        }
        return null;
    }
}
