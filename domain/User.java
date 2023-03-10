package domain;

import java.util.HashMap;

public class User {
    
    private String name;
    private double balance;
    private HashMap<String, Wine> wines;

    public User(String name) {
        this.name = name;
        this.balance = 200;
        this.wines = new HashMap<String, Wine>();
    }
}
