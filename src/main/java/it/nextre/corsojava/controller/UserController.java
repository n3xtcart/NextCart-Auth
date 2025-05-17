package it.nextre.corsojava.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.dto.GroupDTO;
import it.nextre.corsojava.dto.LoginInfo;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import it.nextre.corsojava.service.ObjectService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users") 
public class UserController extends Controller{


   

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<UserDTO> getAll(@HeaderParam("Authorization") String authHeader) {
	ObjectMapper objectMapper=new ObjectMapper();
	TokenDTO token = null;
	try {
		token = objectMapper.readValue(authHeader, TokenDTO.class);
		LOGGER.debug("conversione header in tokenDto completata");
	}catch (JsonProcessingException e) {
		throw new RuntimeException("errore trasformando l' header : "+e.getMessage(),e);
	}
        return service.getAllUsers(token) ;
    }
    
    @POST
    @Path("/login")
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
    public TokenDTO login(LoginInfo info) {
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
        userDTO.setGroupDTO(new GroupDTO(ObjectService.getInstance().getGroupById(2L)));
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


}