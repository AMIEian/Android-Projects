package sysnotion.tagid.tagsmart.model;

public class Inventory {
    private String category;
    private String percentage;
    private String quantity;

    public Inventory(String category, String percentage, String quantity) {
        this.category = category;
        this.percentage = percentage;
        this.quantity = quantity;
    }
    public  Inventory()
    {

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
