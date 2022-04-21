package com.amr.chatservice.service;

import com.amr.chatservice.exception.ResourceNotFoundException;
import com.amr.chatservice.model.ChatMessage;
import com.amr.chatservice.model.MessageStatus;
import com.amr.chatservice.repository.ChatMessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository repository;
    @Autowired private ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        repository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }
    
    public ChatMessage getLastMessage(String senderId, String recipientId) {

		Optional<String> chatId = chatRoomService.getChatId(
                senderId, recipientId);
		if(chatId.isPresent()) {
			List<ChatMessage> chats = repository.findByChatId(chatId.get());
	    	
			if(chats != null && chats.size() > 0) {
				chats.sort(Comparator.comparing(ChatMessage::getId).reversed());
		    	
		        return chats.get(0);
			}
		} 
    	return null;
    	
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatId(senderId, recipientId);

        var messages =
                chatId.map(cId -> repository.findByChatId(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
        	messages.forEach(chat -> {
        		chat.setStatus(MessageStatus.DELIVERED);
        		repository.save(chat);
        	});
        }

        return messages;
    }

    public ChatMessage findById(long id) {
        return repository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return repository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("can't find message (" + id + ")"));
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
    	List<ChatMessage> chats = repository.findBySenderIdAndRecipientId(senderId, recipientId);
    	
    	chats.forEach(chat -> {
    		chat.setStatus(status);

        	repository.save(chat);
    	});
    	
    }

}
