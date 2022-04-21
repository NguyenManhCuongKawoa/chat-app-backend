package com.amr.chatservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amr.chatservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username) throws Exception; 
}
