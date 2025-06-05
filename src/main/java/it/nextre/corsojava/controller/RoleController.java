package it.nextre.corsojava.controller;

import java.util.List;

import org.jboss.logging.Logger;

import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.RoleService;
import it.nextre.corsojava.config.RoleServiceProducer;
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
public class RoleController  {
	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	private final RoleService service;
	
	public RoleController(RoleServiceProducer serviceProducer) {
				this.service = serviceProducer.getService();
	}


	

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<RoleDTO> getAll() {
        return service.getAllRoles() ;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createRole(RoleDTO roleDTO) {
    	service.create(roleDTO);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRole(RoleDTO roleDTO) {
    	service.update(roleDTO);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteRole(RoleDTO roleDTO) {
    	service.delete(roleDTO);
    }
    
    @GET
    @Path("/paginated/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<RoleDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {
	
        return service.getAllRolesPag(page, size) ;
    }

   
}