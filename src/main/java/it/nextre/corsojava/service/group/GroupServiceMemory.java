package it.nextre.corsojava.service.group;

import java.util.List;
import java.util.Optional;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.GroupService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped

@LookupIfProperty(name = "source.Mem", stringValue = "mem")
public class GroupServiceMemory implements GroupService {

	@Override
	public void create(GroupDTO group, UserDTO user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GroupDTO group, UserDTO user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(GroupDTO group, UserDTO user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Optional<GroupDTO> findById(Long id, UserDTO user) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<GroupDTO> getAllGroups(UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedResult<GroupDTO> getAllGroupsPag(int page, int size, UserDTO user) {
		// TODO Auto-generated method stub
		return null;
	}}
