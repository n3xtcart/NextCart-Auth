package it.nextre.corsojava.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.aut.dto.GroupDTO;
import it.nextre.corsojava.dao.jdbc.PagedResult;
import it.nextre.corsojava.entity.Token;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/groups") 
public class GroupController extends Controller{

    public GroupController() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<GroupDTO> getAll() {
        return service.getAllGroup() ;
    }
   
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createGroup(GroupDTO groupDTO) {
    	service.createGroup(groupDTO);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateGroup(GroupDTO groupDTO) {
    	service.updateGroup(groupDTO);
    }
    
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteGroup(GroupDTO groupDTO) {
    	service.deleteGroup(groupDTO);
    }

    
    @GET
    @Path("/paginated/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<GroupDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {
	
        return service.getAllGroupsPag(page, size) ;
    }

}