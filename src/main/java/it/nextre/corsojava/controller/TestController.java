package it.nextre.corsojava.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/test")
public class TestController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() {
        return Response.ok("succes").build();
    }
}
