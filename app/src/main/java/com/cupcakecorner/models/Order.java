package com.cupcakecorner.models;

public class Order {
    private int id;
    private String details;
    private String pickupDate;
    private int totalPrice;
    private int quantity;
    private String category;
    private String username;
    private String status;

    public Order() {
    }

    public Order(String details) {
        this.details = details;
        parseDetails(details);
    }

    public void parseDetails(String details) {

        String[] parts = details.split(",");

        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length < 2)
                continue;

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            try {
                switch (key) {
                    case "Username":
                        this.username = value;
                        break;
                    case "Category":
                        this.category = value;
                        break;
                    case "Quantity":
                        this.quantity = Integer.parseInt(value);
                        break;
                    case "Pickup Date":
                        this.pickupDate = value;
                        break;
                    case "Price":
                        this.totalPrice = Integer.parseInt(value.replaceAll("[^0-9]", ""));
                        break;
                    case "Status:":
                        this.status = value;
                        break;
                    default:
                        System.out.println("default :  " + key + " : " + value);
                        break;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parsing value for " + key + ": " + value);
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void updateDetails(String username, String category, int quantity, String pickupDate, int totalPrice,
            String status) {
        this.details = "Username: " + username + ", Category: " + category + ", Quantity: " + quantity
                + ", Pickup Date: " + pickupDate + ", Price: LKR. " + totalPrice + ", Status: " + status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        parseDetails(details);
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;

        updateDetails(username, category, quantity, pickupDate, totalPrice, status);
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
        updateDetails(username, category, quantity, pickupDate, totalPrice, status);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateDetails(username, category, quantity, pickupDate, totalPrice, status);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        updateDetails(username, category, quantity, pickupDate, totalPrice, status);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        updateDetails(username, category, quantity, pickupDate, totalPrice, status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        updateDetails(username, category, quantity, pickupDate, totalPrice, status);
    }
}
