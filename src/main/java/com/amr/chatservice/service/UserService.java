package com.amr.chatservice.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amr.chatservice.model.User;
import com.amr.chatservice.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	public User signup(User user) throws Exception {
		Optional<User> u = userRepository.findByUsername(user.getUsername());
		
		if(u.isPresent()) {
			throw new Exception("Username existed!");
		}
		
		return userRepository.save(user);
	}
	
	public User login(String username) throws Exception {
		Optional<User> u = userRepository.findByUsername(username);
		
		if(!u.isPresent()) {
			throw new Exception("Username is not exist!");
		}
		
		return u.get();
	}
	
	
	public List<User> getUsersWithoutMe(int id) {
		List<User> users = userRepository.findAll();
		
		return users.stream().filter(user -> user.getId() != id).collect(Collectors.toList());
	}

	public User getUserById(long id) throws Exception {
		User u =  userRepository.findById(id).orElseThrow(() -> new Exception("User is not exist!"));
		
		return u;
	}
}
