package com.usermanagement.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.usermanagement.controller.RestApiController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.Instant;
import java.time.Period;
import java.util.Date;

import static java.util.Collections.emptyList;

public class TokenAuthenticationService {

	static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	static final Period EXPIRATION_PERIOD = Period.ofDays(10);
	static final String SECRET = "ThisIsASecretKeyThatIsLongEnoughForJWTSecurityRequirements256Bits";
	static final String TOKEN_PREFIX = "Bearer";
	public static final String HEADER_STRING = "Authorization";

	static void addAuthentication(HttpServletResponse res, String username) {
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + generateToken(username));
	}

	static String generateToken(String username) {
		try {
			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
					.subject(username)
					.expirationTime(Date.from(Instant.now().plus(EXPIRATION_PERIOD)))
					.build();

			SignedJWT signedJWT = new SignedJWT(
					new JWSHeader(JWSAlgorithm.HS256),
					claimsSet
			);
			signedJWT.sign(new MACSigner(SECRET.getBytes()));
			return signedJWT.serialize();
		} catch (JOSEException e) {
			throw new AuthenticationServiceException("Failed to generate JWT token", e);
		}
	}

	static Authentication getAuthentication(HttpServletRequest request) throws AuthenticationServiceException {
		String token = request.getHeader(HEADER_STRING);
		if (token != null && !token.isEmpty()) {
			logger.info("try with token: {}", token);
			try {
				SignedJWT signedJWT = SignedJWT.parse(token.replace(TOKEN_PREFIX + " ", ""));

				if (!signedJWT.verify(new MACVerifier(SECRET.getBytes()))) {
					throw new AuthenticationServiceException("Invalid JWT signature");
				}

				JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
				String user = claims.getSubject();
				Date expirationDate = claims.getExpirationTime();

				if (user == null || user.isEmpty()) {
					throw new AuthenticationServiceException("Invalid JWT token - no subject");
				}

				if (isExpired(expirationDate)) {
					throw new AuthenticationServiceException("Token has expired");
				}

				return new UsernamePasswordAuthenticationToken(user, null, emptyList());
			} catch (ParseException | JOSEException e) {
				throw new AuthenticationServiceException("Invalid JWT token", e);
			}
		}
		return null;
	}

	private static boolean isExpired(Date expirationDate) {
		return expirationDate.before(new Date());
	}
}
