package com.example.xinhe002614.modbustest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;

/**
 * Created by Whisper on 2017/6/15.
 */

public class ControlFragment2 extends Fragment {
    private Context context;
    private View mRoot;
    private WheelView speed_select;

    public static ControlFragment2 newInstance(int index) {
        ControlFragment2 fragment = new ControlFragment2();
        Bundle arguments = new Bundle();
        arguments.putInt("index", index);
        fragment.setArguments(arguments);
        return fragment;
    }

    public ControlFragment2() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_control2, container, false);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();
        initView();
    }

    private void initView() {
        initWheel();
    }

    private void initWheel() {
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextColor = Color.parseColor("#6CD0E8");
        style.textColor = Color.parseColor("#0288ce");
        style.textSize = 12;
        style.backgroundColor = Color.parseColor("#666666");
        style.selectedTextSize = 26;
        speed_select = (WheelView) getActivity().findViewById(R.id.speed_select);
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
