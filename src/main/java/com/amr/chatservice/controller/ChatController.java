package com.amr.chatservice.controller;

import com.amr.chatservice.model.ChatMessage;
import com.amr.chatservice.model.ChatNotification;
import com.amr.chatservice.model.MessageStatus;
import com.amr.chatservice.service.ChatMessageService;
import com.amr.chatservice.service.ChatRoomService;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId());
        chatMessage.setChatId(chatId.get());

        ChatMessage saved = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),"/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName()));
    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<?> countNewMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId) {
    	
    	long times = chatMessageService.countNewMessages(senderId, recipientId);
    	ChatMessage lastMessage = chatMessageService.getLastMessage(senderId, recipientId);

        return ResponseEntity
                .ok(new CountMessage(times, lastMessage));
    }
    
    

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages ( @PathVariable String senderId,
                                                @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage ( @PathVariable long id) {
        return ResponseEntity
                .ok(chatMessageService.findById(id));
    }
    
    @Data
    @AllArgsConstructor
    private class CountMessage {
    	long times;
    	ChatMessage lastMessage;
    }
}
