package it.nextre.corsojava.service.groupService;

import java.util.List;
import java.util.Optional;

import io.quarkus.arc.lookup.LookupIfProperty;
import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.pagination.PagedResult;
import it.nextre.aut.service.GroupService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped

@LookupIfProperty(name = "source.Mem", stringValue = "mem")
public class GroupServiceMemory implements GroupService {

	@Override
	public void create(GroupDTO group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GroupDTO group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(GroupDTO group) {
		// TODO Auto-generated method stub

	}

	@Override
	public Optional<GroupDTO> findById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<GroupDTO> getAllGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PagedResult<GroupDTO> getAllGroupsPag(int page, int size) {
		// TODO Auto-generated method stub
		return null;
	}

}
