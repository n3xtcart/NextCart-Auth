package it.nextre.corsojava.service.role;

import java.util.List;
import java.util.Optional;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.RoleService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped

@LookupIfProperty(name = "source.Mem", stringValue = "mem")
public class RoleServiceMemory implements RoleService {

	@Override
	public void create(RoleDTO roleDTO, UserDTO user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(RoleDTO roleDTO, UserDTO user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(RoleDTO roleDTO, UserDTO user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Optional<RoleDTO> findById(Long id, UserDTO user) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<RoleDTO> getAllRoles(UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedResult<RoleDTO> getAllRolesPag(int page, int size, UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}}
