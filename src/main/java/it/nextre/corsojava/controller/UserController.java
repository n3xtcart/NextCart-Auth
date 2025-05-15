package it.nextre.corsojava.controller;
import java.util.ArrayList;
import java.util.List;

import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.dto.TokenDTO;
import it.nextre.corsojava.dto.UserDTO;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.service.UserService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users") // ✅ Definisce il percorso dell'API
public class UserController {

  
    public UserController() {
        
    }

    @GET
    
@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
@Consumes(MediaType.APPLICATION_JSON)
    public List<UserDTO> getAllUsers(TokenDTO token) {
        return UserService.getInstance().getAllUsers(token); // ✅ Restituisce tutti gli utenti
    }

//    @GET@P
//    @Path("/{id}")
//@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
//@Consumes(MediaType.APPLICATION_JSON)
//    public User getUserById(@PathParam("id") int id) {
//        return users.stream()
//                .filter(user -> user.getId() == id)
//                .findFirst()
//                .orElseThrow(() -> new NotFoundException("User not found"));
//    }

//    @POST
//@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
//@Consumes(MediaType.APPLICATION_JSON)
//    public User createUser(User user) {
//        users.add(user);
//        return user; // ✅ Aggiunge un nuovo utente
//    }

//    @DELETE
//    @Path("/{id}")
//@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
//@Consumes(MediaType.APPLICATION_JSON)
//    public void deleteUser(@PathParam("id") int id) {
//        users.removeIf(user -> user.getId() == id); // ✅ Elimina un utente
//    }
}