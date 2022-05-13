package net.lokmane.projectandroidgestionstock.model;

public class Product {

    private int id;
    private String name;
    private int quantity;
    private String image;

    public Product(int id, String name, int quantity, String image) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.image = image;
    }

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
