package com.usermanagement.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.usermanagement.SpringBootRestApiApp;
import com.usermanagement.model.User;
import com.usermanagement.repositories.UserRepository;
import com.usermanagement.security.AccountCredentials;
import com.usermanagement.security.TokenAuthenticationService;

import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootRestApiApp.class) 
@AutoConfigureMockMvc
public class RestApiControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    private String authTokenHeader = "";

    @Autowired
    private ObjectMapper objectMapper;

    private List<User> userList = new ArrayList<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() throws Exception {
        this.userRepository.deleteAll();
        this.userList.add(new User("Marcos", 23, 30000, passwordEncoder.encode("password")));
        this.userList.add(new User("Gerardo", 17, 1000, passwordEncoder.encode("password")));
        
        this.userRepository.saveAll(userList);
        authToken();
    }
    
    public void authToken() throws Exception {
    	AccountCredentials accountCredentials = new AccountCredentials("Marcos", "password");
    	
    	MvcResult mvcResult = mockMvc.perform(post("/login")
    			.content(this.json(accountCredentials))
    			.contentType(MediaType.APPLICATION_JSON))
    			.andExpect(status().isOk())
    	.andExpect(header().string(TokenAuthenticationService.HEADER_STRING, notNullValue()))
    	.andReturn();
    	
    	authTokenHeader = mvcResult.getResponse().getHeader(TokenAuthenticationService.HEADER_STRING);
    	
    	assertNotNull(authTokenHeader);
    	
    }
    
    @Test
    public void UserNotFound() throws Exception {
    	mockMvc.perform(get("/api/user/12")
				.header(TokenAuthenticationService.HEADER_STRING, authTokenHeader)
    			.content(this.json(new User()))
    			.contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isNotFound());
    }

    @Test
    public void readSingleUser() throws Exception {
        mockMvc.perform(get("/api/user/" + this.userList.get(0).getId())
        		.header(TokenAuthenticationService.HEADER_STRING, authTokenHeader)
        		.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(this.userList.get(0).getName())))
                .andExpect(jsonPath("$.age", is(this.userList.get(0).getAge())));
    }

    @Test
    public void readPeople() throws Exception {
        mockMvc.perform(get("/api/user/")
        		.header(TokenAuthenticationService.HEADER_STRING, authTokenHeader)
        		.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(this.userList.get(0).getName())))
                .andExpect(jsonPath("$[0].age", is(this.userList.get(0).getAge())))
                .andExpect(jsonPath("$[1].name", is(this.userList.get(1).getName())))
                .andExpect(jsonPath("$[1].age", is(this.userList.get(1).getAge())));
    }

    @Test
    public void createUser() throws Exception {
        String userJson = json(new User("Marcos", 23, 30000, "password"));

        this.mockMvc.perform(post("/api/user/")
        		.header(TokenAuthenticationService.HEADER_STRING, authTokenHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());
    }

	protected String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }
}
