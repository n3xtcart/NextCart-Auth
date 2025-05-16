package it.nextre.corsojava.controller;
import it.nextre.corsojava.dto.LoginInfo;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/login") // ✅ Definisce il percorso dell'API
public class LoginController extends Controller{

  
  





    @POST
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON)
    public TokenDTO login(LoginInfo info) {
    	UserDTO userDTO=new UserDTO();
    	userDTO.setEmail(info.getEmail());
    	userDTO.setPassword(info.getPassword());
        return service.login(userDTO);
    }


}