import java.util.HashMap;
import WineCatalog;

public class User {
    
    private String name;
    private double balance;

    public User(String name) {
        this.name = name;
        this.balance = 200;
        this.winesNotForSale = new HashMap<String, Wine>();
        this.winesForSale = new HashMap<String, Wine>();
    }
}
