package com.example.websocket_chat_application.service;

import com.example.websocket_chat_application.dto.request.Message;
import com.example.websocket_chat_application.entity.MessageEntity;
import com.example.websocket_chat_application.responsitory.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    MessageRepository messageRepository;

    @Transactional
    public void saveMessage(Message request){
        MessageEntity message = MessageEntity.builder()
                .sender(request.getSender())
                .content(request.getContent())
                .receiver(request.getReceiver())
                .timestamp(LocalDateTime.now())
                .build();

        log.info(request.getReceiver() + request.getContent());
        messageRepository.save(message);
    }

    public List<MessageEntity> getChatHistory(String user1, String user2){
        return messageRepository.findBySenderOrReceiver(user1, user2);
    }
}
