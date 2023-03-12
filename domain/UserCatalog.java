package domain;

import java.util.HashMap;

public class UserCatalog {
    
    private HashMap<String, User> users;

    public UserCatalog(HashMap<String, User> users) {
        this.users = users;
    }

    public User getUser(String name) {
        return users.get(name);
    }

    public void addUser(String name) {
        users.put(name, new User(name, 200, new HashMap<String, Wine>()));
    }
}
