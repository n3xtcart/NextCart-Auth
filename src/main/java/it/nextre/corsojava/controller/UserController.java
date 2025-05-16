package it.nextre.corsojava.controller;

import java.util.List;

import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users") 
public class UserController extends Controller{


   

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    @Consumes(MediaType.APPLICATION_JSON)
    public List<UserDTO> getAllUsers(TokenDTO token) {
        return service.getAllUsers(token);
    }


}