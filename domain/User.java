package domain;

import java.util.HashMap;

public class User {
    
    private String name;
    private double balance;
    private HashMap<String, Wine> wines;

    // Construtor para criar um novo utilizador
    public User(String name) {
        this.name = name;
        this.balance = 200;
        this.wines = new HashMap<>();
    }

    // Construtor para criar um utilizador a partir de um ficheiro
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
}
