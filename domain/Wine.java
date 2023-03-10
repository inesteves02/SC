package domain;

public class Wine {

    private String name;
    private String image;
    private double price;
    private int quantity;
    private double rating;
    private boolean isForSale;

    public Wine(String name, String image) {
        this.name = name;
        this.image = image;
        this.isForSale = false;
    }

    public Wine(String name, String image, double price, int quantity, double averageRating, boolean isForSale) {
        this.name = name;
        this.image = image;
        this.rating = averageRating;
        this.quantity = quantity;
        this.price = price;
        this.isForSale = isForSale;
    }

    public String getName() {
        return name;
    }
}