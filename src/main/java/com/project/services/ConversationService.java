package com.project.services;
import com.project.models.*;

import java.util.*;
import java.util.stream.Collectors;

import com.project.repositories.ConversationRepository;
import com.project.repositories.UserRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Conversation> getConversationsForUser(String username) {
        User user = userRepository.findByUsername(username);
        return conversationRepository.findByParticipantsContaining(user);
    }

    public Conversation getConversation(Long id) {
        return conversationRepository.findById(id).get();
    }

    public Conversation findOrCreateConversation(String senderUsername, String recipientUsername) {
        User sender = userRepository.findByUsername(senderUsername);
        User recipient = userRepository.findByUsername(recipientUsername);

        List<Conversation> senderConversations = conversationRepository.findByParticipantsContaining(sender);
        for (Conversation conversation : senderConversations) {
            if (conversation.getParticipants().contains(recipient)) {
                return conversation;
            }
        }

        Conversation newConversation = new Conversation();
        newConversation.setParticipants(List.of(sender, recipient));
        return conversationRepository.save(newConversation);
    }


    public Conversation createConversation(String[] usernames) {
        // Get the current authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUserOptional = userRepository.findByUsername(currentUsername);
        if (currentUserOptional != null) {
            // Handle case when current user is not found
            throw new IllegalStateException("Current user not found");
        }
        User currentUser = currentUserOptional;

        // Find users by their usernames
        List<User> participants = new ArrayList<>();
        for (String username : usernames) {
            User userOptional = userRepository.findByUsername(username);
            if (userOptional != null) {
                participants.add(userOptional);
            } else {
                // Handle case when user is not found
                throw new IllegalArgumentException("User not found for username: " + username);
            }
        }

        // Add the current user to the participants set
        participants.add(currentUser);

        // Create a new conversation
        Conversation conversation = new Conversation();
        conversation.setParticipants(participants);

        // Save the conversation
        return conversationRepository.save(conversation);
    }
    public List<Conversation> getConversationsByUsername(String username) {
        List<Conversation> allConversations = conversationRepository.findAll();
        return allConversations.stream()
                .filter(conversation -> conversation.getParticipants().stream()
                        .map(User::getUsername)
                        .anyMatch(participant -> participant.equals(username)))
                .collect(Collectors.toList());
    }

}

