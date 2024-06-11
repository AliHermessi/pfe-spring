package com.project.services;
import com.project.models.*;

import java.time.LocalDateTime;
import java.util.Set;
import com.project.repositories.ConversationRepository;
import com.project.repositories.MessageRepository;
import com.project.repositories.UserRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    ConversationRepository conversationRepository;

    public Message sendMessage(String senderUsername, String recipientUsername, String content, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        User sender = userRepository.findByUsername(senderUsername);

        User recipient = userRepository.findByUsername(recipientUsername);

        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setConversation(conversation);

        conversation.getMessages().add(message);

        return messageRepository.save(message);
    }

    public List<Message> getMessagesForConversation(Long conversationId) {
        Conversation conversation = conversationService.getConversation(conversationId);
        return messageRepository.findByConversation(conversation);
    }
}

