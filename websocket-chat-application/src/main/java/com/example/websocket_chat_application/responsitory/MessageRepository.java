package com.example.websocket_chat_application.responsitory;

import com.example.websocket_chat_application.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findBySenderOrReceiver(String sender, String receiver);
}
