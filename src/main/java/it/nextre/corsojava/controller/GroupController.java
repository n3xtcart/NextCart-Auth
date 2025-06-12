package it.nextre.corsojava.controller;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.security.identity.SecurityIdentity;
import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.GroupService;
import it.nextre.corsojava.config.GroupServiceProducer;
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

@Path("/groups") 
public class GroupController {
	private static final Logger LOGGER = Logger.getLogger(GroupController.class);

	private final GroupService service;
	@Inject
	SecurityIdentity securityContext;
	
	@Inject
	JsonWebToken jwt;
	ObjectMapper objectMapper;
	
	public GroupController(GroupServiceProducer serviceProducer,ObjectMapper objectMapper) {
				this.service = serviceProducer.getService();
				this.objectMapper = objectMapper;
	}


	

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<GroupDTO> getAll() {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}
        return service.getAllGroups(userObject) ;
    }
   
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createGroup(GroupDTO groupDTO) {
    	UserDTO userObject = null;
		try {
			userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
		} catch (JsonMappingException e) {
			throw new ControllerException("Error mapping user from JWT", e);
		} catch (JsonProcessingException e) {
			throw new ControllerException("Error processing user from JWT", e);
		}

    	
    	service.create(groupDTO,userObject);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateGroup(GroupDTO groupDTO) {UserDTO userObject = null;
	try {
		userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
	} catch (JsonMappingException e) {
		throw new ControllerException("Error mapping user from JWT", e);
	} catch (JsonProcessingException e) {
		throw new ControllerException("Error processing user from JWT", e);
	}
    	service.update(groupDTO, userObject);
    }
    
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteGroup(GroupDTO groupDTO) {UserDTO userObject = null;
	try {
		userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
	} catch (JsonMappingException e) {
		throw new ControllerException("Error mapping user from JWT", e);
	} catch (JsonProcessingException e) {
		throw new ControllerException("Error processing user from JWT", e);
	}
    	service.delete(groupDTO, userObject);
    }

    
    @GET
    @Path("/paginated/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<GroupDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {UserDTO userObject = null;
	try {
		userObject = objectMapper.readValue(jwt.getClaim("user").toString(), UserDTO.class);
	} catch (JsonMappingException e) {
		throw new ControllerException("Error mapping user from JWT", e);
	} catch (JsonProcessingException e) {
		throw new ControllerException("Error processing user from JWT", e);
	}
	
        return service.getAllGroupsPag(page, size, userObject); 
    }

}