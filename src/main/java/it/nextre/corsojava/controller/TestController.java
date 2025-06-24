package it.nextre.corsojava.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class TestController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String test() {
        return "{\"test\":\"ok\"}";
    }
}
