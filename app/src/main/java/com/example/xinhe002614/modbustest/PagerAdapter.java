package com.example.xinhe002614.modbustest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Whisper on 2017/4/20.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    List<Fragment> contents = null;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setContents(List<Fragment> list) {
        this.contents = list;
    }

    @Override
    public Fragment getItem(int position) {
        return contents.get(position);
    }

    @Override
    public int getCount() {
        return contents.size();
    }
}