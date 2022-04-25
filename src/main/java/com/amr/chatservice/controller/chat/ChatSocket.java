package com.amr.chatservice.controller.chat;

import com.amr.chatservice.model.ChatNotification;
import com.amr.chatservice.model.User;
import com.amr.chatservice.service.ChatMessageService;
import com.amr.chatservice.service.ChatRoomService;
import com.amr.chatservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatSocket {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private UserService userService;


    @MessageMapping("/chat")
    public void processMessage(@Payload ChatNotification chatNotification) {
        messagingTemplate.convertAndSendToUser(
                chatNotification.getRecipientId() + "","/messages",
                chatNotification
        );
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

}
