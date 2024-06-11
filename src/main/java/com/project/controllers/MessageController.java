package com.project.controllers;

import com.project.dto.MessageDTO;
import com.project.models.Conversation;
import com.project.models.Message;
import com.project.models.User;
import com.project.repositories.ConversationRepository;
import com.project.repositories.MessageRepository;
import com.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/messages")
public class MessageController {/*
    private final Map<Long, List<SseEmitter>> emittersMap = new HashMap<>();

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/sendMessage/{conversationId}")
    public ResponseEntity<?> sendMessage(@PathVariable Long conversationId, @RequestBody MessageDTO messageDto) {
        Conversation conversation = conversationRepository.findById(conversationId).orElse(null);
        if (conversation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation not found");
        }

        Message message = new Message();
        User user = userRepository.findById(messageDto.getSenderId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        message.setSender(user);
        message.setContent(messageDto.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setConversation(conversation);

        messageRepository.save(message);

        List<SseEmitter> emitters = emittersMap.getOrDefault(conversationId, new ArrayList<>());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(messageDto));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/streamMessages/{conversationId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessages(@PathVariable Long conversationId) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(10));
        emittersMap.computeIfAbsent(conversationId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emittersMap.get(conversationId).remove(emitter));
        emitter.onTimeout(() -> emittersMap.get(conversationId).remove(emitter));
        emitter.onError((e) -> emittersMap.get(conversationId).remove(emitter));

        return emitter;
    }

    @Scheduled(fixedRate = 30000)
    public void sendHeartbeats() {
        for (List<SseEmitter> emitters : emittersMap.values()) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            }
        }
    }

    @GetMapping("/getConversations/{userId}")
    public ResponseEntity<?> getConversations(@PathVariable Long userId) {
        List<User> users = new ArrayList<>();
        users.add(userRepository.findById(userId).get());
        List<Conversation> conversations = conversationRepository.findByParticipants(users);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/getMessages/{conversationId}")
    public ResponseEntity<?> getMessages(@PathVariable Long conversationId) {
        List<Message> messages = conversationRepository.findById(conversationId).get().getMessages();
        return ResponseEntity.ok(messages);
    }*/
}