package it.nextre.corsojava.service.UserAdminService;

import java.util.List;
import java.util.Optional;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.UserAdminService;
import it.nextre.corsojava.service.UserService.UserServiceMemory;
import it.nextre.corsojava.utils.EntityConverter;
import it.nextre.corsojava.utils.JwtGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped

@LookupIfProperty(name = "source.Mem", stringValue = "mem")
public class UserAdminServiceMemory extends UserServiceMemory implements UserAdminService {

	 public UserAdminServiceMemory() {
	        // Required by CDI
	        super(null,null); 
	    }

	@Inject
	public UserAdminServiceMemory(EntityConverter entityConverter,JwtGenerator jwtGenerator) {
		super(entityConverter, jwtGenerator);
	}

	@Override
	public void createUser(UserDTO userDTO, UserDTO user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<UserDTO> getAllUsers(UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<UserDTO> findById(Long id, UserDTO user) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public PagedResult<UserDTO> getAllUsersPag(int page, int size, UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}





}
