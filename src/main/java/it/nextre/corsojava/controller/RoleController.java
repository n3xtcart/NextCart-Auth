package it.nextre.corsojava.controller;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.RoleService;
import it.nextre.corsojava.config.RoleServiceProducer;
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

@Path("/roles") 

@RolesAllowed("admin")
public class RoleController  {
	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	private final RoleService service;
	@Inject
	JsonWebToken jwt;
	@Inject
	ObjectMapper objectMapper;
	
	public RoleController(RoleServiceProducer serviceProducer) {
				this.service = serviceProducer.getService();
	}


	

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<RoleDTO> getAll() {
    	UserDTO userObject = null;
	try {
		userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
	} catch (JsonMappingException e) {
		throw new ControllerException("Error mapping user from JWT", e);
	} catch (JsonProcessingException e) {
		throw new ControllerException("Error processing user from JWT", e);
	}
	LOGGER.info("richiesta di tutti i ruoli da : "+ userObject.getEmail());
	
        return service.getAllRoles(userObject) ;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createRole(RoleDTO roleDTO) {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
		LOGGER.info("richiesta di creazione ruolo da : "+ userObject.getEmail());
		
    	service.create(roleDTO,userObject);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRole(RoleDTO roleDTO) {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
		LOGGER.info("richiesta di modifica ruolo da : "+ userObject.getEmail());
		
    	service.update(roleDTO,userObject);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteRole(RoleDTO roleDTO) {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
		LOGGER.info("richiesta di eliminazione ruolo da : "+ userObject.getEmail());
		
    	service.delete(roleDTO,userObject);
    }
    
    @GET
    @Path("/paginated/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<RoleDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {
	
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
		LOGGER.info("richiesta di tutti i ruoli paginati da : "+ userObject.getEmail());
		
        return service.getAllRolesPag(page, size,userObject) ;
    }
    
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RoleDTO getById(@PathParam("id")long id) {UserDTO userObject = null;
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