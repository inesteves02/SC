package domain;

public class Wine {

    private String sellerName;
    private String name;
    private double price;
    private int quantity;
    private double rating;
    private int ratingCount;
    private boolean isForSale;
    private String imageFormat;

    public Wine(String name, double price, int quantity, double rating, String sellerName, boolean isForSale, String imageFormat) {
        this.name = name;
        this.rating = rating;
        this.quantity = quantity;
        this.price = price;
        this.sellerName = sellerName;
        this.ratingCount = 0;
        this.isForSale = isForSale;
        this.imageFormat = imageFormat;
    }

    public String getName() {
        return name;
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

    public void setRating(double rating) {
        this.ratingCount++;
        if (this.rating == 0){
            this.rating = rating;
        }
        else {
            this.rating = (this.rating + rating) / this.ratingCount;
        }
        }

    public void setIsForSale(boolean b) {
        isForSale = b;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }
}