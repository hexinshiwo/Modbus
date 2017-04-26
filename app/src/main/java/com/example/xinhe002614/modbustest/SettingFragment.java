package com.example.xinhe002614.modbustest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Whisper on 2017/4/25.
 */

public class SettingFragment extends Fragment {
    private View mRoot;
    private Context context;
    private SQLiteDatabase modbus;
    private EditText ip_1, ip_2, ip_3, ip_4, ip_5, ip_6, ip_7, ip_8, ip_9, ip_10, ip_11;
    private EditText port_1, port_2, port_3, port_4, port_5, port_6, port_7, port_8, port_9, port_10, port_11;
    private Button save;

    public static SettingFragment newInstance(int index) {
        SettingFragment fragment = new SettingFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("index", index);
        fragment.setArguments(arguments);
        return fragment;
    }

    public SettingFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_setting, container, false);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();
        modbus = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/modbus.db3", null);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public void initView() {
        ip_1 = (EditText) mRoot.findViewById(R.id.ip_address_1);
        ip_2 = (EditText) mRoot.findViewById(R.id.ip_address_2);
        ip_3 = (EditText) mRoot.findViewById(R.id.ip_address_3);
        ip_4 = (EditText) mRoot.findViewById(R.id.ip_address_4);
        ip_5 = (EditText) mRoot.findViewById(R.id.ip_address_5);
        ip_6 = (EditText) mRoot.findViewById(R.id.ip_address_6);
        ip_7 = (EditText) mRoot.findViewById(R.id.ip_address_7);
        ip_8 = (EditText) mRoot.findViewById(R.id.ip_address_8);
        ip_9 = (EditText) mRoot.findViewById(R.id.ip_address_9);
        ip_10 = (EditText) mRoot.findViewById(R.id.ip_address_10);
        ip_11 = (EditText) mRoot.findViewById(R.id.ip_address_11);
        port_1 = (EditText) mRoot.findViewById(R.id.port_1);
        port_2 = (EditText) mRoot.findViewById(R.id.port_2);
        port_3 = (EditText) mRoot.findViewById(R.id.port_3);
        port_4 = (EditText) mRoot.findViewById(R.id.port_4);
        port_5 = (EditText) mRoot.findViewById(R.id.port_5);
        port_6 = (EditText) mRoot.findViewById(R.id.port_6);
        port_7 = (EditText) mRoot.findViewById(R.id.port_7);
        port_8 = (EditText) mRoot.findViewById(R.id.port_8);
        port_9 = (EditText) mRoot.findViewById(R.id.port_9);
        port_10 = (EditText) mRoot.findViewById(R.id.port_10);
        port_11 = (EditText) mRoot.findViewById(R.id.port_11);
        save = (Button) mRoot.findViewById(R.id.save);
        save.setOnClickListener(clickListener);
    }

    public void initData() {
        Cursor cur = modbus.rawQuery("select * from ip_table", null);
        if (cur.moveToNext()) {
            ip_1.setText(cur.getString(1));
            port_1.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_2.setText(cur.getString(1));
            port_2.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_3.setText(cur.getString(1));
            port_3.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_4.setText(cur.getString(1));
            port_4.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_5.setText(cur.getString(1));
            port_5.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_6.setText(cur.getString(1));
            port_6.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_7.setText(cur.getString(1));
            port_7.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_8.setText(cur.getString(1));
            port_8.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_9.setText(cur.getString(1));
            port_9.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_10.setText(cur.getString(1));
            port_10.setText(cur.getString(2));
        }
        if (cur.moveToNext()) {
            ip_11.setText(cur.getString(1));
            port_11.setText(cur.getString(2));
        }
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save:
                    saveData();
                    break;
            }
        }
    };

    public void saveData() {
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 1",
                new String[]{ip_1.getText().toString(), port_1.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 2",
                new String[]{ip_2.getText().toString(), port_2.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 3",
                new String[]{ip_3.getText().toString(), port_3.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 4",
                new String[]{ip_4.getText().toString(), port_4.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 5",
                new String[]{ip_5.getText().toString(), port_5.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 6",
                new String[]{ip_6.getText().toString(), port_6.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 7",
                new String[]{ip_7.getText().toString(), port_7.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 8",
                new String[]{ip_8.getText().toString(), port_8.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 9",
                new String[]{ip_9.getText().toString(), port_9.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 10",
                new String[]{ip_10.getText().toString(), port_10.getText().toString()});
        modbus.execSQL("update ip_table set ip_address = ?,port = ? where _id = 11",
                new String[]{ip_11.getText().toString(), port_11.getText().toString()});
    }
}
