package com.vsi.smart.dairy1;

/**
 * Created by sac on 25/03/2018.
 */

public class ItemMaster {
    public ItemMaster(long itmcode, String itmName)
    {
        itemCode = itmcode;
        itemName = itmName;
    }
    public ItemMaster(long itmcode, String itmName, long itmgroupcode, String itmgroupName)
    {
        itemCode = itmcode;
        itemName = itmName;
        itemGroupCode = itmgroupcode;
        itemGroupName = itmgroupName;
    }
    public long itemCode;

    public String itemName;

    public long itemGroupCode;

    public String itemGroupName;

}
