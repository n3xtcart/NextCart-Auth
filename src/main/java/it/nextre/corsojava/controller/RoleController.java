package it.nextre.corsojava.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.RoleDTO;
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

@Path("/roles") 
public class RoleController extends Controller{

    public RoleController() {

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<RoleDTO> getAll(@HeaderParam("Authorization") String authHeader) {
	ObjectMapper objectMapper=new ObjectMapper();
	TokenDTO token = null;
	try {
		token = objectMapper.readValue(authHeader, TokenDTO.class);
		LOGGER.info("conversione header in tokenDto completata");
		
	} catch (JsonProcessingException e) {
		throw new RuntimeException("errore trasformando l' header : "+e.getMessage(),e);
	}
        return service.getAllRole(token) ;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createRole(RoleDTO roleDTO,@HeaderParam("Authorization") String authHeader) {
    	TokenDTO token = new TokenDTO();
    	token.setToken(authHeader);
    
    	service.createRole(roleDTO, token);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRole(RoleDTO roleDTO,@HeaderParam("Authorization") String authHeader) {
    	TokenDTO token = new TokenDTO();
    	token.setToken(authHeader);
    
    	service.updateRole(roleDTO, token);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteRole(RoleDTO roleDTO,@HeaderParam("Authorization") String authHeader) {
    	TokenDTO token = new TokenDTO();
    	token.setToken(authHeader);
    	service.deleteRole(roleDTO, token);
    }

   
}