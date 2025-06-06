package it.nextre.corsojava.controller;

import java.util.List;

import org.jboss.logging.Logger;

import it.nextre.aut.dto.LoginInfo;
import it.nextre.aut.dto.TokenJwtDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.UserAdminService;
import it.nextre.corsojava.config.UserAdminServiceProducer;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/admin/users") 
public class UserAdminController {
	private static final Logger LOGGER = Logger.getLogger(UserAdminController.class);

	private final UserAdminService service;
	
	public UserAdminController(UserAdminServiceProducer serviceProducer) {
				this.service = serviceProducer.getService();
	}


	


   

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public List<UserDTO> getAll() {
	
        return service.getAllUsers() ;
    }
    
    @GET
    @Path("/paginated/{page}/{size}")
    @RolesAllowed({"admin"}) 
    @Produces(MediaType.APPLICATION_JSON) 
    public PagedResult<UserDTO> getAllPag(@PathParam("page") int page, @PathParam("size") int size) {
    	 return service.getAllUsersPag(page, size) ;
    }
    
    @POST
    @Path("/login")
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
    public TokenJwtDTO login(LoginInfo info) {
    	UserDTO userDTO=new UserDTO();
    	userDTO.setEmail(info.getEmail());
    	userDTO.setPassword(info.getPassword());
        return service.login(info);
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
    public MessageResponse confirmRegistration(@PathParam("token") String token) {
        service.confirmRegistration(token);
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
    	
    	service.update(userDTO);
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteGroup(UserDTO userDTO) {
    
    	service.delete(userDTO);
    }


}