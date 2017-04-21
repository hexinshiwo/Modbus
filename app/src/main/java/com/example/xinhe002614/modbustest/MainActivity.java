package com.example.xinhe002614.modbustest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button forward_btn, backward_btn, stop_btn, coil_up, coil_down, cruise_control;
    TextView power_describ;
    CheckBox power_switch;
    private ViewPager viewPager = null;
    private PagerAdapter pagerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initview();
    }

    public void initview() {
        initWheel();
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setOffscreenPageLimit(2);
        List<Fragment> list = new ArrayList<Fragment>();
        list.add(EfficientFragment.newInstance(0));
        list.add(ElectricFragment.newInstance(1));
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.setContents(list);
        viewPager.setAdapter(pagerAdapter);
        power_describ = (TextView) findViewById(R.id.power_describ);
        power_switch = (CheckBox) findViewById(R.id.power_switch);
        forward_btn = (Button) findViewById(R.id.forward_btn);
        backward_btn = (Button) findViewById(R.id.backward_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);
        coil_up = (Button) findViewById(R.id.coil_up);
        coil_down = (Button) findViewById(R.id.coil_down);
        cruise_control = (Button) findViewById(R.id.cruise_control);
        power_switch.setOnClickListener(this);
        forward_btn.setOnClickListener(this);
        backward_btn.setOnClickListener(this);
        stop_btn.setOnClickListener(this);
        coil_up.setOnClickListener(this);
        coil_down.setOnClickListener(this);
        cruise_control.setOnClickListener(this);

        power_describ.setText("小车未启动");
        power_describ.setTextColor(Color.parseColor("#CD3700"));
    }

    private void initWheel() {
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextColor = Color.parseColor("#0288ce");
        style.textColor = Color.parseColor("#0288ce");
        style.textSize = 12;
        style.backgroundColor = Color.parseColor("#666666");
        style.selectedTextSize = 26;
        WheelView speed_select = (WheelView) findViewById(R.id.speed_select);
        speed_select.setWheelAdapter(new ArrayWheelAdapter(this));
        speed_select.setSkin(WheelView.Skin.Holo);
        speed_select.setLoop(false);
        speed_select.setStyle(style);
        speed_select.setWheelData(createSpeed());
        speed_select.setSelection(31);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.power_switch:
                if (power_switch.isChecked()) {
                    power_describ.setText("小车已启动");
                    power_describ.setTextColor(Color.parseColor("#BCEE68"));
                } else {
                    power_describ.setText("小车未启动");
                    power_describ.setTextColor(Color.parseColor("#CD3700"));
                }
                break;
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

                break;
        }
    }

    // 速度滚轮数据列表适配器
    private ArrayList<String> createSpeed() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 30; i >= 0; i--) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
        return list;
    }
}
