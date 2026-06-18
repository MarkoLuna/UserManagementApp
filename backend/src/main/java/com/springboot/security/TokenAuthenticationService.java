package com.springboot.security;

import com.springboot.controller.RestApiController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import javax.crypto.SecretKey;

import static java.util.Collections.emptyList;

public class TokenAuthenticationService {

	static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	static final long EXPIRATIONTIME = 864_000_000; // 10 days
	static final String SECRET = "ThisIsASecretKeyThatIsLongEnoughForJWTSecurityRequirements256Bits";
	static final String TOKEN_PREFIX = "Bearer";
	public static final String HEADER_STRING = "Authorization";
	
	private static final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

	static void addAuthentication(HttpServletResponse res, String username) {
		
		String jwt = Jwts.builder()
				.subject(username)
				.expiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(key)
				.compact();
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
	}

	static String generateToken(String username) {
		return Jwts.builder()
				.subject(username)
				.expiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(key)
				.compact();
	}

	static Authentication getAuthentication(HttpServletRequest request) throws AuthenticationServiceException{
		String token = request.getHeader(HEADER_STRING);
		if (token != null && !token.isEmpty()) {
			// parse the token.
			logger.info("try with token: {}", token );
			String user = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token.replace(TOKEN_PREFIX + " ", ""))
					.getPayload()
					.getSubject();

			Date expirationDate = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token.replace(TOKEN_PREFIX + " ", ""))
					.getPayload()
					.getExpiration();
			
			if(StringUtils.isEmpty(user)){
				return null;
			}
			if(isExpirate(expirationDate)){
				throw new AuthenticationServiceException("Token has expirated");
			}

			return new UsernamePasswordAuthenticationToken(user, null, emptyList());
		}
		return null;
	}
	private static boolean isExpirate(Date expirationDate){
		return expirationDate.before(new Date(System.currentTimeMillis()) );
	}
}
