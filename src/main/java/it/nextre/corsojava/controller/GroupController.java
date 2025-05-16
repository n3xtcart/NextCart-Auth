package it.nextre.corsojava.controller;

import java.util.List;

import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.TokenDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/groups") 
public class GroupController extends Controller{

    public GroupController() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON) // 
    @Consumes(MediaType.APPLICATION_JSON)
    public List<GroupDTO> getAllGroups(TokenDTO token) {
        return service.getAllGroup(token); 
        }




}