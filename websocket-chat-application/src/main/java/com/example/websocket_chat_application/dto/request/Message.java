package com.example.websocket_chat_application.dto.request;

import com.example.websocket_chat_application.constant.MgsType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    MgsType type;
    String receiver;//null nếu chat nhóm
    String content;
    String sender;
}
