package it.nextre.corsojava.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.TokenDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/groups") 
public class GroupController extends Controller{

    public GroupController() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<GroupDTO> getAll(@HeaderParam("Authorization") String authHeader) {
	ObjectMapper objectMapper=new ObjectMapper();
	TokenDTO token = null;
	try {
		token = objectMapper.readValue(authHeader, TokenDTO.class);
		LOGGER.info("conversione header in tokenDto completata");
		
	} catch (JsonProcessingException e) {
		throw new RuntimeException("errore trasformando l' header : "+e.getMessage(),e);
	}
        return service.getAllGroup(token) ;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createGroup(GroupDTO groupDTO,TokenDTO tokenDTO) {
    	service.createGroup(groupDTO, tokenDTO);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateGroup(GroupDTO groupDTO,TokenDTO tokenDTO) {
    	service.updateGroup(groupDTO, tokenDTO);
    }
    
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteGroup(GroupDTO groupDTO,TokenDTO tokenDTO) {
    	service.deleteGroup(groupDTO, tokenDTO);
    }


}