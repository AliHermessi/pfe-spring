package com.project.repositories;

import com.project.models.Conversation;
import com.project.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversation(Conversation conversation);
}

