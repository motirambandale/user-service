package com.mit.devops.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.mit.devops.dto.RegisterRequest;
import com.mit.devops.entity.User;
import com.mit.devops.exception.ResourceNotFoundException;
import com.mit.devops.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private User user;
	private RegisterRequest registerRequest;

	@BeforeEach
	void setup() {

		user = User.builder().id(1L).name("Motiram").email("motiram@gmail.com").password("password123").build();

		registerRequest = new RegisterRequest();
		registerRequest.setName("Motiram");
		registerRequest.setEmail("motiram@gmail.com");
		registerRequest.setPassword("password123");
	}

	@Test
	void testRegisterUser() {

		when(userRepository.save(any(User.class))).thenReturn(user);

		String response = userService.register(registerRequest);

		assertEquals("User registered successfully", response);

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testGetUserById_Success() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		User foundUser = userService.getUserById(1L);

		assertNotNull(foundUser);
		assertEquals("Motiram", foundUser.getName());
		assertEquals("motiram@gmail.com", foundUser.getEmail());

		verify(userRepository, times(1)).findById(1L);
	}

	@Test
	void testGetUserById_NotFound() {

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			userService.getUserById(1L);
		});

		verify(userRepository, times(1)).findById(1L);
	}

	@Test
	void testGetAllUsers() {

		List<User> users = Arrays.asList(user);

		when(userRepository.findAll()).thenReturn(users);

		List<User> result = userService.getAllUsers();

		assertEquals(1, result.size());
		assertEquals("Motiram", result.get(0).getName());

		verify(userRepository, times(1)).findAll();
	}

	@Test
	void testUpdateUser() {

		RegisterRequest updateRequest = new RegisterRequest();
		updateRequest.setName("Updated User");
		updateRequest.setEmail("updated@gmail.com");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		when(userRepository.save(any(User.class))).thenReturn(user);

		User updatedUser = userService.updateUser(1L, updateRequest);

		assertNotNull(updatedUser);
		assertEquals("Updated User", updatedUser.getName());
		assertEquals("updated@gmail.com", updatedUser.getEmail());

		verify(userRepository, times(1)).findById(1L);

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testDeleteUser() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		String response = userService.deleteUser(1L);

		assertEquals("User deleted successfully", response);

		verify(userRepository, times(1)).findById(1L);

		verify(userRepository, times(1)).delete(user);
	}

	@Test
	void testDeleteUser_NotFound() {

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			userService.deleteUser(1L);
		});

		verify(userRepository, times(1)).findById(1L);

		verify(userRepository, never()).delete(any(User.class));
	}
}