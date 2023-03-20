package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    
    private final String name;
    private double balance;
    private HashMap<String, Wine> wines;
    List<Message> messages;

    // Constructor to create a new user
    public User(String name) {
        this(name, 200, new HashMap<>(), new ArrayList<>());
    }

    // Constructor to create a user from a file 
    public User(String name, double balance, HashMap<String, Wine> wines, List<Message> messages) {
        this.name = name;
        this.balance = balance;
        this.wines = wines;
        this.messages = messages;
    } 

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public Wine getWine(String name) {
        return wines.get(name);
    }

    public HashMap<String, Wine> getWines() {
        return wines;
    }

    public boolean haveWine(String name) {
        return wines.containsKey(name);
    }

    public void addWine(Wine wine) {
        wines.put(wine.getName(), wine);
    }

    public void setWines(HashMap<String, Wine> wines) {
        this.wines = wines;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addMessage(Message msg) {
        messages.add(msg);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void clearMessages() {
        if (messages != null) {
            messages.clear();
        }
    }
}
