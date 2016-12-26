package com.hady.attendancesystem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by Hussein Hady on 13/03/2016.
 */
public class SpecificTable extends Activity {

    AttendanceDataBase att_db;
    TabHost tab_lay;
    ToggleButton tbu_sc_m;
    int sc_no, lang_res;
    final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    LinkedList ll_list = new LinkedList(), ll_full_list = new LinkedList();
    ListView lv_list, lv_list_long;
    CustomArrayAdapter adapt_list;
    TextToSpeech tts;
    Button bu_stop, bu_scan;
    String disp_t_n;
    AlertDialog ad_list_single, ad_list_long;
    TextView tv_list_single;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spcific_table);

        bu_scan = (Button) findViewById(R.id.bu_scan);
        bu_stop = (Button) findViewById(R.id.bu_stop);

        Intent intent = getIntent();
        disp_t_n = new String(intent.getStringExtra("displayed_table_name"));

        tab_lay = (TabHost) findViewById(R.id.tabHost);
        tab_lay.setup();
        TabHost.TabSpec spec = tab_lay.newTabSpec("list");
        spec.setIndicator("Preparation List");
        spec.setContent(R.id.list);
        tab_lay.addTab(spec);
        spec = tab_lay.newTabSpec("group");
        spec.setIndicator(disp_t_n);
        spec.setContent(R.id.group);
        tab_lay.addTab(spec);

        tab_lay.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId == "group") {
                    Fragment fragment = new FragmentPreviewTable();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_space_preview_table, fragment);
                    ft.commit();
                }
            }
        });

        tbu_sc_m = (ToggleButton) findViewById(R.id.tbu_s_m);
        tbu_sc_m.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    sc_no = 2;
            }
        });

        tts = new TextToSpeech(SpecificTable.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    lang_res = tts.setLanguage(Locale.ENGLISH);
                }
                else {
                    Toast.makeText(getApplicationContext(),"feature not supported in your device",Toast.LENGTH_SHORT).show();
                }
            }
        });

        lv_list = (ListView) findViewById(R.id.lv_list);
        adapt_list = new CustomArrayAdapter(this,android.R.layout.select_dialog_item,ll_list);
        lv_list.setAdapter(adapt_list);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_list_single = new TextView(SpecificTable.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(SpecificTable.this);
                HashMap<String, String> map = new HashMap<String, String>();
                map = (HashMap<String, String>) ll_full_list.get(position);
                tv_list_single.setPadding(10,5,10,5);
                tv_list_single.setTextSize(15);
                tv_list_single.setText("ID : " + map.get("ID") + '\n' + "Name : " + map.get("Name") + '\n' + "Grade : " + map.get("Grade") + '\n'
                        + "Department : " + map.get("Department") + '\n'+ "Date : " + map.get("Date"));
                builder.setView(tv_list_single);
                ad_list_single = builder.create();
                ad_list_single.show();
            }
        });
        lv_list_long = new ListView(this);
        ArrayAdapter arr_adapt_list_long = ArrayAdapter.createFromResource(this, R.array.list_long, android.R.layout.simple_list_item_1);
        lv_list_long.setAdapter(arr_adapt_list_long);
        AlertDialog.Builder builder_list_long = new AlertDialog.Builder(this);
        builder_list_long.setView(lv_list_long);
        ad_list_long = builder_list_long.create();
        lv_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position_P, long id) {
                ad_list_long.show();
                lv_list_long.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position_Ch, long id) {
// speak
                        if (position_Ch == 0) {
                            tts.speak(ll_list.get(position_P).toString(), TextToSpeech.QUEUE_FLUSH, null);
                        }
// delete
                        else if (position_Ch == 1) {
                            showDialogRemoveCon("Do you want to remove number "+(position_P+1)+" ?"+'\n'+ll_list.get(position_P), position_P);
                        }
                        ad_list_long.dismiss();
                        ad_list_long.cancel();
                    }
                });
                return true;
            }
        });

        bu_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(ACTION_SCAN);
                    i.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(i, 0);
                } catch (ActivityNotFoundException anfe) {
                    showDialogSc().show();
                }
            }
        });

        bu_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts!=null)
                    tts.stop();
            }
        });

    }

    public void oCClear (View view) {
        if (!ll_list.isEmpty()) {
            showDialogRemoveCon("Do you want to clear all ?", -1);
        }
    }

    public void oCSave (View view) {
        if(ll_list.isEmpty());
        else {
            att_db = new AttendanceDataBase(this, MainActivity.ac_db_n_pass);
            HashMap<String, String> map = new HashMap<String, String>();
            String name, grade, dep, date;
            for (int i = 0; i <= ll_list.indexOf(ll_list.getLast()); i++) {
                map = (HashMap<String, String>) ll_full_list.get(i);
                int id = Integer.parseInt(map.get("ID"));
                name = map.get("Name");
                grade = map.get("Grade");
                dep = map.get("Department");
                date = map.get("Date");
                if (att_db.insertData(id, name, grade, dep, date)) ;
                else
                    att_db.updateData(id, date);
                map.clear();
            }
            ll_list.clear();
            adapt_list.notifyDataSetChanged();
            ll_full_list.clear();
        }
    }

    public AlertDialog showDialogSc () {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(SpecificTable.this);
        downloadDialog.setTitle("No Scanner Found");
        downloadDialog.setMessage("Download a scanner code activity ?");
        downloadDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    SpecificTable.this.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {}
            }
        });
        downloadDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {}});
        return downloadDialog.show();
    }

    public AlertDialog showDialogRemoveCon(String remove_mess, final int remove_pos) {
        AlertDialog.Builder conRemoveDialog = new AlertDialog.Builder(SpecificTable.this);
        conRemoveDialog.setTitle("Confirmation");
        conRemoveDialog.setMessage(remove_mess);
        conRemoveDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (remove_pos == -1) {
                    ll_list.clear();
                    adapt_list.notifyDataSetChanged();
                    ll_full_list.clear();
                }
                else {
                    ll_list.remove(remove_pos);
                    adapt_list.notifyDataSetChanged();
                    ll_full_list.remove(remove_pos);
                }
            }
        });
        conRemoveDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}});
        return conRemoveDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String sc_res = intent.getStringExtra("SCAN_RESULT");
                String spl_sc_res [] = sc_res.split("\n");
                if (ll_list.contains(spl_sc_res[1])) {
                    Toast.makeText(getApplicationContext(), " Repeated : No = " + (ll_list.indexOf(spl_sc_res[1]) + 1) + " ", Toast.LENGTH_LONG).show();
                } else {
                    ll_list.add(spl_sc_res[1]);
                    adapt_list.notifyDataSetChanged();
                    HashMap <String, String> map = new HashMap<String, String>();
                    map.put("ID", spl_sc_res[0]);
                    map.put("Name", spl_sc_res[1]);
                    map.put("Grade", spl_sc_res[2]);
                    map.put("Department", spl_sc_res[3]);
                    map.put("Date", DateFormat.getDateTimeInstance().format(new Date()));
                    ll_full_list.add(map);
                }
                if (sc_no == 2)
                    bu_scan.callOnClick();
                else ;

            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        bu_stop.callOnClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts!=null) {
            tts.shutdown();
        }
    }

}