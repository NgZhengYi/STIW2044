package food_system.project.stiw2044.com.stiw2044_project;

public class Food {
    private String foodOwnerName, foodID, foodName, foodPrice;

    public Food() {

    }

    public Food(String foodOwnerName, String foodID, String foodName, String foodPrice) {
        this.foodOwnerName = foodOwnerName;
        this.foodID = foodID;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
    }

    public String getFoodOwnerName() {
        return foodOwnerName;
    }

    public String getFoodID() {
        return foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }
}
