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

    // returns the name of the wine
    public String getName() {
        return name;
    }

    // returns the price of the wine
    public double getPrice() {
        return price;
    }

    // returns the quantity of that wine avaiable
    public int getQuantity() {
        return quantity;
    }

    // returns the rating given to that wine
    public double getRating() {
        return rating;
    }

    //returns the seller's name
    public String getSellerName() {
        return sellerName;
    }

    //returns true if it's for sale
    public boolean isForSale() {
        return isForSale;
    }

    // updates/sets the price of the wine
    public void setPrice(double value) {
        price = value;
    }

    // updates/sets the quantity of the wine
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //updates the rating given to the wine and does the mean of all classifications
    public void setRating(double rating) {
        this.ratingCount++;
        if (this.rating == 0){
            this.rating = rating;
        }
        else {
            this.rating = (this.rating + rating) / this.ratingCount;
        }
    }

    //sets the wine for sale
    public void setIsForSale(boolean b) {
        isForSale = b;
    }

    // returns the image format
    public String getImageFormat() {
        return imageFormat;
    }

    // sets the image with the given format
    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }
}