package it.nextre.corsojava.utils;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.smallrye.jwt.build.Jwt;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.corsojava.entity.Role;

public class JwtGenerator {
	private static final Logger log = Logger.getLogger(JwtGenerator.class);

	
	public  static TokenJwtDTO generateTokens(String email,Set<Role> roles) {	
		log.info("Creating JWT tokens for user: " + email);
		
	    String accessToken = Jwt.subject(email)
	                          .groups( roles.stream()
	                                               .map(Role::getDescrizione).collect(Collectors.toSet()))
	                          .claim("token_type", "access")
	                          .expiresIn(Duration.ofMinutes(10))
	                          .sign();

	    String refreshToken = Jwt.subject(email)
	                           .claim("token_type", "refresh")
	                           .expiresIn(Duration.ofDays(1)).sign();
	    return new TokenJwtDTO(accessToken, refreshToken);
	}

}
