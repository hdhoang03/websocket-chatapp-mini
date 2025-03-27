package com.example.websocket_chat_application.controller;

import com.example.websocket_chat_application.dto.request.Message;
import com.example.websocket_chat_application.entity.MessageEntity;
import com.example.websocket_chat_application.responsitory.MessageRepository;
import com.example.websocket_chat_application.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
//@RequestMapping("/api/chat")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {

    SimpMessagingTemplate messagingTemplate;
    ChatService chatService;

    @MessageMapping("/chat.sendMessage") //lắng nghe tin nhắn từ client đến /app/chat.sendMessage
//    @SendTo("/topic/chat")
    public Message sendMgs(@Payload Message mgs){
        chatService.saveMessage(mgs);
        messagingTemplate.convertAndSend("/topic/chat", mgs);
        return mgs;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/chat")
    public Message addUser(@Payload Message mgs,
                           SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username", mgs.getSender());
        return mgs;
    }

    @MessageMapping("chat.privateMessage")
    public Message sendPrivateMessage(@Payload Message message){//, Principal principal
//        String sender = (principal != null) ? principal.getName() : message.getSender(); //Lấy tên người gửi
//        message.setSender(sender);

        String receiver = message.getReceiver();
        System.out.println("Gửi tin nhắn riêng đến: " + receiver); // Debug
        messagingTemplate.convertAndSendToUser(receiver, "/private", message);
        chatService.saveMessage(message);
        log.info("Tin nhắn được " + message.getSender() + " gửi riêng " + message.getReceiver() + " nội dung: " + message.getContent());
        return message;
    }

    @GetMapping("/history")
    public List<MessageEntity> getChatHistory(@RequestParam String user1,@RequestParam String user2){
        return chatService.getChatHistory(user1, user2);
    }
}
