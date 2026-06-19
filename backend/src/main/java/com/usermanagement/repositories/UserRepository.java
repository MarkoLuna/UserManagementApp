package com.usermanagement.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.usermanagement.model.User;

public interface UserRepository extends MongoRepository<User, String>{
	
	User findByName(String name);
	
}
