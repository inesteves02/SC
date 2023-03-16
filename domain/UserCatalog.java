package domain;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Wine> viewWine(String wineName) {
        return users.entrySet().stream()
            .filter(user -> user.getValue().haveWine(wineName))
            .map(user -> user.getValue().getWines().get(wineName))
            .collect(Collectors.toList());
    }

    public HashMap<String, User> getUsers() {
        return users;
    }
}
