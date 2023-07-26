package sysnotion.tagid.tagsmart.nLevel;

/**
 * Created by sadra on 7/29/17.
 */


import android.view.View;

public class NLevelItem implements NLevelListItem{

    private Object wrappedObject;
    private NLevelItem parent;
    private NLevelView nLevelView;
    private boolean isExpanded = false;
    int id;
    boolean ischecked;
    int parentId;
    int level;
    int total_qty;
    int scan_qty;


    public NLevelItem(Object wrappedObject, NLevelItem parent, NLevelView nLevelView) {
        this.wrappedObject = wrappedObject;
        this.parent = parent;
        this.nLevelView = nLevelView;
    }

    public Object getWrappedObject() {
        return wrappedObject;
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }
    @Override
    public NLevelListItem getParent() {
        return parent;
    }
    @Override
    public View getView() {
        return nLevelView.getView(this);
    }
    @Override
    public void toggle() {
        isExpanded = !isExpanded;
    }


    public void set_isExpanded(boolean bo) {
        isExpanded = bo;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int l) {
        this.level = l;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int id) {
        this.parentId = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public int getTotalQty() {
        return total_qty;
    }

    public void setTotalQty(int total_qty) {
        this.total_qty = total_qty;
    }

    public int getScanQty() {
        return scan_qty;
    }

    public void setScanQty(int scan_qty) {
        this.scan_qty = scan_qty;
    }
}