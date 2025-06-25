package it.nextre.corsojava.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.service.UserService;
import it.nextre.corsojava.config.UserServiceProducer;
import it.nextre.corsojava.exception.ControllerException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/tokens") 
public class TokenController {
	
	private static final Logger LOGGER = Logger.getLogger(TokenController.class);

	private final UserService service;
	
	
	@Inject
	JsonWebToken jwt;
	@Inject
	ObjectMapper objectMapper;
	
	public TokenController(UserServiceProducer serviceProducer) {
				this.service = serviceProducer.getService();
	}
	
    @POST
    @Path("/refresh")
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
    public TokenJwtDTO refresh() {
    	UserDTO userObject = null;
	try {
		userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
	} catch (JsonMappingException e) {
		throw new ControllerException("Error mapping user from JWT", e);
	} catch (JsonProcessingException e) {
		throw new ControllerException("Error processing user from JWT", e);
	}
        return service.refreshToken(userObject);
    }
    
    @POST
    @Path("/verify")
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
    public TokenJwtDTO verify( String tokenString) {
  
        return service.confirmRegistration(tokenString);
    }









}