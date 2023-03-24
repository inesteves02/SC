package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WineCatalog {
    
    private List<Wine> wines;

    public WineCatalog(UserCatalog userCatalog) {

        wines = new ArrayList<Wine>();
        
        // finds all the wines from the users and adds it to a hashmap
        for (User user : userCatalog.getUsers().values()) {
            for (Wine wine : user.getWines().values()) {
                if (wine.isForSale())
                    wines.add(wine);
            }
        }
    }
    // adds the wine to the wineCatalog
    public void addWine(Wine wine) {
        wines.add(wine);
    }

    //returns all the wines present in the wineCatalog
    public List<Wine> getWines(String name) {
        return this.wines.stream()
                    .filter(wine -> wine.getName().equals(name))
                    .collect(Collectors.toList());
    }
    // updates the wineCatalog with the new wine information
    public void updateWine (Wine wine) {
        wines.forEach(w -> {
            if(w.getName().equals(wine.getName())) {
                wines.set(wines.indexOf(w), wine);
            }
        });
    }
}
