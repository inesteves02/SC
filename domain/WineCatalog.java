package domain;

import java.util.HashMap;

public class WineCatalog {
    
    private HashMap<String, Wine> wines;

    public WineCatalog() {
        this.wines = new HashMap<String, Wine>();
    }
}
