package com.example.xinhe002614.modbustest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ElectricFragment extends Fragment {
    private View mRoot;
    private Context context;
    private TextView coil_elect_1, coil_elect_2, input_elect, input_voltage, BUCK_voltage, BUCK_elect, temp;

    public static ElectricFragment newInstance(int index) {
        ElectricFragment fragment = new ElectricFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("index", index);
        fragment.setArguments(arguments);
        return fragment;
    }

    public ElectricFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_electric, container, false);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();
        initView();
    }

    public void initView() {
        coil_elect_1 = (TextView) mRoot.findViewById(R.id.coil_elect_1);
        coil_elect_2 = (TextView) mRoot.findViewById(R.id.coil_elect_2);
        input_elect = (TextView) mRoot.findViewById(R.id.input_elect);
        input_voltage = (TextView) mRoot.findViewById(R.id.input_voltage);
        BUCK_voltage = (TextView) mRoot.findViewById(R.id.BUCK_voltage);
        BUCK_elect = (TextView) mRoot.findViewById(R.id.BUCK_elect);
        temp = (TextView) mRoot.findViewById(R.id.temp);
    }
}
