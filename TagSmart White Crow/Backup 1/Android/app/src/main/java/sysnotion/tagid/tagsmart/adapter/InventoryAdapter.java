package sysnotion.tagid.tagsmart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sysnotion.tagid.tagsmart.R;
import sysnotion.tagid.tagsmart.model.Inventory;

public class InventoryAdapter<T> extends BaseAdapter {
    Context mContext;
    LayoutInflater mInflater;
    ArrayList<sysnotion.tagid.tagsmart.model.Inventory> mList;

    public InventoryAdapter(Context context, ArrayList<sysnotion.tagid.tagsmart.model.Inventory> list) {

        // TODO Auto-generated constructor stub
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = new ArrayList<sysnotion.tagid.tagsmart.model.Inventory>();
        this.mList = list;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override

    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if(convertView == null) {

            convertView = mInflater.inflate(R.layout.inventory_row, null);
        }
        Inventory inv = (Inventory) mList.get(position);
        TextView intCatTV = (TextView) convertView.findViewById(R.id.intCatTV);
        intCatTV.setText(inv.getCategory());
        TextView intPercentageTV = (TextView) convertView.findViewById(R.id.intPercentageTV);
        intPercentageTV.setText(inv.getPercentage()+" %");
        TextView intQtyTV = (TextView) convertView.findViewById(R.id.intQtyTV);
        intQtyTV.setText(inv.getQuantity());

        return convertView;
    }
}


