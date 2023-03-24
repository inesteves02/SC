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

    // returns the name of the user
    public String getName() {
        return name;
    }

    // returns the balence of the user
    public double getBalance() {
        return balance;
    }

    // returns the wine with that name
    public Wine getWine(String name) {
        return wines.get(name);
    }


    public HashMap<String, Wine> getWines() {
        return wines;
    }

    //Returns true if it has the specific wine
    public boolean haveWine(String name) {
        return wines.containsKey(name);
    }

    // adds wine
    public void addWine(Wine wine) {
        wines.put(wine.getName(), wine);
    }

    public void setWines(HashMap<String, Wine> wines) {
        this.wines = wines;
    }

    //updates the balance
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // sends a message
    public void addMessage(Message msg) {
        messages.add(msg);
    }

    // returns the messages
    public List<Message> getMessages() {
        return messages;
    }

    //clears the messages
    public void clearMessages() {
        if (messages != null) {
            messages.clear();
        }
    }
}
