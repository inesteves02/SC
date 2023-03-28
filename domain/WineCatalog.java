package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WineCatalog {
    
    private List<Wine> wines;

    public WineCatalog(UserCatalog userCatalog) {

        wines = new ArrayList<Wine>();
        
        //Encontra todos os vinhos dos usuários e os adiciona ao hashmap
        for (User user : userCatalog.getUsers().values()) {
            for (Wine wine : user.getWines().values()) {
                if (wine.isForSale())
                    wines.add(wine);
            }
        }
    }

    public void addWine(Wine wine) {
        wines.add(wine);
    }

    public List<Wine> getWines(String name) {
        return this.wines.stream()
                    .filter(wine -> wine.getName().equals(name))
                    .collect(Collectors.toList());
    }

    public void updateWine (Wine wine) {
        wines.forEach(w -> {
            if(w.getName().equals(wine.getName())) {
                wines.set(wines.indexOf(w), wine);
            }
        });
    }
}
