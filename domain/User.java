package domain;

import java.util.HashMap;

public class User {
    
    private String name;
    private double balance;
    private HashMap<String, Wine> wines;

    // Constructor to create a new user
    public User(String name) {
        this(name, 200, new HashMap<>());
    }

    // Constructor to create a user from a file 
    public User(String name, double balance, HashMap<String, Wine> wines) {
        this.name = name;
        this.balance = balance;
        this.wines = wines;
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
}
