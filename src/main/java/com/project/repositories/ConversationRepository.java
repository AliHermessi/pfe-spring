package com.project.repositories;

import com.project.models.Conversation;
import com.project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByParticipantsContaining(User user);


    List<Conversation> findByParticipants(List<User> participants);
}

