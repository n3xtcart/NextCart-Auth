package it.nextre.corsojava.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users") 
public class UserController extends Controller{


   

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<UserDTO> getAll(@HeaderParam("Authorization") String authHeader) {
	ObjectMapper objectMapper=new ObjectMapper();
	TokenDTO token = null;
	try {
		token = objectMapper.readValue(authHeader, TokenDTO.class);
		LOGGER.info("conversione header in tokenDto completata");
		
	} catch (JsonProcessingException e) {
		throw new RuntimeException("errore trasformando l' header : "+e.getMessage(),e);
	}
        return service.getAllUsers(token) ;
    }


}