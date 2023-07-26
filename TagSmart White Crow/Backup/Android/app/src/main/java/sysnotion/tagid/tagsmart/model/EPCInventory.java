package sysnotion.tagid.tagsmart.model;

public class EPCInventory implements Comparable<EPCInventory>  {
    String epc;
    String category;
    String isPresent;

    public EPCInventory(String epc, String category, String ip) {
        this.epc = epc;
        this.category = category;
        this.isPresent = ip;
    }
    public EPCInventory(String epc) {
        this.epc = epc;
    }
    public EPCInventory( ) {

    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(String ip) {
        this.isPresent = ip;
    }

    @Override
    public int compareTo(EPCInventory epcInventory) {
        return this.epc.compareTo(epcInventory.epc);
    }
}
