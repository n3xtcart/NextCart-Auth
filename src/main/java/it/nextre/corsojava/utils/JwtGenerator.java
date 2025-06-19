package it.nextre.corsojava.utils;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtSignatureException;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped

public class JwtGenerator {
	private static final Logger log = Logger.getLogger(JwtGenerator.class);
	private ObjectMapper objectMapper;
	
	
	public JwtGenerator(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	
	public TokenJwtDTO generateTokens(UserDTO user) {	
		user.setPassword(null); // Clear password for security reasons
		log.info("Creating JWT tokens for user: " + user.getEmail());
		
		
	    String accessToken = null;
		String refreshToken;
		try {
			String userString=objectMapper.writeValueAsString(user);
			Set<String > roles= user.getRuoli().stream()
                    .map(RoleDTO::getDescrizione).collect(Collectors.toSet());
                    roles.addAll(user.getGroupDTO().getRoleDTO().stream().map(RoleDTO::getDescrizione).collect(Collectors.toSet()));
			accessToken = Jwt.subject(user.getEmail())
			                      .groups(roles)
			                      .claim("token_type", "access")
			                      .claim("user",userString )
			                      .expiresIn(Duration.ofMinutes(10))
			                      .sign();
			refreshToken = Jwt.subject(user.getEmail())
					.claim("token_type", "refresh")
					.claim("user", userString)
					.expiresIn(Duration.ofDays(1)).sign();
			
		} catch (JwtSignatureException | JsonProcessingException e) {
			log.error("Error generating JWT token for user: " + user.getEmail(), e);
			throw new RuntimeException("Error generating JWT token", e);
		}

	    return new TokenJwtDTO(accessToken, refreshToken);
	}

}
