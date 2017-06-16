package com.example.xinhe002614.modbustest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Whisper on 2017/6/15.
 */

public class SettingFragment2 extends Fragment {
    private Context context;
    private View mRoot;

    public static SettingFragment2 newInstance(int index) {
        SettingFragment2 fragment = new SettingFragment2();
        Bundle arguments = new Bundle();
        arguments.putInt("index", index);
        fragment.setArguments(arguments);
        return fragment;
    }

    public SettingFragment2() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_setting2, container, false);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();
        initView();
    }

    private void initView() {

    }
}
