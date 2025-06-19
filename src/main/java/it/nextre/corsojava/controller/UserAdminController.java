package it.nextre.corsojava.controller;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.UserAdminService;
import it.nextre.corsojava.config.UserAdminServiceProducer;
import it.nextre.corsojava.exception.ControllerException;
import it.nextre.corsojava.exception.UserMissingException;
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

@RolesAllowed("admin")
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
		LOGGER.info("richiesta di tutti gli utenti da : "+ userObject.getEmail());
		
	
        return service.getAllUsers(userObject) ;
    }
    
    @GET
    @Path("/paginated/{page}/{size}")
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
		LOGGER.info("richiesta di tutti gli utenti paginati da : "+ userObject.getEmail());
		
    	 return service.getAllUsersPag(page, size,userObject) ;
    }
    
	

    public class MessageResponse {
        public String message;
        public MessageResponse(String message) {
            this.message = message;
        }
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
		LOGGER.info("richiesta di tutti gli utenti da : "+ userObject.getEmail());
		
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
		LOGGER.info("richiesta di creazione utente da : "+ userObject.getEmail());
		
    	
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
		}LOGGER.info("richiesta di eliminazione utente da : "+ userObject.getEmail());
		
    
    	service.delete(userDTO,userObject);
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getById(@PathParam("id")long id) {UserDTO userObject = null;
	try {
		userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
	} catch (JsonMappingException e) {
		throw new ControllerException("Error mapping user from JWT", e);
	} catch (JsonProcessingException e) {
		throw new ControllerException("Error processing user from JWT", e);
	}LOGGER.info("richiesta di recupero utente con id : "+id+" da : "+ userObject.getEmail());
	return service.findById(id, userObject).orElseThrow(()->new UserMissingException("utente non trovato"));
    	
    }


}