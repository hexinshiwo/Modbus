package com.example.xinhe002614.modbustest;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;

/**
 * Created by Whisper on 2017/4/25.
 */

public class ControlFragment extends Fragment {
    private View mRoot;
    private Context context;
    private Button forward_btn, backward_btn, stop_btn, coil_up, coil_down, cruise_control;
    private SQLiteDatabase modbus;

    public static ControlFragment newInstance(int index) {
        ControlFragment fragment = new ControlFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("index", index);
        fragment.setArguments(arguments);
        return fragment;
    }

    public ControlFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_control, container, false);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();
        modbus = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString() + "/modbus.db3", null);
        initView();
    }

    public void initView() {
        initWheel();
        forward_btn = (Button) mRoot.findViewById(R.id.forward_btn);
        backward_btn = (Button) mRoot.findViewById(R.id.backward_btn);
        stop_btn = (Button) mRoot.findViewById(R.id.stop_btn);
        coil_up = (Button) mRoot.findViewById(R.id.coil_up);
        coil_down = (Button) mRoot.findViewById(R.id.coil_down);
        cruise_control = (Button) mRoot.findViewById(R.id.cruise_control);
        forward_btn.setOnClickListener(clickListener);
        backward_btn.setOnClickListener(clickListener);
        stop_btn.setOnClickListener(clickListener);
        coil_up.setOnClickListener(clickListener);
        coil_down.setOnClickListener(clickListener);
        cruise_control.setOnClickListener(clickListener);
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.forward_btn:

                    break;
                case R.id.backward_btn:

                    break;
                case R.id.stop_btn:

                    break;
                case R.id.coil_up:

                    break;
                case R.id.coil_down:

                    break;
                case R.id.cruise_control:
//                    try {
//                        modbus.execSQL("drop table ip_table");//删除整个表
//                    } catch (SQLiteException ignored) {
//
//                    }
                    break;
            }
        }
    };

    private void initWheel() {
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextColor = Color.parseColor("#6CD0E8");
        style.textColor = Color.parseColor("#0288ce");
        style.textSize = 12;
        style.backgroundColor = Color.parseColor("#666666");
        style.selectedTextSize = 26;
        WheelView speed_select = (WheelView) getActivity().findViewById(R.id.speed_select);
        speed_select.setWheelAdapter(new ArrayWheelAdapter(context));
        speed_select.setSkin(WheelView.Skin.Holo);
        speed_select.setLoop(false);
        speed_select.setStyle(style);
        speed_select.setExtraText("                           km/h", Color.parseColor("#6CD0E8"), 26, 0);
        speed_select.setWheelData(createSpeed());
        speed_select.setWheelSize(5);
        speed_select.setSelection(21);
    }

    // 速度滚轮数据列表适配器
    private ArrayList<String> createSpeed() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 20; i >= 0; i--) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
        return list;
    }
}
