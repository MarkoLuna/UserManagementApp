package com.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.springboot.model.User;
import com.springboot.repositories.UserRepository;

@SpringBootApplication(scanBasePackages = { "com.springboot" })
public class SpringBootRestApiApp implements CommandLineRunner {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestApiApp.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		this.userRepository.deleteAll();

		this.userRepository.save(new User("Marcos", 23, 30000, passwordEncoder.encode("password")));
		this.userRepository.save(new User("Gerardo", 17, 1000, passwordEncoder.encode("password")));

		// fetch all users
		log.info("Users found with findAll():");
		log.info("--------------------------------------------------------");
		this.userRepository.findAll().stream().forEach(user -> log.info(user.toString()));

		// fetch an individual user
		User marcoUser = userRepository.findByName("Marcos");
		log.info("Users found with findByFirstName('Marcos'): {}", marcoUser);

		User gerardoUser = userRepository.findByName("Gerardo");
		log.info("Users found with findByLastName('Gerardo'): {}", gerardoUser);
	}
}
