package com.example.websocket_chat_application.controller;

import com.example.websocket_chat_application.dto.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("chat.sendMessage") //lắng nghe tin nhắn từ client đến /app/chat.sendMessage
    @SendTo("/topic/chat")
    public Message sendMgs(@Payload Message mgs){
        return mgs;
    }

    @MessageMapping("chat.addUser")
    @SendTo("/topic/chat")
    public Message addUser(@Payload Message mgs,
                           SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", mgs.getSender());
        return mgs;
    }
}
