package com.amr.chatservice.service;

import com.amr.chatservice.exception.ResourceNotFoundException;
import com.amr.chatservice.model.ChatMessage;
import com.amr.chatservice.repository.ChatMessageRepository;

import com.amr.chatservice.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository repository;
    @Autowired private ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
//        chatMessage.setStatus( Constant.MESSAGE_RECEIVED);
        repository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(Long senderId, Long recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, Constant.MESSAGE_RECEIVED);
    }
    
    public ChatMessage getLastMessage(Long senderId, Long recipientId) {

		Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId);
		if(chatId.isPresent()) {
			List<ChatMessage> chats = repository.findByChatId(chatId.get());
	    	
			if(chats != null && chats.size() > 0) {
				chats.sort(Comparator.comparing(ChatMessage::getId).reversed());
		    	
		        return chats.get(0);
			}
		} 
    	return null;
    	
    }

    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId) {
        var chatId = chatRoomService.getChatId(senderId, recipientId);

        var messages =
                chatId.map(cId -> repository.findByChatId(cId)).orElse(new ArrayList<>());

        if(messages.size() > 0) {
            ChatMessage lastChatMessage = messages.get(messages.size() - 1);
            if(lastChatMessage.getRecipientId() == senderId) {
                messages.stream().filter(m -> m.getStatus() != Constant.MESSAGE_ERROR);
                messages.forEach(chat -> {
                    chat.setStatus(Constant.MESSAGE_DELIVERED);
                    repository.save(chat);
                });
            }
        }

        return messages;
    }

    public ChatMessage findById(long id) {
        return repository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setStatus(Constant.MESSAGE_DELIVERED);
                    return repository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("can't find message (" + id + ")"));
    }

    public void updateStatuses(Long senderId, Long recipientId, String status) {
    	List<ChatMessage> chats = repository.findBySenderIdAndRecipientId(senderId, recipientId);
    	
    	chats.forEach(chat -> {
    		chat.setStatus(status);

        	repository.save(chat);
    	});
    	
    }

}
