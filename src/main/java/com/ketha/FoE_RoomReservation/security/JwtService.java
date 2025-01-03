package com.ketha.FoE_RoomReservation.security;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	private static final String SECRET_KEY = "7d0c9d8df98fb49de55aefe3846bd7a2314506216cee3febcd5a4d8992df67e8";
	private static final long VALIDITY = TimeUnit.MINUTES.toMillis(60 * 24);
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	// Extracting one single claim
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	// Generate a token only from user details
	public String generateToken(OAuth2User oAuth2User) {
		return generateToken(new HashMap<>(), oAuth2User);
	}
	
	// Generate a token out of extra claims and user details
	public String generateToken(
			Map<String, Object> extraClaims,
			OAuth2User oAuth2User
	) {
		return Jwts
				.builder()
				.setClaims(extraClaims)
				.setSubject(oAuth2User.getAttribute("name"))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + VALIDITY))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	// Method to validate token (Validate if the token belongs to the user details
	public boolean isTokenValid(String token, OAuth2User oAuth2User) {
		final String username = extractUsername(token);
		return (username.equals(oAuth2User.getAttribute("name"))) && !isTokenExpired(token);
	}
	
	private boolean isTokenExpired(String token) {
		return extractExpirationDate(token).before(new Date());
	}
	
	private Date extractExpirationDate(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	// Extracting all the claims
	public Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private SecretKey getSignInKey() {
		byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(decodedKey);
	}
}
