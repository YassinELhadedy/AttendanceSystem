package com.hady.attendancesystem;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.LinkedList;

public class MainActivity extends Activity {

    SharedPreferences shr_pfs_table_lv;
    static SharedPreferences shr_pfs_db_names;
    EditText et_table_name, et_rename_table;
    LinkedList ll_tables = new LinkedList();
    AlertDialog ad_tables_add, ad_table_menu, ad_del_conf, ad_rename_tab;
    ListView lv_tables, lv_table_menu; ArrayAdapter arr_adapt_Tables, arr_adapt_table_menu; AttendanceDataBase att_db;
    static String ac_db_n_pass;
    TextView tv_no_group;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_no_group = (TextView) findViewById(R.id.tv_no_group);
        shr_pfs_table_lv = getSharedPreferences("Tablelv", MODE_PRIVATE);
        shr_pfs_db_names = getSharedPreferences("Groupsnames", MODE_PRIVATE);


// add dialog creation ****************************************************************
        et_table_name = new EditText(this);
        AlertDialog.Builder builder_tables = new AlertDialog.Builder(this);
        builder_tables.setTitle("Create New Group");
        builder_tables.setMessage("Enter Group Name : ");
        builder_tables.setView(et_table_name);
        builder_tables.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String table_name = et_table_name.getText().toString();
                if (table_name.matches(""))
                    Toast.makeText(MainActivity.this, "Please First Enter The Group Name", Toast.LENGTH_LONG).show();
                else {
                    if (ll_tables.contains(table_name)) {
                        Toast.makeText(MainActivity.this, "Duplicate Name", Toast.LENGTH_LONG).show();
                    } else {
                        if (createTable(table_name))
                            Toast.makeText(MainActivity.this, "Created", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "Can't Create This", Toast.LENGTH_SHORT).show();
                        et_table_name.setText("");
                    }
                }
            }
        });
        builder_tables.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et_table_name.setText("");
            }
        });
        builder_tables.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                et_table_name.setText("");
            }
        });
        builder_tables.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                et_table_name.setText("");
            }
        });
        ad_tables_add = builder_tables.create();

// tables list view creation
        lv_tables = (ListView) findViewById (R.id.lv_tables);
        arr_adapt_Tables = new ArrayAdapter(this, android.R.layout.select_dialog_item, ll_tables);
        lv_tables.setAdapter(arr_adapt_Tables);
// menu list view creation ************************************************
        lv_table_menu = new ListView(this);
        arr_adapt_table_menu = ArrayAdapter.createFromResource(this, R.array.table_menu, android.R.layout.simple_list_item_1);
        lv_table_menu.setAdapter(arr_adapt_table_menu);
        AlertDialog.Builder builder_table_menu = new AlertDialog.Builder(this);
        builder_table_menu.setView(lv_table_menu);
        ad_table_menu = builder_table_menu.create();
// tables list view selections *************************************************************************
        lv_tables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ac_db_n_pass = getActualDBName(ll_tables.get(position).toString());
                Intent intent = new Intent("com.hady.SpecificTable");
                intent.putExtra("displayed_table_name", ll_tables.get(position).toString());
                startActivity(intent);
            }
        });
        lv_tables.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position_P, long id) {

                att_db = new AttendanceDataBase(MainActivity.this, getActualDBName(ll_tables.get(position_P).toString()));
                ad_table_menu.show();
// menu list view selections ******************************************************************************
                lv_table_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position_Ch, long id) {
// preview
                        if(position_Ch == 0) {
                            ac_db_n_pass = getActualDBName(ll_tables.get(position_P).toString());
                            Intent intent = new Intent("com.hady.PreviewTableActivity");
                            intent.putExtra("displayed_table_name", ll_tables.get(position_P).toString());
                            startActivity(intent);
                        }
// delete
                        else if(position_Ch == 1) {
                            AlertDialog.Builder builder_del_group = new AlertDialog.Builder(MainActivity.this);
                            builder_del_group.setTitle("Confirmation");
                            builder_del_group.setMessage("Do you want to delete " +'"'+" "+ll_tables.get(position_P).toString()+" "+'"'+" Group ?");
                            builder_del_group.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDatabase(getActualDBName(ll_tables.get(position_P).toString()));
                                    shr_pfs_db_names.edit().remove(ll_tables.get(position_P).toString()).commit();
                                    ll_tables.remove(position_P);
                                    arr_adapt_Tables.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    if(ll_tables.isEmpty())
                                        tv_no_group.setText("No Groups Found");
                                }
                            });
                            builder_del_group.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}});
                            ad_del_conf = builder_del_group.create();
                            ad_del_conf.show();
                        }
// rename
                        else if(position_Ch == 2) {
                            et_rename_table = new EditText(MainActivity.this);
                            AlertDialog.Builder builder_rename_tab = new AlertDialog.Builder(MainActivity.this);
                            et_rename_table.setText(ll_tables.get(position_P).toString());
                            builder_rename_tab.setView(et_rename_table);
                            builder_rename_tab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String new_table_name = et_rename_table.getText().toString();
                                    if(new_table_name.matches(""));
                                    else if(ll_tables.contains(new_table_name)) {
                                        Toast.makeText(MainActivity.this, "Duplicate Name", Toast.LENGTH_LONG).show();
                                    } else {
                                        String ac_db_n = getActualDBName(ll_tables.get(position_P).toString());
                                        shr_pfs_db_names.edit().remove(ll_tables.get(position_P).toString()).commit();
                                        shr_pfs_db_names.edit().putString(new_table_name, ac_db_n).commit();
                                        ll_tables.set(position_P, new_table_name);
                                        arr_adapt_Tables.notifyDataSetChanged();
                                    }
                                }
                            });
                            ad_rename_tab = builder_rename_tab.create();
                            ad_rename_tab.show();
                        }
// export
                        else if(position_Ch == 3) {

                        }
                        ad_table_menu.dismiss();
                        ad_table_menu.cancel();
                    }
                });
// end of menu list view selections ****************************************
                return true;
            }
        });
// end of tables list view selections ***********************************************************

    }

    public void oCAddTable(View view) {
        ad_tables_add.show();
    }

    public int newNum() {
        SharedPreferences.Editor prefEditor = shr_pfs_db_names.edit();
        prefEditor.putInt("dbnum", (shr_pfs_db_names.getInt("dbnum", 0) + 1));
        prefEditor.commit();
        return shr_pfs_db_names.getInt("dbnum", 1);
    }

    public boolean createTable(String displayed_table_name) {
        try {
            String ac_db_n = "T"+String.valueOf(newNum())+".db";
            att_db = new AttendanceDataBase(MainActivity.this, ac_db_n);
            SharedPreferences.Editor prefEditor = shr_pfs_db_names.edit();
            prefEditor.putString(displayed_table_name, ac_db_n);
            prefEditor.commit();
            ll_tables.add(displayed_table_name);
            arr_adapt_Tables.notifyDataSetChanged();
            tv_no_group.setText("");
            return true;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static String getActualDBName(String displayed_table_name) {
        return MainActivity.shr_pfs_db_names.getString(displayed_table_name, null);
    }

    public void setTablesLV() {
        shr_pfs_table_lv.edit().clear().commit();
        SharedPreferences.Editor prefEditor = shr_pfs_table_lv.edit();
        int lti = (ll_tables.size()-1);
        for (int i=0; i<=lti; i++) {
            prefEditor.putString(String.valueOf(i), ll_tables.get(i).toString());
            prefEditor.commit();
        }
        prefEditor.putInt("last_table_index", lti);
        prefEditor.commit();
    }

    public void loadTablesLV() {
        ll_tables.clear();
        arr_adapt_Tables.notifyDataSetChanged();
        for (int i = 0; i <= shr_pfs_table_lv.getInt("last_table_index", -1); i++)
            ll_tables.add(i, shr_pfs_table_lv.getString(String.valueOf(i), null));
        arr_adapt_Tables.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTablesLV();
        if(ll_tables.isEmpty())
            tv_no_group.setText("No Groups Found");
        else
            tv_no_group.setText("");
    }

    @Override
    public void onPause() {
        super.onPause();
        setTablesLV();
    }

}