package com.example.xinhe002614.modbustest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class EfficientFragment extends Fragment {
    private View mRoot;
    private Context context;
    private TextView mail_text, input_rate, output_rate, sys_efficient;
    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private PointValue value;
    private PointValue last_value;
    private Timer timer;
    private Axis axisY;
    private int real_time = 0;
    private Random random;
    private Line line;
    private LineChartData data;
    private Viewport port;

    public static EfficientFragment newInstance(int index) {
        EfficientFragment fragment = new EfficientFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("index", index);
        fragment.setArguments(arguments);
        return fragment;
    }

    public EfficientFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_efficient, container, false);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getActivity();
        initView();
        showChangeLineChart();
    }

    /**
     * 这个是模拟实时获取数据的一个计时器，大家可以根据实际情况修改
     */
    private void showChangeLineChart() {
        timer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //实时添加新的点
                if (real_time < 41) {
                    value = new PointValue(real_time, random.nextInt(100));
                    pointValueList.add(value);
                    Log.d("List add", " " + real_time);
                } else {
                    for (int i = 0; i < 40; i++) {
                        pointValueList.get(i).set(pointValueList.get(i).getX(), pointValueList.get(i + 1).getY());
                    }
                    pointValueList.remove(40);
                    last_value.set(40, random.nextInt(100));
                    pointValueList.add(last_value);
                }
                //根据新的点的集合画出新的线
                line.setValues(pointValueList);
                linesList.clear();
                linesList.add(line);
                lineChartData = initDatas(linesList);
                lineChartView.setLineChartData(lineChartData);
                if (real_time < 41) real_time++;
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                timer.schedule(timerTask, 50, 50);
            }
        });
        thread.run();
    }

    public void initView() {
        mail_text = (TextView) mRoot.findViewById(R.id.mail_text);
        input_rate = (TextView) mRoot.findViewById(R.id.input_rate);
        output_rate = (TextView) mRoot.findViewById(R.id.output_rate);
        sys_efficient = (TextView) mRoot.findViewById(R.id.sys_efficient);
        lineChartView = (LineChartView) mRoot.findViewById(R.id.linechartview);
        initData();
        axisY.setTextColor(Color.parseColor("#ffffff"));
        Viewport port = initViewPort(0, 40);
        lineChartData = initDatas(null);
        lineChartView.setLineChartData(lineChartData);
        lineChartView.setMaximumViewport(port);
        lineChartView.setCurrentViewport(port);
        lineChartView.setInteractive(false);
        lineChartView.setScrollEnabled(false);
        lineChartView.setValueTouchEnabled(false);
        lineChartView.setFocusableInTouchMode(false);
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.startDataAnimation();
    }

    private LineChartData initDatas(List<Line> lines) {
        data.setLines(lines);
        data.setAxisYLeft(axisY);
        return data;
    }

    private Viewport initViewPort(float left, float right) {
        port.top = 100;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void initData() {
        last_value = new PointValue();
        pointValueList = new ArrayList<>();
        linesList = new ArrayList<>();
        axisY = new Axis();
        port = new Viewport();
        data = new LineChartData();
        line = new Line()
                .setColor(Color.WHITE)
                .setShape(ValueShape.CIRCLE)
                .setPointRadius(1)// 设置节点半径
                .setCubic(true);//曲线是否平滑，即是曲线还是折线
        random = new Random();
    }
}
