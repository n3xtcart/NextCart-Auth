package it.nextre.corsojava.controller;
import java.util.ArrayList;
import java.util.List;

import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.dto.LoginInfo;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.service.UserService;
import it.nextre.corsojava.service.UserServiceJdbc;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/login") // ✅ Definisce il percorso dell'API
public class LoginController {

  
    public LoginController() {
        
    }



//    @GET@P
//@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
//@Consumes(MediaType.APPLICATION_JSON)
//    public User getUserById(@PathParam("id") int id) {
//        return users.stream()
//                .filter(user -> user.getId() == id)
//                .findFirst()
//                .orElseThrow(() -> new NotFoundException("User not found"));
//    }

    @POST
@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
@Consumes(MediaType.APPLICATION_JSON)
    public TokenDTO login(LoginInfo info) {
    	UserDTO userDTO=new UserDTO();
    	userDTO.setEmail(info.getEmail());
    	userDTO.setPassword(info.getPassword());
        return UserServiceJdbc.getInstance().login(userDTO);
    }

//    @DELETE
//    @Path("/{id}")
//@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
//@Consumes(MediaType.APPLICATION_JSON)
//    public void deleteUser(@PathParam("id") int id) {
//        users.removeIf(user -> user.getId() == id); // ✅ Elimina un utente
//    }
}