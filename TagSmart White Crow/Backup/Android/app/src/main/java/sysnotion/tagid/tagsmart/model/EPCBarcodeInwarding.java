package sysnotion.tagid.tagsmart.model;

public class EPCBarcodeInwarding implements Comparable<EPCBarcodeInwarding>  {
    String epc;
    String barcode;
    int rfidQuantity;


    public EPCBarcodeInwarding(String epc, String br, int qty) {
        this.epc = epc;
        this.rfidQuantity = qty;
        barcode = br;
    }

    public EPCBarcodeInwarding(String epc, String ean) {
        barcode =  ean;
        this.epc = epc;
        rfidQuantity = 0;
    }

    public EPCBarcodeInwarding( ) {

    }
    public int getRFIDQuantity() {
        return rfidQuantity;
    }

    public void setRFIDQuantity(int st) {
        this.rfidQuantity = st;
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

    @Override
    public int compareTo(EPCBarcodeInwarding epcInventory) {
        return this.epc.compareTo(epcInventory.epc);
    }
}
