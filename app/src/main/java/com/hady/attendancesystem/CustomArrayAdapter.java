package com.hady.attendancesystem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Hussein Hady on 14/03/2016.
 */
public class CustomArrayAdapter extends ArrayAdapter {

    private Context ctx;

    public CustomArrayAdapter(Context context, int resource, LinkedList objects) {
        super(context, resource, objects);
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView;
        if (convertView == null) {
            newView = View.inflate(ctx, R.layout.scan_list_view, null);
        } else {
            newView = convertView;
        }
        String txt = (String) this.getItem(position);
        ((TextView) newView.findViewById(R.id.list_num)).setText((position + 1) + " :");
        ((TextView) newView.findViewById(R.id.list_txt)).setText(txt);
        return newView;
    }
}