package com.amr.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypingNotification {
    private Long senderId;
    private Long recipientId;
    private boolean typing;
}
