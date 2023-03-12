package domain;

public class Wine {

    private String name;
    private String image;
    private double price;
    private int quantity;
    private double rating;
    private boolean isForSale;

    public Wine(String name, String image, double price, int quantity, double rating, boolean isForSale) {
        this.name = name;
        this.image = image;
        this.rating = rating;
        this.quantity = quantity;
        this.price = price;
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

    public boolean isForSale() {
        return isForSale;
    }
}