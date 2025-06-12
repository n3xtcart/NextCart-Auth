package it.nextre.corsojava.controller;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.aut.dto.LoginInfo;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.UserAdminService;
import it.nextre.corsojava.config.UserAdminServiceProducer;
import it.nextre.corsojava.exception.ControllerException;
import jakarta.annotation.security.RolesAllowed;
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

@Path("/admin/users") 
public class UserAdminController {
	private static final Logger LOGGER = Logger.getLogger(UserAdminController.class);

	private final UserAdminService service;
	
	
	@Inject
	JsonWebToken jwt;
	@Inject
	ObjectMapper objectMapper;
	
	public UserAdminController(UserAdminServiceProducer serviceProducer) {
				this.service = serviceProducer.getService();
	}


	


   

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<UserDTO> getAll() {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
	
        return service.getAllUsers(userObject) ;
    }
    
    @GET
    @Path("/paginated/{page}/{size}")
    @RolesAllowed({"admin"}) 
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<UserDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
    	 return service.getAllUsersPag(page, size,userObject) ;
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

    public class MessageResponse {
        public String message;
        public MessageResponse(String message) {
            this.message = message;
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MessageResponse register(UserDTO userDTO) {
        service.register(userDTO);
        return new MessageResponse("Mail inviata");
    }
    
    @GET
    @Path("/confirmRegistration/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MessageResponse confirmRegistration(@PathParam("token") String token) {
        service.confirmRegistration(token);
        return new MessageResponse("Mail inviata");
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createUser(UserDTO userDTO) {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
    	service.createUser(userDTO,userObject);
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
    	
    	service.update(userDTO,userObject);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteGroup(UserDTO userDTO) {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
    
    	service.delete(userDTO,userObject);
    }


}