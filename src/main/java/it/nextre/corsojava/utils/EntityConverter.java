package it.nextre.corsojava.utils;

import it.nextre.aut.dto.GroupDTO;
import it.nextre.aut.dto.RoleDTO;
import it.nextre.aut.dto.UserDTO;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EntityConverter {
    public UserDTO fromEntity(User user) {
        // TODO convert
        return null;
    }

    public User fromDTO(UserDTO dto) {
        // TODO convert
        return null;
    }

    public GroupDTO fromEntity(Group group) {
        // TODO convert
        return null;
    }

    public Group fromDTO(GroupDTO dto) {
        // TODO convert
        return null;
    }

    public RoleDTO fromEntity(Role role) {
        // TODO convert
        return null;
    }

    public Role fromDTO(RoleDTO dto) {
        // TODO convert
        return null;
    }
}
