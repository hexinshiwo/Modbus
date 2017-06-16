package com.example.xinhe002614.modbustest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Whisper on 2017/6/15.
 */

public class TrackFragment extends Fragment {
    private Context context;
    private View mRoot;
    private TextView track_1, track_2, track_3, track_4, track_5, track_6, track_7, track_8, track_9, track_10;
    private LinearLayout left_track, right_track;

    public static TrackFragment newInstance(int index) {
        TrackFragment fragment = new TrackFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("index", index);
        fragment.setArguments(arguments);
        return fragment;
    }

    public TrackFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_track, container, false);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();
        initView();
    }

    private void initView() {
        left_track = (LinearLayout) getActivity().findViewById(R.id.left_track);
        right_track = (LinearLayout) getActivity().findViewById(R.id.right_track);
        track_1 = (TextView) getActivity().findViewById(R.id.track_1);
        track_2 = (TextView) getActivity().findViewById(R.id.track_2);
        track_3 = (TextView) getActivity().findViewById(R.id.track_3);
        track_4 = (TextView) getActivity().findViewById(R.id.track_4);
        track_5 = (TextView) getActivity().findViewById(R.id.track_5);
        track_6 = (TextView) getActivity().findViewById(R.id.track_6);
        track_7 = (TextView) getActivity().findViewById(R.id.track_7);
        track_8 = (TextView) getActivity().findViewById(R.id.track_8);
        track_9 = (TextView) getActivity().findViewById(R.id.track_9);
        track_10 = (TextView) getActivity().findViewById(R.id.track_10);
    }
}
