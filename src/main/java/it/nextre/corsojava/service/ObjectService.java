package it.nextre.corsojava.service;

import it.nextre.corsojava.dao.jdbc.GroupJdbcDao;
import it.nextre.corsojava.dao.jdbc.RoleJdbcDao;
import it.nextre.corsojava.dao.jdbc.TokenJdbcDao;
import it.nextre.corsojava.dao.jdbc.UserJdbcDao;
import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.Role;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;

import java.util.List;
import java.util.Optional;

public class ObjectService {
    private static ObjectService instance = new ObjectService();
    private final UserJdbcDao userDAO = UserJdbcDao.getInstance();
    private final TokenJdbcDao tokenUserDAO = TokenJdbcDao.getInstance();
    private final GroupJdbcDao groupDAO = GroupJdbcDao.getInstance();
    private final RoleJdbcDao roleDAO = RoleJdbcDao.getInstance();

    private ObjectService() {

    }

    public static ObjectService getInstance() {
        return instance;

    }

    private void setUser(Token t) {
        User user = UserJdbcDao.getInstance().getById(t.getUser().getId());
        if (user == null) throw new IllegalAccessError("group non trovato");
        setGroup(user);
        t.setUser(user);
    }

    private void setGroup(User user) {
        Group group = GroupJdbcDao.getInstance().getById(user.getGroup().getId());
        if (group == null) throw new IllegalAccessError("group non trovato");
        setRole(group);
        user.setGroup(group);
    }


    private void setRole(Group group) {
        Role role = RoleJdbcDao.getInstance().getById(group.getRole().getId());
        if (role == null) throw new IllegalAccessError("role non trovato");
        group.setRole(role);
    }

    public User getUserById(Long id) {
        User user = userDAO.getById(id);
        if (user == null) return null;
        setGroup(user);
        return user;

    }

    public Group getGroupById(Long id) {

        Group group = groupDAO.getById(id);
        if (group == null) return null;
        setRole(group);
        return group;
    }

    public Role getRoleById(Long id) {

        Role role = roleDAO.getById(id);
        return role;
    }

    public Token getTokenById(Long id) {

        Token token = tokenUserDAO.getById(id);
        if (token == null) return null;
        setUser(token);
        return token;
    }

    public User getUserByEmail(String email) {
        User user = userDAO.getByEmail(email);
        if (user == null) return null;
        setGroup(user);
        return user;

    }

    public Optional<User> getUserByEmailPassword(String email, String password) {
        Optional<User> user = userDAO.findByEmailPassword(email, password);
        if (user.isEmpty()) return user;
        setGroup(user.get());
        return user;

    }

    public Token getTokenByValue(String value) {
        Token token = tokenUserDAO.getTokenByValue(value);
        if (token == null) return null;
        setUser(token);
        return token;
    }

    public List<Token> getTokenByIdUser(Long id) {
        List<Token> token = tokenUserDAO.getTokenByIdUser(id);
        if (token == null) return null;
        for (Token t : token) {

            setUser(t);
        }
        return token;
    }

    public List<User> getAllUsers() {
        List<User> users = userDAO.getAll();
        if (users == null) return null;
        for (User t : users) {

            setGroup(t);
        }
        return users;
    }

    public List<Group> getAllGroup() {
        List<Group> groups = groupDAO.getAll();
        if (groups == null) return null;
        for (Group t : groups) {

            setRole(t);
        }
        return groups;
    }

}
