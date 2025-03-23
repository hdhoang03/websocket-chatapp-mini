package com.example.websocket_chat_application.dto;

import com.example.websocket_chat_application.constant.MgsType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    private MgsType type;
    private String content;
    private String sender;
}
