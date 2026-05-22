package com.devdad.book_worms.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${application.security.jwt.expiration}")
	private Long jwtExpiration;

	@Value("${application.security.jwt.secret-key}")
	private String secretKey;

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	/**
	 * Generates a Jwt token
	 * 
	 * @param claims      - Optional data to pass in to get generated along the
	 *                    users details.
	 * @param userDetails - The users details
	 * @return Generated Jwt Token.
	 */
	public String 
		generateToken(Map<String, Object> claims, UserDetails userDetails) {
		return buildToken(claims, userDetails, jwtExpiration);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String extractUsername(String token) {
		// We set the UserDetails.getUsername as the subject, so this returns it.
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.verifyWith((SecretKey) getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private boolean isTokenExpired(String token){
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token){
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Builds A JWT Token
	 * 
	 * @param additionalClaims
	 * @param userDetails
	 * @param jwtExpiration
	 * @return Builds Jwt Token with the given parameters, Additional Claims, Users
	 *         Unique Email, Jwt Expiration Time.
	 */
	private String buildToken(
			Map<String, Object> additionalClaims,
			UserDetails userDetails,
			Long jwtExpiration) {
		var authorities = userDetails.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.toList();

		return Jwts
				.builder()
				// Users Unique Identifier, in this case we chose the email
				.subject(userDetails.getUsername())
				.claim("authorities", authorities)
				.claims(additionalClaims)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(getSigningKey())
				.compact();
	}

	/**
	 * @return Signing Key - Using Base64 Decoding of a Secret Key.
	 */
	private Key getSigningKey() {
		byte[] keyBytes = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
