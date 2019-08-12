public class Item {
    private String pName;
    private double pPrice;

    public Item(String name, double price){
        this.pName = name;
        this.pPrice = price;
    }
    public String getpName(){
        return  pName;
    }

    public double getpPrice(){
        return pPrice;
    }
}
