package sysnotion.tagid.tagsmart.model;

public class EPCBarcodeInventory implements Comparable<EPCBarcodeInventory>  {
    String epc;
    String barcode;
    String stock_id;
    String category;
    String isPresent;

    public EPCBarcodeInventory(String epc, String category, String ip, String br, String st) {
        this.epc = epc;
        this.category = category;
        this.isPresent = ip;
        barcode = br;
        stock_id =st;
    }
    public EPCBarcodeInventory(String epc) {
        this.epc = epc;
    }
    public EPCBarcodeInventory(String epc, String ean) {
        barcode =  ean;
        this.epc = epc;
    }

    public EPCBarcodeInventory( ) {

    }

    public String getStockId() {
        return stock_id;
    }

    public void setStockId(String st) {
        this.stock_id = st;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String b) {
        this.barcode = b;
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
    public int compareTo(EPCBarcodeInventory epcInventory) {
        return this.epc.compareTo(epcInventory.epc);
    }
}
