package it.nextre.corsojava.controller;

import java.util.List;

import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.pagination.PagedResult;
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
public class RoleController extends Controller{

    public RoleController() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<RoleDTO> getAll() {
        return service.getAllRole() ;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createRole(RoleDTO roleDTO) {
    	service.createRole(roleDTO);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRole(RoleDTO roleDTO) {
    	service.updateRole(roleDTO);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteRole(RoleDTO roleDTO) {
    	service.deleteRole(roleDTO);
    }
    
    @GET
    @Path("/paginated/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<RoleDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {
	
        return service.getAllRolesPag(page, size) ;
    }

   
}