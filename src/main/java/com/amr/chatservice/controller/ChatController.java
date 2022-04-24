package com.amr.chatservice.controller;

import com.amr.chatservice.model.ChatMessage;
import com.amr.chatservice.model.ChatNotification;
import com.amr.chatservice.model.MessageStatus;
import com.amr.chatservice.model.User;
import com.amr.chatservice.response.ResponseDto;
import com.amr.chatservice.service.ChatMessageService;
import com.amr.chatservice.service.ChatRoomService;

import com.amr.chatservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatRoomService chatRoomService;
    @Autowired private UserService userService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId());
        chatMessage.setChatId(chatId.get());

        ChatMessage saved = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),"/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName()));
    }

    @MessageMapping("/users/status/{id}/{status}")
    @SendTo("/user/status")
    private User changeStatus(@DestinationVariable  long id, @DestinationVariable int status) {
        User user = null;
        try {
            user = userService.changeStatus(id, status);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
