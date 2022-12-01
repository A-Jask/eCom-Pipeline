package com.example.demo.controller;

import com.example.demo.Security.HandmadeError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			log.info("User with Id  " +  id  + " not found" );
			return ResponseEntity.notFound().build();
		}
		else{
			log.info("User with Id " + id + " found");
			return ResponseEntity.ok(user.get());
		}

	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null){
			log.info("User with username " + username + " not found");
			 return ResponseEntity.notFound().build();
		}else{
			log.info("User with username " + username + " found");
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		if (!isPasswordAcceptable(createUserRequest)) {
			log.error("ERROR| with Password {} did not meet requirements", createUserRequest.getPassword());
			return ResponseEntity.badRequest().build();
		}

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		userRepository.save(user);
		log.info("CreateUser {} create successfully", createUserRequest.getUsername());
		return ResponseEntity.ok(user);
//		if(createUserRequest.getPassword().length() < 7  || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
//				log.error("Error with the Password. User can't be created {}", createUserRequest.getUsername());
//				return ResponseEntity.badRequest().build();
//			}
		}
	private boolean isPasswordAcceptable(CreateUserRequest createUserRequest) {
		return checkPassword(createUserRequest.getPassword()) &&
				(createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword()));
	}
	private boolean checkPassword(String password) {
		return password != null && password.length() > 4;
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object>  handleException(HttpServletResponse response, RuntimeException ex, HttpServletRequest request) {
		log.info("UserController|CreateUser|ERROR|{}", ex.getMessage());
		List<String> errors = new ArrayList<String>();
		errors.add("Error occurred at " + LocalDateTime.now());

		HandmadeError handmadeError = new HandmadeError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
		return new ResponseEntity<Object>(handmadeError, new HttpHeaders(), handmadeError.getStatus());
	}

	}
	

