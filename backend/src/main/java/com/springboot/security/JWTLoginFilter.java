package com.springboot.security;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	public JWTLoginFilter(String url, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException, ServletException {

		try{
			AccountCredentials creds = new ObjectMapper().readValue(req.getInputStream(), AccountCredentials.class);
			
			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(),
					creds.getPassword(), Collections.emptyList()));
		}catch(JsonMappingException e){
			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken("",
					"", Collections.emptyList()));
		}
		
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		
		// Generate JWT token and add it as response header
		TokenAuthenticationService.addAuthentication(res, auth.getName());
		
		// Get the generated token from the response header
		String token = res.getHeader(TokenAuthenticationService.HEADER_STRING);
		
		// Set response content type
		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		// Create and write response body with token
		LoginResponse loginResponse = new LoginResponse(token);
		ObjectMapper mapper = new ObjectMapper();
		res.getWriter().write(mapper.writeValueAsString(loginResponse));
	}
}
