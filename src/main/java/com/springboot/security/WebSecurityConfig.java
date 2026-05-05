package com.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private CustomUserService userService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers("/").permitAll()
				.requestMatchers(HttpMethod.POST, "/login").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JWTLoginFilter("/login", authenticationManager(authConfig)), 
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JWTAuthenticationFilter(), 
				UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		List<String> METHODS = Arrays.asList("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH");
		List<String> ORIGINS = Arrays.asList("*", "http://127.0.0.1:4200","http://localhost:4200", "http://localhost:8080");
		List<String> HEADERS = Arrays.asList("Authorization", "authorization",
				"Origin", "X-Requested-With", "Content-Type", "Accept", "X-XSRF-TOKEN", "credential");

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOriginPatterns(ORIGINS);
		configuration.setAllowedMethods(METHODS);
		configuration.setMaxAge(3600L);
		configuration.setAllowedHeaders(HEADERS);
		configuration.setExposedHeaders(HEADERS);
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
