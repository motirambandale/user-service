package com.mit.devops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mit.devops.dto.RegisterRequest;
import com.mit.devops.entity.User;
import com.mit.devops.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testRegisterUser() throws Exception {

		RegisterRequest request = new RegisterRequest();
		request.setName("Motiram");
		request.setEmail("motiram@gmail.com");
		request.setPassword("password123");

		Mockito.when(userService.register(any(RegisterRequest.class))).thenReturn("User registered successfully");

		mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))

				.andExpect(status().isOk()).andExpect(content().string("User registered successfully"));
	}

	@Test
	void testGetUserById() throws Exception {

		User user = User.builder().id(1L).name("Motiram").email("motiram@gmail.com").password("password123").build();

		Mockito.when(userService.getUserById(1L)).thenReturn(user);

		mockMvc.perform(get("/api/users/1"))

				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Motiram"))
				.andExpect(jsonPath("$.email").value("motiram@gmail.com"));
	}

	@Test
	void testGetAllUsers() throws Exception {

		List<User> users = Arrays.asList(
				User.builder().id(1L).name("Motiram").email("motiram@gmail.com").password("password123").build(),

				User.builder().id(2L).name("Amit").email("amit@gmail.com").password("password456").build());

		Mockito.when(userService.getAllUsers()).thenReturn(users);

		mockMvc.perform(get("/api/users"))

				.andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].name").value("Motiram")).andExpect(jsonPath("$[1].name").value("Amit"));
	}

	@Test
	void testUpdateUser() throws Exception {

		RegisterRequest request = new RegisterRequest();
		request.setName("Updated User");
		request.setEmail("updated@gmail.com");

		User updatedUser = User.builder().id(1L).name("Updated User").email("updated@gmail.com").password("password123")
				.build();

		Mockito.when(userService.updateUser(eq(1L), any(RegisterRequest.class))).thenReturn(updatedUser);

		mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))

				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Updated User"))
				.andExpect(jsonPath("$.email").value("updated@gmail.com"));
	}

	@Test
	void testDeleteUser() throws Exception {

		Mockito.when(userService.deleteUser(1L)).thenReturn("User deleted successfully");

		mockMvc.perform(delete("/api/users/1"))

				.andExpect(status().isOk()).andExpect(content().string("User deleted successfully"));
	}
}