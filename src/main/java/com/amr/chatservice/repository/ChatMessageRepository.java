package com.amr.chatservice.repository;

import com.amr.chatservice.model.ChatMessage;
import com.amr.chatservice.model.MessageStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    long countBySenderIdAndRecipientIdAndStatus(
            String senderId, String recipientId, MessageStatus status);
    
    List<ChatMessage> findBySenderIdAndRecipientIdAndStatus(
    		 String senderId, String recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);
    
    List<ChatMessage> findBySenderIdAndRecipientId(String senderId, String recipientId);
}