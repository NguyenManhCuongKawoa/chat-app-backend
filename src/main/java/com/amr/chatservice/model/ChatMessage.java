package com.amr.chatservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@GenericGenerator(name="my_increment", strategy = "increment")
public class ChatMessage {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String chatId;
   private Long senderId;
   private Long recipientId;
   private String senderName;
   private String recipientName;
   private String text;
   @ElementCollection
   @CollectionTable(name = "chat_images")
   @org.hibernate.annotations.CollectionId(columns = @Column(name = "id"),
           type = @org.hibernate.annotations.Type(type = "long"), generator = "my_increment")
   private List<String> images;
//   private List<String> files;
   private Date timestamp;
   private String status;

}
