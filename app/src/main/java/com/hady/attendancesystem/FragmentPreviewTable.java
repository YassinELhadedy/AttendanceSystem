package com.hady.attendancesystem;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Hussein Hady on 18/03/2016.
 */
public class FragmentPreviewTable extends Fragment {

    AttendanceDataBase att_db;
    ListView lv_spc_table;
    SimpleAdapter sim_adapt_table;
    ArrayList< HashMap<String, String> > arr_list = new ArrayList< HashMap<String, String> >();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.preview_table_layout, container, false);

        lv_spc_table = (ListView) v.findViewById(R.id.lv_spc_table);

        att_db = new AttendanceDataBase (container.getContext(), MainActivity.ac_db_n_pass);

        Cursor res = att_db.getAllData();
        if(res.getCount()!= 0) {
            while(res.moveToNext()) {
                HashMap <String, String> map = new HashMap <String, String>();
                map.put("id", String.valueOf(res.getString(0)));
                map.put("name", res.getString(1));
                map.put("grade", res.getString(2));
                map.put("dep", res.getString(3));
                map.put("att", String.valueOf(res.getString(4)));
                map.put("dates", res.getString(5));
                arr_list.add(map);
            }
        }

        sim_adapt_table = new SimpleAdapter
                (container.getContext(),
                        arr_list,
                        R.layout.row_preview_table,
                        new String[]{"id", "name", "grade", "dep", "att", "dates"},
                        new int[]{R.id.cell_id, R.id.cell_name, R.id.cell_grade, R.id.cell_department, R.id.cell_attendance, R.id.cell_dates});
        lv_spc_table.setAdapter(sim_adapt_table);

        return v;
    }

}
