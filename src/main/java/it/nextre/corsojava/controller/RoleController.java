package it.nextre.corsojava.controller;
import java.util.ArrayList;
import java.util.List;

import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
import it.nextre.corsojava.entity.Role;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/roles") // ✅ Definisce il percorso dell'API
public class RoleController {

    public RoleController() {
        
    }

    @GET
    
@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
@Consumes(MediaType.APPLICATION_JSON)
    public List<Role> getAllRoles() {
        return RoleJdbcDao.getInstance().getAll(); // ✅ Restituisce tutti gli utenti
    }

//    @GET
//    @Path("/{id}")
//@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
//@Consumes(MediaType.APPLICATION_JSON)
//    public Role getRoleById(@PathParam("id") int id) {
//        return users.stream()
//                .filter(user -> user.getId() == id)
//                .findFirst()
//                .orElseThrow(() -> new NotFoundException("Role not found"));
//    }

    @POST
@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
@Consumes(MediaType.APPLICATION_JSON)
    public Role createRole(Role user) {
      //  users.add(user);
        return user; // ✅ Aggiunge un nuovo utente
    }

    @DELETE
    @Path("/{id}")
@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
@Consumes(MediaType.APPLICATION_JSON)
    public void deleteRole(@PathParam("id") int id) {
       // users.removeIf(user -> user.getId() == id); // ✅ Elimina un utente
    }
}