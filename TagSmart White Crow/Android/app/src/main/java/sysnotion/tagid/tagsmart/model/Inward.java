package sysnotion.tagid.tagsmart.model;

public class Inward {

    String dispatch_order;
    String quantity;
    String inwarding_date;
    public Inward(){}

    public Inward(String d, String q, String date)
    {
        dispatch_order = d;
        quantity = q;
        inwarding_date = date;
    }

    public String getDispatch_order() {
        return dispatch_order;
    }

    public void setDispatch_order(String dispatch_order) {
        this.dispatch_order = dispatch_order;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getInwarding_date() {
        return inwarding_date;
    }

    public void setInwarding_date(String inwarding_date) {
        this.inwarding_date = inwarding_date;
    }
}
