package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.technivaaran.dto.UserDto;
import com.technivaaran.entities.User;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.UserMapper;
import com.technivaaran.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserMapper userMapper;

	public User saveUser(UserDto userDto) {
		try {
			User user = userMapper.convertToEntity(userDto);
			return userRepository.save(user);

		} catch (DataIntegrityViolationException integrityViolationException) {
			throw new OMSException("User already exists with user name: " + userDto.getUserName());
		}
	}

	public List<User> getAllUsers() {

		return userRepository.findAll();
	}

	public User getUserById(long userId) {
		Optional<User> userOp = userRepository.findById(userId);

		if (userOp.isPresent()) {
			return userOp.get();
		} else {
			throw new OMSException("User not found for id : " + userId);
		}
	}

	public User getUserByEmailAndPassword(String email, String password) {
		Optional<User> userOp = userRepository.findUserByEmailAndPassword(email, password);

		if (userOp.isPresent()) {
			return userOp.get();
		} else {
			throw new OMSException("User not found for email : " + email + " and password : " + password);
		}
	}

	public User updateUserById(long userId, UserDto userDto) {
		Optional<User> userOp = userRepository.findById(userId);

		if (userOp.isPresent()) {
			User user = userOp.get();
			user.setPassword(userDto.getPassword());
			user.setStatus(StringUtils.hasLength(userDto.getStatus()) ? userDto.getStatus() : user.getStatus());

			return userRepository.save(user);
		} else {
			throw new OMSException("User not found for id : " + userId);
		}
	}
}
