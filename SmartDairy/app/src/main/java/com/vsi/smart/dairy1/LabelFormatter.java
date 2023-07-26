package com.vsi.smart.dairy1;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

/**
 * Created by sac on 27/11/2017.
 */

public class LabelFormatter implements IAxisValueFormatter {
    private final DataSet mData;

    public LabelFormatter(DataSet data) {
        mData = data;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // return the entry's data which represents the label
        return (String) mData.getEntryForXValue(value, axis.getYOffset()).getData();
    }
}