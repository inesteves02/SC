package domain;

import java.util.HashMap;

public class UserCatalog {
    
    private HashMap<String, User> users;

    public UserCatalog() {
        users = new HashMap<String, User>();
    }

    public User getUser(String name) {
        return users.get(name);
    }

    public void addUser(String name) {
        users.put(name, new User(name));
    }
}
