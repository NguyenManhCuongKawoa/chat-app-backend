package com.amr.chatservice.service;

import com.amr.chatservice.model.ChatRoom;
import com.amr.chatservice.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired private ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatId(
            String senderId, String recipientId) {
    	
    	Optional<ChatRoom> chatRoom = chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId);
    	
    	if(chatRoom.isPresent()) {
    		return Optional.of(chatRoom.get().getChatId());
    	} else {
    		 var chatId =
                     String.format("%s_%s", senderId, recipientId);

             ChatRoom senderRecipient = ChatRoom
                     .builder()
                     .chatId(chatId)
                     .senderId(senderId)
                     .recipientId(recipientId)
                     .build();

             ChatRoom recipientSender = ChatRoom
                     .builder()
                     .chatId(chatId)
                     .senderId(recipientId)
                     .recipientId(senderId)
                     .build();
             chatRoomRepository.save(senderRecipient);
             chatRoomRepository.save(recipientSender);

             return Optional.of(chatId);
    	}
    }
}
