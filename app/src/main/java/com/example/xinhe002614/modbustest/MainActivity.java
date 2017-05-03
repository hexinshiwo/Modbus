package com.example.xinhe002614.modbustest;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.xinhe002614.modbustest.Unit.ModbusUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase modbus;
    private PagerAdapter pagerAdapter;
    private TextView input_rate, inpute_elect, coil_elect_1, input_voltage, output_rate, coil_elect_2, BUCK_voltage, BUCK_elect, temp;
    private TextView track_1, track_2, track_3, track_4, track_5, track_6, track_7, track_8, track_9, track_10;
    private ViewPager viewPager;
    private CheckBox power_switch;
    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private PointValue value;
    private PointValue last_value;
    private Timer timer;
    private Axis axisY, axisX;
    private int real_time = 0;
    private Random random;
    private Line line;
    private LineChartData data;
    private Viewport port;
    private static int record_num = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        modbus = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/modbus.db3", null);
        try {//创建抢红包数据表
            modbus.execSQL("create table ip_table(" + "_id integer primary key autoincrement,"
                    + "ip_address varchar(20)," + "port varchar(20))");
            initDatabase();
        } catch (SQLiteException se) {
            Log.w("Exception", se.toString());
        }
        initview();
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
                if (real_time <= record_num) {
                    value = new PointValue(real_time, random.nextInt(20));
                    pointValueList.add(value);
                } else {
                    for (int i = 0; i < record_num; i++) {
                        pointValueList.get(i).set(pointValueList.get(i).getX(), pointValueList.get(i + 1).getY());
                    }
                    pointValueList.remove(record_num);
                    last_value.set(record_num, random.nextInt(20));
                    pointValueList.add(last_value);
                }
                //根据新的点的集合画出新的线
                line.setValues(pointValueList);
                linesList.clear();
                linesList.add(line);
                lineChartData = initDatas(linesList);
                lineChartView.setLineChartData(lineChartData);
                if (real_time <= record_num) real_time++;
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

    public void initview() {
        BindView();
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setOffscreenPageLimit(2);
        List<Fragment> list = new ArrayList<Fragment>();
        list.add(ControlFragment.newInstance(0));
        list.add(SettingFragment.newInstance(1));
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.setContents(list);
        viewPager.setAdapter(pagerAdapter);

        lineChartView = (LineChartView) findViewById(R.id.linechartview);
        initData();
        axisY.setTextColor(Color.parseColor("#ffffff"));
        axisX.setTextColor(Color.parseColor("#ffffff"));
        axisY.setHasLines(true);
        Viewport port = initViewPort(0, record_num);
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
        ModbusUnit.createThreadPoolSendAllQequest("手动输入ip地址",8888,1);//开始连接服务器
    }

    public void BindView() {
        input_rate = (TextView) findViewById(R.id.input_rate);
        inpute_elect = (TextView) findViewById(R.id.input_elect);
        coil_elect_1 = (TextView) findViewById(R.id.coil_elect_1);
        input_voltage = (TextView) findViewById(R.id.input_voltage);
        output_rate = (TextView) findViewById(R.id.output_rate);
        coil_elect_2 = (TextView) findViewById(R.id.coil_elect_2);
        BUCK_voltage = (TextView) findViewById(R.id.BUCK_voltage);
        BUCK_elect = (TextView) findViewById(R.id.BUCK_elect);
        temp = (TextView) findViewById(R.id.temp);
        power_switch = (CheckBox) findViewById(R.id.power_switch);

        track_1 = (TextView) findViewById(R.id.track_1);
        track_2 = (TextView) findViewById(R.id.track_2);
        track_3 = (TextView) findViewById(R.id.track_3);
        track_4 = (TextView) findViewById(R.id.track_4);
        track_5 = (TextView) findViewById(R.id.track_5);
        track_6 = (TextView) findViewById(R.id.track_6);
        track_7 = (TextView) findViewById(R.id.track_7);
        track_8 = (TextView) findViewById(R.id.track_8);
        track_9 = (TextView) findViewById(R.id.track_9);
        track_10 = (TextView) findViewById(R.id.track_10);
    }

    private LineChartData initDatas(List<Line> lines) {
        data.setLines(lines);
        data.setAxisYLeft(axisY);
        //data.setAxisXBottom(axisX);
        return data;
    }

    private Viewport initViewPort(float left, float right) {
        port.top = 20;
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
        axisX = new Axis();
        port = new Viewport();
        data = new LineChartData();
        line = new Line()
                .setColor(Color.parseColor("#6CD0E8"))
                .setShape(ValueShape.CIRCLE)
                .setHasPoints(false)// 是否显示节点
                .setCubic(true);//曲线是否平滑，即是曲线还是折线
        random = new Random();
    }

    public void initDatabase() {
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6901"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6902"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6903"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6904"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6905"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6906"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6907"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6908"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6909"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6910"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"192.168.1.255", "6911"});
    }
}
