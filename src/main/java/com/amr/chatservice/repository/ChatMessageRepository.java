package com.amr.chatservice.repository;

import com.amr.chatservice.model.ChatMessage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    long countBySenderIdAndRecipientIdAndStatus(
            Long senderId, Long recipientId, String status);
    
    List<ChatMessage> findBySenderIdAndRecipientIdAndStatus(
            Long senderId, Long recipientId, String status);

    List<ChatMessage> findByChatId(String chatId);
    
    List<ChatMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
}