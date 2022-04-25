package com.amr.chatservice.controller.api;

import com.amr.chatservice.model.ChatMessage;
import com.amr.chatservice.response.ResponseDto;
import com.amr.chatservice.service.ChatMessageService;
import com.amr.chatservice.service.ChatRoomService;
import com.amr.chatservice.service.upload.FileUploadService;
import com.amr.chatservice.utils.Constant;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("messages")
public class ChatController {

    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatRoomService chatRoomService;
    @Autowired private FileUploadService storageService;

    @PostMapping("/save")
    public  ResponseEntity<?> saveMessage(@RequestBody ChatMessage chatMessage) {
        ChatMessage saved = null;
        try {
            var chatId = chatRoomService
                    .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId());
            chatMessage.setChatId(chatId.get());
            chatMessage.setStatus(Constant.MESSAGE_ERROR);
            saved = chatMessageService.save(chatMessage);

            return ResponseEntity.ok(saved);
        } catch(Exception e) {
            e.printStackTrace();
            saved.setStatus(Constant.MESSAGE_ERROR);
            saved = chatMessageService.save(saved);
            return new ResponseEntity<>(new ResponseDto(400, saved), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile[] files, HttpServletRequest httpServletRequest)  {
        List<String> filePaths = new ArrayList<String>();
        for(MultipartFile file : files) {
            String rootPath = httpServletRequest.getServletContext().getRealPath("/");
            filePaths.add(rootPath + storageService.saveAndReturnPath(file));
        }

        return ResponseEntity.ok(filePaths);
    }

    @GetMapping("/status/change/{id}/{status}")
    public ResponseEntity<?> changeMessageStatus(
            @PathVariable Long id,
            @PathVariable String status) {

        ChatMessage message = chatMessageService.findById(id);

        if (message != null) {
            message.setStatus(status);
            chatMessageService.save(message);
            return ResponseEntity.ok(message);
        } else {
            throw new RuntimeException("Chat message not found!!");
        }
    }

    @GetMapping("/{senderId}/{recipientId}/count")
    public ResponseEntity<?> countNewMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {
    	
    	long times = chatMessageService.countNewMessages(senderId, recipientId);
    	ChatMessage lastMessage = chatMessageService.getLastMessage(senderId, recipientId);

        return ResponseEntity
                .ok(new CountMessage(times, lastMessage));
    }
    
    

    @GetMapping("/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages( @PathVariable Long senderId,
                                                @PathVariable Long recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/{id}")
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
