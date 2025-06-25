package it.nextre.corsojava.controller;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.aut.dto.LoginInfo;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.service.UserService;
import it.nextre.corsojava.config.UserServiceProducer;
import it.nextre.corsojava.exception.ControllerException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users") 
public class UserController {
	private static final Logger LOGGER = Logger.getLogger(UserController.class);
	@Inject
	JsonWebToken jwt;
	@Inject
	ObjectMapper objectMapper;

	private final UserService service;
	
	public UserController(UserServiceProducer serviceProducer) {
				this.service = serviceProducer.getService();
	}


	


   


    
    @POST
    @Path("/login")
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
    public TokenJwtDTO login(LoginInfo info) {
    	UserDTO userDTO=new UserDTO();
    	userDTO.setEmail(info.getEmail());
    	userDTO.setPassword(info.getPassword());
        return service.login(info);
    }

    public static class MessageResponse {
        public String message;
        public MessageResponse(String message) {
            this.message = message;
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public MessageResponse register(UserDTO userDTO) {
        service.register(userDTO);
        return new MessageResponse("Mail inviata");
    }
    
    @GET
    @Path("/confirmRegistration/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MessageResponse confirmRegistration(@PathParam("token") String token) {
    	LOGGER.info("conferma registrazione ");
		
        service.confirmRegistration(token);
        return new MessageResponse("registrazione confermata con successo");
    }
    
    
   
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUser(UserDTO userDTO) {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
		LOGGER.info("richiesta di aggiornamento del prorpio utente da : "+ userObject.getEmail());
		
    	
    	service.update(userDTO,userObject);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteUser(UserDTO userDTO) {
    	UserDTO userObject = null;
	try {
		userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
	} catch (JsonMappingException e) {
		throw new ControllerException("Error mapping user from JWT", e);
	} catch (JsonProcessingException e) {
		throw new ControllerException("Error processing user from JWT", e);
	}
	LOGGER.info("richiesta di eliminazione del prorpio utente da : "+ userObject.getEmail());
	
    	service.delete(userDTO,userObject);
    }


}