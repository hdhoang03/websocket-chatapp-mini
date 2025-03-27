package com.example.websocket_chat_application.event;

import com.example.websocket_chat_application.constant.MgsType;
import com.example.websocket_chat_application.dto.request.Message;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor//tao cac constructors
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class WebSocketEventListener {

    Map<String, String> activeUsers = new ConcurrentHashMap<>();
    final SimpMessageSendingOperations messageSendingOperations; // gửi tin nhắn từ server qua client

    @EventListener
    public void handleWebsocketConnectListener(SessionConnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null){
            activeUsers.put(username, username);
            sendUserListUpdate();
        }
    }

    @EventListener //nếu user leave out thì sẽ khởi chạy sự kiện
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");//trả về object nên phải ép qua String

        if(username != null){
            log.info("User disconnected: {}", username);
            activeUsers.remove(username);
            sendUserListUpdate();
            messageSendingOperations.convertAndSend("/topic/chat", Message
                    .builder().type(MgsType.LEAVE).sender(username).build());
        }
    }
    private void sendUserListUpdate(){
        messageSendingOperations.convertAndSend("/topic/user", activeUsers.values());
    }
}
