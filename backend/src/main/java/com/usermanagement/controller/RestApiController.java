package com.usermanagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.usermanagement.model.User;
import com.usermanagement.repositories.UserRepository;
import com.usermanagement.util.CustomErrorType;

@RestController
@Validated
@RequestMapping(value = "/api")
public class RestApiController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	private final UserRepository userRepository;

	public RestApiController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/user/")
	public ResponseEntity<List<User>> listAllUsers() {
		List<User> users = userRepository.findAll();
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") @NonNull String id) {
		logger.info("Fetching User with id {}", id);

		Optional<User> user = userRepository.findById(id);

		if (user.isEmpty()) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("User with id " + id 
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user.get(), HttpStatus.OK);
	}

	@PostMapping(value = "/user/")
	public ResponseEntity<?> createUser(
		@RequestBody @NonNull User user,
		UriComponentsBuilder ucBuilder
	) {
		logger.info("Creating User : {}", user);

		Optional<User> currentUser = Optional.ofNullable(user.getId())
			.filter(id -> !id.isEmpty())
			.flatMap(userRepository::findById);

		if (currentUser.isPresent()) {
			logger.error("Unable to create. A User with id {} already exist", user.getId());
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("Unable to create. A User with id " + 
			user.getId() + " already exist."),HttpStatus.CONFLICT);
		}

		userRepository.save(user);
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}

	@PutMapping(value = "/user/{id}")
	public ResponseEntity<?> updateUser(
		@PathVariable("id") @NonNull String id, 
		@RequestBody @NonNull User user
	) {
		
		logger.info("Updating User with id {}", id);

		Optional<User> currentUser = userRepository.findById(id);

		if (currentUser.isEmpty()) {
			logger.error("Unable to update. User with id {} not found.", id);
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("Unable to upate. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		User existingUser = currentUser.get();
		existingUser.setName(user.getName());
		existingUser.setAge(user.getAge());
		existingUser.setSalary(user.getSalary());

		userRepository.save(existingUser);
		return new ResponseEntity<User>(existingUser, HttpStatus.OK);
	}

	@DeleteMapping("/user/{id}")	
	public ResponseEntity<?> deleteUser(@PathVariable("id") @NonNull String id) {
		
		logger.info("Fetching & Deleting User with id {}", id);

		var user = userRepository.findById(id);
		if (user.isPresent()) {
			userRepository.delete(user.get());
			return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<CustomErrorType>(new CustomErrorType("Unable to delete. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
	}
}