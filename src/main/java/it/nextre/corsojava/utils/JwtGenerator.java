package it.nextre.corsojava.utils;

import java.time.Duration;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtSignatureException;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped

public class JwtGenerator {
	private static final Logger log = Logger.getLogger(JwtGenerator.class);
	private ObjectMapper objectMapper;
	
	
	public JwtGenerator(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	
	public TokenJwtDTO generateTokens(User user) {	
		log.info("Creating JWT tokens for user: " + user.getEmail());

		
	    String accessToken = null;
		try {
			accessToken = Jwt.subject(user.getEmail())
			                      .groups( user.getRoles().stream()
			                                           .map(Role::getDescrizione).collect(Collectors.toSet()))
			                      .claim("token_type", "access")
			                      .claim("user", objectMapper.writeValueAsString(user))
			                      .expiresIn(Duration.ofMinutes(10))
			                      .sign();
		} catch (JwtSignatureException | JsonProcessingException e) {
			log.error("Error generating JWT token for user: " + user.getEmail(), e);
			throw new RuntimeException("Error generating JWT token", e);
		}

	  

	    String refreshToken = Jwt.subject(user.getEmail())
	                           .claim("token_type", "refresh")
	                           .expiresIn(Duration.ofDays(1)).sign();
	    return new TokenJwtDTO(accessToken, refreshToken);
	}

}
