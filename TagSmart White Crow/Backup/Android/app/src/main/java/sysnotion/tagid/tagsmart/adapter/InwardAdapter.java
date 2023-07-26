package sysnotion.tagid.tagsmart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sysnotion.tagid.tagsmart.R;
import sysnotion.tagid.tagsmart.model.Inward;

public class InwardAdapter<T> extends BaseAdapter {
    Context mContext;
    LayoutInflater mInflater;
    ArrayList<Inward> mList;

    public InwardAdapter(Context context, ArrayList<Inward> list) {

        // TODO Auto-generated constructor stub
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = new ArrayList<Inward>();
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

            convertView = mInflater.inflate(R.layout.inward_row, null);
        }
        Inward inv = (Inward) mList.get(position);
        TextView intCatTV = (TextView) convertView.findViewById(R.id.dispatchTV);
        intCatTV.setText(inv.getDispatch_order());

        TextView intPercentageTV = (TextView) convertView.findViewById(R.id.dateTV);
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd");
        Date newDate= null;
        try {
            newDate = spf.parse(inv.getInwarding_date());
            spf= new SimpleDateFormat("dd MMM yyyy");

            intPercentageTV.setText(spf.format(newDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }




        TextView intQtyTV = (TextView) convertView.findViewById(R.id.QtyTV);
        intQtyTV.setText(inv.getQuantity());

        return convertView;
    }
}


