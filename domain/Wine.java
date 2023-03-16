package domain;

public class Wine {

    private String sellerName;
    private String name;
    private String image;
    private double price;
    private int quantity;
    private double rating;
    private boolean isForSale;

    public Wine(String name, String image, double price, int quantity, double rating, String sellerName, boolean isForSale) {
        this.name = name;
        this.image = image;
        this.rating = rating;
        this.quantity = quantity;
        this.price = price;
        this.sellerName = sellerName;
        this.isForSale = isForSale;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getRating() {
        return rating;
    }

    public String getSellerName() {
        return sellerName;
    }

    public boolean isForSale() {
        return isForSale;
    }

    public void setPrice(double value) {
        price = value;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setIsForSale(boolean b) {
        isForSale = b;
    }
}