package com.example.websocket_chat_application.configuration;

import com.example.websocket_chat_application.constant.MgsType;
import com.example.websocket_chat_application.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor//tao cac constructors
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageSendingOperations; // gửi tin nhắn từ server qua client

    @EventListener //nếu user leave out thì sẽ khởi chạy sự kiện
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");//trả về object nên phải ép qua String

        if(Objects.nonNull(username)){
            log.info("User disconnected: {}", username);

            messageSendingOperations.convertAndSend("/topic/chat", Message
                    .builder().type(MgsType.LEAVE).sender(username).build());
        }
    }
}
