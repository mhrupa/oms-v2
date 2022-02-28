package com.technivaaran.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.UserDto;
import com.technivaaran.entities.User;
import com.technivaaran.services.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserController {

	@Autowired
	UserService userservice;

	@PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> saveUser(@RequestBody UserDto userDto) {
		log.info("User Creation started.");
		userservice.saveUser(userDto);
		log.info("User Creation completed.");
		return new ResponseEntity<OmsResponse>(OmsResponse.builder().message("User created successfully").build(),
				HttpStatus.CREATED);
	}

	@GetMapping("/users")
	public List<User> getAllUsers() {
		log.info("Get all users is called.");
		return userservice.getAllUsers();
	}

	@GetMapping("/users/{userId}")
	public User getUserById(@PathVariable(name = "userId") long userId) {
		log.info("Get user by Id called");
		return userservice.getUserById(userId);
	}

	@PutMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> updateUserById(@PathVariable(name = "userId") long userId,
			@RequestBody UserDto userDto) {
		log.info("Update user by Id called");
		userservice.updateUserById(userId, userDto);

		return new ResponseEntity<OmsResponse>(OmsResponse.builder().message("User updated successfully").build(),
				HttpStatus.OK);
	}

}
