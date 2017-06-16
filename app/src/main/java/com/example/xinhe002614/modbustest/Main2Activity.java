package com.example.xinhe002614.modbustest;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);
        initView();
    }

    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setOffscreenPageLimit(3);
        List<Fragment> list = new ArrayList<Fragment>();
        list.add(TrackFragment.newInstance(0));
        list.add(ControlFragment2.newInstance(1));
        list.add(SettingFragment2.newInstance(2));
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.setContents(list);
        viewPager.setAdapter(pagerAdapter);
    }
}
