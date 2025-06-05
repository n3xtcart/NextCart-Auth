package it.nextre.corsojava.controller;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.aut.service.UserService;
import it.nextre.corsojava.config.UserServiceProducer;
import it.nextre.corsojava.entity.Token;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/tokens") 
public class TokenController {
	
	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	private final UserService service;
	
	public TokenController(UserServiceProducer serviceProducer) {
				this.service = serviceProducer.getService();
	}









}