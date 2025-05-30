package it.nextre.corsojava.controller;

import java.util.List;

import it.nextre.corsojava.dao.jdbc.PagedResult;
import it.nextre.corsojava.dto.LoginInfo;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.TokensJwt;
import it.nextre.corsojava.dto.UserDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users") 
public class UserController extends Controller{


   

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<UserDTO> getAll() {
	
        return service.getAllUsers() ;
    }
    
    @GET
    @Path("/paginated/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<UserDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {

        return service.getAllUsersPag(page, size) ;
    }
    
    @POST
    @Path("/login")
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
    public TokensJwt login(LoginInfo info) {
    	UserDTO userDTO=new UserDTO();
    	userDTO.setEmail(info.getEmail());
    	userDTO.setPassword(info.getPassword());
        return service.login(userDTO);
    }

    public class MessageResponse {
        public String message;
        public MessageResponse(String message) {
            this.message = message;
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MessageResponse register(UserDTO userDTO) {
        service.register(userDTO);
        return new MessageResponse("Mail inviata");
    }
    
    @GET
    @Path("/confirmRegistration/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MessageResponse confirmRegistration(@PathParam("token") String Token) {
        TokenDTO tokenDTO=service.findTokenByValue(Token);
        service.confirmRegistration(tokenDTO);
        return new MessageResponse("Mail inviata");
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createUser(UserDTO userDTO) {
    
    	service.createUser(userDTO);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUser(UserDTO userDTO) {
    	
    	service.updateUser(userDTO);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteGroup(UserDTO userDTO) {
    
    	service.deleteUser(userDTO);
    }


}