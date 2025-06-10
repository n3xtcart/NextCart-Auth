package it.nextre.corsojava.controller;

import java.security.Principal;
import java.util.List;

import org.jboss.logging.Logger;

import io.quarkus.security.identity.SecurityIdentity;
import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.GroupService;
import it.nextre.corsojava.config.GroupServiceProducer;
import it.nextre.corsojava.entity.User;
import jakarta.inject.Inject;
import jakarta.resource.spi.work.SecurityContext;
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
	
	public GroupController(GroupServiceProducer serviceProducer) {
				this.service = serviceProducer.getService();
	}


	

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<GroupDTO> getAll() {
        return service.getAllGroups() ;
    }
   
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createGroup(GroupDTO groupDTO) {
    	Principal principal2 = securityContext.getPrincipal();
    	User principal = (User) securityContext.getAttributes().get("user");
    	System.out.println(securityContext.getPrincipal().getName());
    	service.create(groupDTO);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateGroup(GroupDTO groupDTO) {
    	service.update(groupDTO);
    }
    
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteGroup(GroupDTO groupDTO) {
    	service.delete(groupDTO);
    }

    
    @GET
    @Path("/paginated/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<GroupDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {
	
        return service.getAllGroupsPag(page, size) ;
    }

}