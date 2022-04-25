package com.amr.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    private Long chatId;
    private Long senderId;
    private String senderName;
    private Long recipientId;
}
