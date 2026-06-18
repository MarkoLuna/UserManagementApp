package com.springboot.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {

		try{
			Authentication authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) req);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}catch (AuthenticationServiceException e){
			SecurityContextHolder.getContext().setAuthentication(null);
			ObjectMapper mapper = new ObjectMapper();
			
			((HttpServletResponse )res).setStatus(HttpStatus.UNAUTHORIZED.value());
			((HttpServletResponse )res).setContentType(MediaType.APPLICATION_JSON_VALUE);
			Map<String, Object> tokenMap = new HashMap<>();
	        tokenMap.put("message",  e.getMessage());
	        tokenMap.put("status", HttpStatus.UNAUTHORIZED);
			
			mapper.writeValue(res.getWriter(), tokenMap);
		}
		filterChain.doFilter(req, res);
	}
}
