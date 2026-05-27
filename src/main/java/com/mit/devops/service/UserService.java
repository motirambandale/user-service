package com.mit.devops.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.mit.devops.dto.RegisterRequest;
import com.mit.devops.entity.User;
import com.mit.devops.exception.ResourceNotFoundException;
import com.mit.devops.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                //.password(passwordEncoder.encode(request.getPassword()))
                .password(request.getPassword())
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

	/*
	 * public AuthResponse login(LoginRequest request) {
	 * 
	 * User user = userRepository.findByEmail(request.getEmail()) .orElseThrow(() ->
	 * new RuntimeException("Invalid email or password"));
	 * 
	 * if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	 * throw new RuntimeException("Invalid email or password"); }
	 * 
	 * String token = jwtService.generateToken(user.getEmail());
	 * 
	 * return new AuthResponse(token); }
	 */

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User updateUser(Long id, RegisterRequest request) {

        User user = getUserById(id);

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return userRepository.save(user);
    }

    public String deleteUser(Long id) {

        User user = getUserById(id);

        userRepository.delete(user);

        return "User deleted successfully";
    }
}