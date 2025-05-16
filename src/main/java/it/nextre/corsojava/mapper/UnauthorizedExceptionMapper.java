package it.nextre.corsojava.mapper;

import it.nextre.corsojava.exception.UnauthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper  implements ExceptionMapper<UnauthorizedException>{

	@Override
	public Response toResponse(UnauthorizedException exception) {
		return Response.status(Response.Status.UNAUTHORIZED).entity(exception.getMessage()).build();

	}
	

}
