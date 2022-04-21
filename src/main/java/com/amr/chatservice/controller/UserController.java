package com.amr.chatservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amr.chatservice.model.User;
import com.amr.chatservice.repository.UserRepository;
import com.amr.chatservice.response.ResponseDto;
import com.amr.chatservice.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/signup")
	private ResponseEntity<?> signup(@RequestBody User user) {
		try {
			User userAfter = userService.signup(user);
			
			return ResponseEntity.ok(userAfter);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseDto(400, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@GetMapping("/login/{username}")
	private ResponseEntity<?> login(@PathVariable String username) {
		try {
			User userAfter = userService.login(username);
			
			return ResponseEntity.ok(userAfter);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseDto(400, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@GetMapping("/users/without/{id}")
	private ResponseEntity<?> getUserWithoutMe(@PathVariable int id) {
		List<User> users = userService.getUsersWithoutMe(id);
		
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/users/{id}")
	private ResponseEntity<?> getUserById(@PathVariable int id) {
		User user;
		try {
			user = userService.getUserById(id);

			return ResponseEntity.ok(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseDto(400, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
	}
}
