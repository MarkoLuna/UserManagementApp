package com.springboot.controller;

import java.util.List;
import java.util.Optional;

import com.springboot.model.User;
import com.springboot.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.springboot.util.CustomErrorType;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	UserRepository userRepository;
	
	@Autowired
	public RestApiController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Retrieve All Users
	 * */ 
	@GetMapping("/user/")
	public ResponseEntity<List<User>> listAllUsers() {
		List<User> users = userRepository.findAll();
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	/**
	 * Retrieve Single User
	 * */
	@GetMapping("/user/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") String id) {
		logger.info("Fetching User with id {}", id);
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("User with id " + id 
					+ " not found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user.get(), HttpStatus.OK);
	}

	/**
	 * Create a User
	 * */
	@PostMapping(value = "/user/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		logger.info("Creating User : {}", user);

		if (user.getId() !=null && userRepository.findById(user.getId()).isPresent()) {
			logger.error("Unable to create. A User with name {} already exist", user.getName());
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("Unable to create. A User with name " + 
			user.getName() + " already exist."),HttpStatus.CONFLICT);
		}
		userRepository.save(user);

		return new ResponseEntity<User>(user, HttpStatus.CREATED);
		
//		HttpHeaders headers = new HttpHeaders();
//		headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
//		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	/**
	 * Update a User
	 * */
	@PutMapping(value = "/user/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateUser(@PathVariable("id") String id, @RequestBody User user) {
		logger.info("Updating User with id {}", id);

		Optional<User> currentUser = userRepository.findById(id);

		if (!currentUser.isPresent()) {
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

	/**
	 * Delete a User
	 * */
	@DeleteMapping("/user/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
		logger.info("Fetching & Deleting User with id {}", id);

		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			logger.error("Unable to delete. User with id {} not found.", id);
			return new ResponseEntity<CustomErrorType>(new CustomErrorType("Unable to delete. User with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		userRepository.delete(user.get());
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}
}