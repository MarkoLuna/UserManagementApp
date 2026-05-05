package com.springboot.controller;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.springboot.SpringBootRestApiApp;
import com.springboot.model.User;
import com.springboot.repositories.UserRepository;
import com.springboot.security.AccountCredentials;
import com.springboot.security.TokenAuthenticationService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringBootRestApiApp.class) 
@AutoConfigureMockMvc
public class RestApiControllerTest {
	
	private MediaType CONTENT_TYPE_JSON = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    
    @Autowired
    private MockMvc mockMvc;
    
    private String authTokenHeader = "";

    @SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private List<User> userList = new ArrayList<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull(this.mappingJackson2HttpMessageConverter,
                "the JSON message converter must not be null");
    }

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
    	
    	MediaType CONTENT_TYPE_TEXT = new MediaType(MediaType.TEXT_PLAIN.getType(),
                MediaType.TEXT_PLAIN.getSubtype(),
                Charset.forName("utf8"));
    	
    	MvcResult mvcResult = mockMvc.perform(post("/login")
    			.content(this.json(accountCredentials))
    			.contentType(CONTENT_TYPE_TEXT))
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
    			.contentType(CONTENT_TYPE_JSON))
    	.andExpect(status().isNotFound());
    }

    @Test
    public void readSingleUser() throws Exception {
        mockMvc.perform(get("/api/user/" + this.userList.get(0).getId())
        		.header(TokenAuthenticationService.HEADER_STRING, authTokenHeader)
        		.contentType(CONTENT_TYPE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(this.userList.get(0).getName())))
                .andExpect(jsonPath("$.age", is(this.userList.get(0).getAge())));
    }

    @Test
    public void readPeople() throws Exception {
        mockMvc.perform(get("/api/user/")
        		.header(TokenAuthenticationService.HEADER_STRING, authTokenHeader)
        		.contentType(CONTENT_TYPE_JSON))
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
                .contentType(CONTENT_TYPE_JSON)
                .content(userJson))
                .andExpect(status().isCreated());
    }

    @SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
