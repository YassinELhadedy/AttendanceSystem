package com.hady.attendancesystem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PreviewTableActivity extends Activity {

    String disp_t_n;
    AttendanceDataBase att_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_table);

        Fragment fragment = new FragmentPreviewTable();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_larg_space, fragment);
        ft.commit();

        Intent intent = getIntent();
        disp_t_n = new String(intent.getStringExtra("displayed_table_name"));
        att_db = new AttendanceDataBase(this, MainActivity.getActualDBName(disp_t_n));

    }
}
