package sysnotion.tagid.tagsmart.model;

public class BarcodeEPCSearch {

    private String barcode;
    private String epc;
    public BarcodeEPCSearch() {
    }

    public BarcodeEPCSearch(String barcode, String epc) {
        this.barcode = barcode;
        this.epc = epc;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

}
