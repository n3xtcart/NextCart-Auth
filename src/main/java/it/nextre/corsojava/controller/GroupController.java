package it.nextre.corsojava.controller;
import java.util.ArrayList;
import java.util.List;

import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.entity.Group;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/groups") // ✅ Definisce il percorso dell'API
public class GroupController {

    public GroupController() {
        
    }

    @GET
    
@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
@Consumes(MediaType.APPLICATION_JSON)
    public List<Group> getAllGroups() {
        return GroupJdbcDao.getInstance().getAll(); // ✅ Restituisce tutti gli utenti
    }

//    @GET
//    @Path("/{id}")
//@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
//@Consumes(MediaType.APPLICATION_JSON)
//    public Group getGroupById(@PathParam("id") int id) {
//        return users.stream()
//                .filter(user -> user.getId() == id)
//                .findFirst()
//                .orElseThrow(() -> new NotFoundException("Group not found"));
//    }

    @POST
@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
@Consumes(MediaType.APPLICATION_JSON)
    public Group createGroup(Group user) {
      //  users.add(user);
        return user; // ✅ Aggiunge un nuovo utente
    }

    @DELETE
    @Path("/{id}")
@Produces(MediaType.APPLICATION_JSON) // ✅ Risponde con JSON
@Consumes(MediaType.APPLICATION_JSON)
    public void deleteGroup(@PathParam("id") int id) {
       // users.removeIf(user -> user.getId() == id); // ✅ Elimina un utente
    }
}