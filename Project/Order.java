package food_system.project.stiw2044.com.stiw2044_project;

public class Order {
    String orderID, orderTime, buyerName, foodID, foodSellerName, foodName;

    public Order() {}

    public Order(String orderID, String orderTime, String buyerName, String foodID, String foodSellerName, String foodName) {
        this.orderID = orderID;
        this.orderTime = orderTime;
        this.buyerName = buyerName;
        this.foodID = foodID;
        this.foodSellerName = foodSellerName;
        this.foodName = foodName;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getBuyerName() { return buyerName; }

    public String getFoodID() {
        return foodID;
    }

    public String getFoodSellerName() {
        return foodSellerName;
    }

    public String getFoodName() {
        return foodName;
    }
}
