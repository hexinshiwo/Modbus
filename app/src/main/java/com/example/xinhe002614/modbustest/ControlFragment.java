package com.example.xinhe002614.modbustest;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.xinhe002614.modbustest.Unit.SocketUnit;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;

import static com.example.xinhe002614.modbustest.MainActivity.socket_11;

/**
 * Created by Whisper on 2017/4/25.
 */

public class ControlFragment extends Fragment {
    private View mRoot;
    private Context context;
    private SocketUnit socketUnit;
    private WheelView speed_select;
    private Button forward_btn, backward_btn, stop_btn, coil_up, coil_down, cruise_control;
    private static final byte[] MOVE_FORWARD = {0x3A, 0x05, 0x06, 0x00, 0x30, 0x7F, 0x00};//前进
    private static final byte[] MOVE_BACKWARD = {0x3A, 0x05, 0x06, 0x00, 0x30, 0x00, 0x00};//后退
    private static final byte[] STOP = {0x3A, 0x05, 0x06, 0x00, 0x31, 0x7F, 0x00};//刹车
    private static final byte[] COIL_UP = {0x3A, 0x05, 0x06, 0x00, 0x33, 0x7F, 0x00};//线圈升
    private static final byte[] COIL_DOWN = {0x3A, 0x05, 0x06, 0x00, 0x33, 0x00, 0x00};//线圈降
    private static final byte[] OPEN_CRUISE_CONTROL = {0x3A, 0x05, 0x06, 0x00, 0x34, 0x7F, 0x00};//定速巡航开
    private static final byte[] SHUT_CRUISE_CONTROL = {0x3A, 0x05, 0x06, 0x00, 0x34, 0x00, 0x00};//定速巡航关
    private static final byte[] SPEED = {0x3A, 0x0E, 0x10, 0x00, 0x35, 0x00, 0x04, 0x08};//发速度
    private int cruise = 0;

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
        initView();
    }

    public void initView() {
        socketUnit = new SocketUnit(context);
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
                    socketUnit.sendControl(socket_11, MOVE_FORWARD);
                    socketUnit.sendControl(socket_11, byteMerger(SPEED, doubleToBytes(speed_select.getSelection())));
                    break;
                case R.id.backward_btn:
                    socketUnit.sendControl(socket_11, MOVE_BACKWARD);
                    socketUnit.sendControl(socket_11, byteMerger(SPEED, doubleToBytes(speed_select.getSelection())));
                    break;
                case R.id.stop_btn:
                    socketUnit.sendControl(socket_11, STOP);
                    break;
                case R.id.coil_up:
                    socketUnit.sendControl(socket_11, COIL_UP);
                    break;
                case R.id.coil_down:
                    socketUnit.sendControl(socket_11, COIL_DOWN);
                    break;
                case R.id.cruise_control:
                    if (cruise == 0) {
                        cruise_control.setText(R.string.cruise_control_open);
                        socketUnit.sendControl(socket_11, OPEN_CRUISE_CONTROL);
                        cruise = 1;
                    } else {
                        cruise_control.setText(R.string.cruise_control_close);
                        socketUnit.sendControl(socket_11, SHUT_CRUISE_CONTROL);
                        cruise = 0;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static byte[] doubleToBytes(double d) {
        return longToBytes(Double.doubleToLongBits(d));
    }

    public static byte[] longToBytes(long l) {
        byte[] b = new byte[8];
        b[7] = (byte) (l >>> 56);
        b[6] = (byte) (l >>> 48);
        b[5] = (byte) (l >>> 40);
        b[4] = (byte) (l >>> 32);
        b[3] = (byte) (l >>> 24);
        b[2] = (byte) (l >>> 16);
        b[1] = (byte) (l >>> 8);
        b[0] = (byte) (l);
        return b;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
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
