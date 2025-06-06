package it.nextre.corsojava.service.roleService;

import java.util.List;
import java.util.Optional;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.RoleService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped

@LookupIfProperty(name = "source.Mem", stringValue = "mem")
public class RoleServiceMemory implements RoleService {

	@Override
	public void create(RoleDTO roleDTO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(RoleDTO roleDTO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(RoleDTO roleDTO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Optional<RoleDTO> findById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<RoleDTO> getAllRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedResult<RoleDTO> getAllRolesPag(int page, int size) {
		// TODO Auto-generated method stub
		return null;
	}


}
