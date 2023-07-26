package sysnotion.tagid.tagsmart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sysnotion.tagid.tagsmart.R;
import sysnotion.tagid.tagsmart.model.BarcodeEPCSearch;

public class MissingEPCAdapter<T> extends BaseAdapter {
    Context mContext;
    LayoutInflater mInflater;
    ArrayList<BarcodeEPCSearch> mList;

    public MissingEPCAdapter(Context context, ArrayList<BarcodeEPCSearch> list) {

        // TODO Auto-generated constructor stub
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = new ArrayList<BarcodeEPCSearch>();
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

            convertView = mInflater.inflate(R.layout.search_item_row, null);
        }
        BarcodeEPCSearch inv = (BarcodeEPCSearch) mList.get(position);
        TextView intCatTV = (TextView) convertView.findViewById(R.id.intCatTV);
        intCatTV.setText(inv.getBarcode());
        TextView intQtyTV = (TextView) convertView.findViewById(R.id.intQtyTV);
        intQtyTV.setText(inv.getEpc());

        return convertView;
    }
}


