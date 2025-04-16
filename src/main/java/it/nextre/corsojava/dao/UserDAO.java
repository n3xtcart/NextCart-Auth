package it.nextre.corsojava.dao;


import it.nextre.corsojava.entity.User;

public class UserDAO extends Dao<User> {

    public UserDAO() {
        super();
    }

    @Override
    public void update(Long id, User item) {
        User user = database.get(id);
        if (item.getNome() != null) user.setNome(item.getNome());
        if (item.getEmail() != null) user.setEmail(item.getEmail());
        if (item.getPassword() != null) user.setPassword(item.getPassword());
        if (item.getCognome() != null) user.setCognome(item.getCognome());
        if (item.getGroup() != null) user.setGroup(item.getGroup());


    }


    public User getByEmail(String email) {
        var u = database.values().stream().filter(user -> user.getNome().equals(email)).findFirst();
        return u.orElse(null);
    }

}
