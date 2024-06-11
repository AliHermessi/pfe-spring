package com.project.controllers;

import com.project.models.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return message;
    }

    @MessageMapping("/messages")
    @SendTo("/topic/messages")
    public Message receiveMessage(Message message) {
        return message;
    }
}
