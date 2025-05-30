package it.nextre.corsojava.dto;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.smallrye.jwt.build.Jwt;
import it.nextre.corsojava.entity.Role;

public class TokensJwt {
	private String accessToken;
	private String refreshToken;
	private Logger log= Logger.getLogger(TokensJwt.class);
	

	public TokensJwt(String email, Set<Role> roles) {
		log.info("Creating JWT tokens for user: " + email);
	   for(Role role : roles) {
        	log.info("Role: " + role.getDescrizione() + " - ID: " + role.getId());
}
		
	    this.accessToken = Jwt.subject(email)
	                          .claim("roles", roles.stream()
	                                               .map(Role::getDescrizione).collect(Collectors.toSet()))
	                          .claim("token_type", "access")
	                          .expiresIn(Duration.ofMinutes(10))
	                          .sign();

	    this.refreshToken = Jwt.subject(email)
	                           .claim("token_type", "refresh")
	                           .expiresIn(Duration.ofDays(1)).sign();
	}




	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
