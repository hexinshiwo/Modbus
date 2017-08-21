package com.example.xinhe002614.modbustest;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.xinhe002614.modbustest.Unit.SocketUnit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static byte[] OPEN_POWER = {0x3A, 0x05, 0x06, 0x00, 0x32, 0x7F, 0x00};//开电源
    private final static byte[] SHUT_POWER = {0x3A, 0x05, 0x06, 0x00, 0x32, 0x00, 0x00};//关电源
    private SocketUnit socketUnit;
    private SQLiteDatabase modbus;
    private PagerAdapter pagerAdapter;
    private TextView input_rate, input_elect, coil_elect_1, input_voltage, output_rate, coil_elect_2, BUCK_voltage, BUCK_elect, temp;
    private TextView track_1, track_2, track_3, track_4, track_5, track_6, track_7, track_8, track_9, track_10;
    private Socket socket_1 = null, socket_2 = null, socket_3 = null, socket_4 = null, socket_5 = null,
            socket_6 = null, socket_7 = null, socket_8 = null, socket_9 = null, socket_10 = null;
    public static Socket socket_11 = null;
    private String ip_1, ip_2, ip_3, ip_4, ip_5, ip_6, ip_7, ip_8, ip_9, ip_10, ip_11;
    private int port_1, port_2, port_3, port_4, port_5, port_6, port_7, port_8, port_9, port_10, port_11;
    private TimerTask connect_timertask_1, connect_timertask_2, connect_timertask_3,
            connect_timertask_4, connect_timertask_5, connect_timertask_6, connect_timertask_7,
            connect_timertask_8, connect_timertask_9, connect_timertask_10, connect_timertask_11;
    private Timer connect_timer_1, connect_timer_2, connect_timer_3, connect_timer_4, connect_timer_5,
            connect_timer_6, connect_timer_7, connect_timer_8, connect_timer_9, connect_timer_10, connect_timer_11;
    private ServerSocket serSoc_1, serSoc_2, serSoc_3, serSoc_4, serSoc_5, serSoc_6, serSoc_7, serSoc_8, serSoc_9, serSoc_10, serSoc_11;
    private Timer timer;
    private ViewPager viewPager;
    private CheckBox power_switch;
    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private PointValue value;
    private PointValue last_value;
    private Axis axisY, axisX;
    private int real_time = 0;
    private Random random;
    private Line line;
    private LineChartData data;
    private Viewport port;
    private final static int record_num = 200;
    public static Handler handler;
    public final static int MSG_READ_COIL_1 = 11;
    public final static int MSG_READ_COIL_2 = 12;
    public final static int MSG_TRACK1 = 1, MSG_TRACK2 = 2, MSG_TRACK3 = 3, MSG_TRACK4 = 4, MSG_TRACK5 = 5,
            MSG_TRACK6 = 6, MSG_TRACK7 = 7, MSG_TRACK8 = 8, MSG_TRACK9 = 9, MSG_TRACK10 = 10, MSG_SAVE = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        modbus = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString() + "/modbus.db3", null);
        try {
            modbus.execSQL("create table ip_table(" + "_id integer primary key autoincrement,"
                    + "ip_address varchar(20)," + "port int(10))");
            initDatabase();
        } catch (SQLiteException se) {
            Log.w("Exception", se.toString());
        }
        initview();
        initHandler();
        getIpAndPort();
        initSerSoc();
        createTask();
        //showChangeLineChart();
    }

    public void initview() {
        socketUnit = new SocketUnit(MainActivity.this);
        new_timer();
        timer = new Timer();
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
    }

    private void initHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_READ_COIL_1:
                        readcoil1();
                        break;
                    case MSG_READ_COIL_2:
                        readcoil2();
                        changeSpeed();
                        break;
                    case MSG_TRACK1:
                        startTranslation(track_1, socket_1);
                        break;
                    case MSG_TRACK2:
                        startTranslation(track_2, socket_2);
                        break;
                    case MSG_TRACK3:
                        startTranslation(track_3, socket_3);
                        break;
                    case MSG_TRACK4:
                        startTranslation(track_4, socket_4);
                        break;
                    case MSG_TRACK5:
                        startTranslation(track_5, socket_5);
                        break;
                    case MSG_TRACK6:
                        startTranslation(track_6, socket_6);
                        break;
                    case MSG_TRACK7:
                        startTranslation(track_7, socket_7);
                        break;
                    case MSG_TRACK8:
                        startTranslation(track_8, socket_8);
                        break;
                    case MSG_TRACK9:
                        startTranslation(track_9, socket_9);
                        break;
                    case MSG_TRACK10:
                        startTranslation(track_10, socket_10);
                        break;
                    case MSG_SAVE:
                        closeSocket();
                        getIpAndPort();
                        initSerSoc();
                        createTask();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void readcoil1() {
        input_rate.setText(socketUnit.Sinput_rate);
        coil_elect_1.setText(socketUnit.Scoil_elect_1);
        input_elect.setText(socketUnit.Sinput_elect);
        input_voltage.setText(socketUnit.Sinput_voltage);
    }

    private void readcoil2() {
        output_rate.setText(socketUnit.Soutput_rate);
        coil_elect_2.setText(socketUnit.Scoil_elect_2);
        BUCK_voltage.setText(socketUnit.SBUCK_voltage);
        BUCK_elect.setText(socketUnit.SBUCK_elect);
        temp.setText(socketUnit.Stemp);
    }

    private void changeSpeed() {
        if (real_time <= record_num) {
            value = new PointValue(real_time, socketUnit.Sspeed);
            pointValueList.add(value);
            real_time++;
        } else {
            for (int i = 0; i < record_num; i++) {
                pointValueList.get(i).set(pointValueList.get(i).getX(), pointValueList.get(i + 1).getY());
            }
            pointValueList.remove(record_num);
            last_value.set(record_num, socketUnit.Sspeed);
            pointValueList.add(last_value);
        }
        //根据新的点的集合画出新的线
        line.setValues(pointValueList);
        linesList.clear();
        linesList.add(line);
        lineChartData = initDatas(linesList);
        lineChartView.setLineChartData(lineChartData);
    }

    /**
     * 这个是模拟实时获取数据的一个计时器，可以根据实际情况修改
     */
    private void showChangeLineChart() {
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //实时添加新的点
                if (real_time <= record_num) {
                    value = new PointValue(real_time, random.nextInt(20));
                    pointValueList.add(value);
                    real_time++;
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
            }
        };
        timer.schedule(timerTask, 50, 50);
    }

    public void BindView() {
        input_rate = (TextView) findViewById(R.id.input_rate);
        input_elect = (TextView) findViewById(R.id.input_elect);
        coil_elect_1 = (TextView) findViewById(R.id.coil_elect_1);
        input_voltage = (TextView) findViewById(R.id.input_voltage);
        output_rate = (TextView) findViewById(R.id.output_rate);
        coil_elect_2 = (TextView) findViewById(R.id.coil_elect_2);
        BUCK_voltage = (TextView) findViewById(R.id.BUCK_voltage);
        BUCK_elect = (TextView) findViewById(R.id.BUCK_elect);
        temp = (TextView) findViewById(R.id.temp);
        power_switch = (CheckBox) findViewById(R.id.power_switch);
        power_switch.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.power_switch:
                if (!power_switch.isChecked()) {
                    socketUnit.sendControl(socket_11, SHUT_POWER);
                } else {
                    socketUnit.sendControl(socket_11, OPEN_POWER);
                }
                break;
        }
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
        Log.d("show now", "onDestroy");
        super.onDestroy();
        timer.cancel();
        cancel_timer();
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
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3325});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3326});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3327});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3328});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3329});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3330});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3331});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3332});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3333});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3334});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new Object[]{"0.0.0.0", 3335});
    }

    public void getIpAndPort() {
        Cursor cur = modbus.rawQuery("select * from ip_table", null);
        if (cur.moveToNext()) {
            ip_1 = cur.getString(1);
            port_1 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_2 = cur.getString(1);
            port_2 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_3 = cur.getString(1);
            port_3 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_4 = cur.getString(1);
            port_4 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_5 = cur.getString(1);
            port_5 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_6 = cur.getString(1);
            port_6 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_7 = cur.getString(1);
            port_7 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_8 = cur.getString(1);
            port_8 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_9 = cur.getString(1);
            port_9 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_10 = cur.getString(1);
            port_10 = cur.getInt(2);
        }
        if (cur.moveToNext()) {
            ip_11 = cur.getString(1);
            port_11 = cur.getInt(2);
        }
        cur.close();
    }

    private void initSerSoc() {
        try {
            serSoc_1 = new ServerSocket(port_1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_2 = new ServerSocket(port_2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_3 = new ServerSocket(port_3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_4 = new ServerSocket(port_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_5 = new ServerSocket(port_5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_6 = new ServerSocket(port_6);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_7 = new ServerSocket(port_7);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_8 = new ServerSocket(port_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_9 = new ServerSocket(port_9);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_10 = new ServerSocket(port_10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serSoc_11 = new ServerSocket(port_11);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancel_timer() {
        connect_timer_1.cancel();
        connect_timer_2.cancel();
        connect_timer_3.cancel();
        connect_timer_4.cancel();
        connect_timer_5.cancel();
        connect_timer_6.cancel();
        connect_timer_7.cancel();
        connect_timer_8.cancel();
        connect_timer_9.cancel();
        connect_timer_10.cancel();
        connect_timer_11.cancel();
    }

    private void new_timer() {
        connect_timer_1 = new Timer();
        connect_timer_2 = new Timer();
        connect_timer_3 = new Timer();
        connect_timer_4 = new Timer();
        connect_timer_5 = new Timer();
        connect_timer_6 = new Timer();
        connect_timer_7 = new Timer();
        connect_timer_8 = new Timer();
        connect_timer_9 = new Timer();
        connect_timer_10 = new Timer();
        connect_timer_11 = new Timer();
    }

    private void createTask() {
        connect_timertask_1 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK1;
                if (socket_1 == null) {
                    handler.sendMessage(msg);
                    socket_1 = socketUnit.connect(serSoc_1);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK1;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_1 = socketUnit.sendRequest(socket_1, 0);
                }
            }
        };
        connect_timertask_2 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK2;
                if (socket_2 == null) {
                    handler.sendMessage(msg);
                    socket_2 = socketUnit.connect(serSoc_2);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK2;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_2 = socketUnit.sendRequest(socket_2, 0);
                }
            }
        };
        connect_timertask_3 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK3;
                if (socket_3 == null) {
                    handler.sendMessage(msg);
                    socket_3 = socketUnit.connect(serSoc_3);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK3;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_3 = socketUnit.sendRequest(socket_3, 0);
                }
            }
        };
        connect_timertask_4 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK4;
                if (socket_4 == null) {
                    handler.sendMessage(msg);
                    socket_4 = socketUnit.connect(serSoc_4);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK4;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_4 = socketUnit.sendRequest(socket_4, 0);
                }
            }
        };
        connect_timertask_5 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK5;
                if (socket_5 == null) {
                    handler.sendMessage(msg);
                    socket_5 = socketUnit.connect(serSoc_5);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK5;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_5 = socketUnit.sendRequest(socket_5, 0);
                }
            }
        };
        connect_timertask_6 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK6;
                if (socket_6 == null) {
                    handler.sendMessage(msg);
                    socket_6 = socketUnit.connect(serSoc_6);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK6;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_6 = socketUnit.sendRequest(socket_6, 0);
                }
            }
        };
        connect_timertask_7 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK7;
                if (socket_7 == null) {
                    handler.sendMessage(msg);
                    socket_7 = socketUnit.connect(serSoc_7);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK7;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_7 = socketUnit.sendRequest(socket_7, 0);
                }
            }
        };
        connect_timertask_8 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK8;
                if (socket_8 == null) {
                    handler.sendMessage(msg);
                    socket_8 = socketUnit.connect(serSoc_8);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK8;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_8 = socketUnit.sendRequest(socket_8, 0);
                }
            }
        };
        connect_timertask_9 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK9;
                if (socket_9 == null) {
                    handler.sendMessage(msg);
                    socket_9 = socketUnit.connect(serSoc_9);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK9;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_9 = socketUnit.sendRequest(socket_9, 0);
                }
            }
        };
        connect_timertask_10 = new TimerTask() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = MSG_TRACK10;
                if (socket_10 == null) {
                    handler.sendMessage(msg);
                    socket_10 = socketUnit.connect(serSoc_10);
                    msg = Message.obtain();
                    msg.what = MSG_TRACK10;
                    handler.sendMessage(msg);
                } else {
                    handler.sendMessage(msg);
                    socket_10 = socketUnit.sendRequest(socket_10, 0);
                }
            }
        };
        connect_timertask_11 = new TimerTask() {
            @Override
            public void run() {
                if (socket_11 == null)
                    socket_11 = socketUnit.connect(serSoc_11);
                else
                    socket_11 = socketUnit.sendRequest(socket_11, 1);
            }
        };
        new_timer();
        connect_timer_1.schedule(connect_timertask_1, 50, 500);
        connect_timer_2.schedule(connect_timertask_2, 50, 500);
        connect_timer_3.schedule(connect_timertask_3, 50, 500);
        connect_timer_4.schedule(connect_timertask_4, 50, 500);
        connect_timer_5.schedule(connect_timertask_5, 50, 500);
        connect_timer_6.schedule(connect_timertask_6, 50, 500);
        connect_timer_7.schedule(connect_timertask_7, 50, 500);
        connect_timer_8.schedule(connect_timertask_8, 50, 500);
        connect_timer_9.schedule(connect_timertask_9, 50, 500);
        connect_timer_10.schedule(connect_timertask_10, 50, 500);
        connect_timer_11.schedule(connect_timertask_11, 50, 500);
    }

    private void closeSocket() {
        try {
            if (socket_1 != null)
                socket_1.close();
            serSoc_1.close();
            socket_1 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_2 != null)
                socket_2.close();
            serSoc_2.close();
            socket_2 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_3 != null)
                socket_3.close();
            serSoc_3.close();
            socket_3 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_4 != null)
                socket_4.close();
            serSoc_4.close();
            socket_4 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_5 != null)
                socket_5.close();
            serSoc_5.close();
            socket_5 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_6 != null)
                socket_6.close();
            serSoc_6.close();
            socket_6 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_7 != null)
                socket_7.close();
            serSoc_7.close();
            socket_7 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_8 != null)
                socket_8.close();
            serSoc_8.close();
            socket_8 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_9 != null)
                socket_9.close();
            serSoc_9.close();
            socket_9 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_10 != null)
                socket_10.close();
            serSoc_10.close();
            socket_10 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket_11 != null)
                socket_11.close();
            serSoc_11.close();
            socket_11 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTranslation(TextView view, Socket socket) {
        Drawable background = view.getBackground();
        ColorDrawable colorDrawable = (ColorDrawable) background;
        int colorA = colorDrawable.getColor();
        int colorB;
        if (socket == null)
            colorB = Color.GRAY;
        else if (socket.getPort() == socketUnit.mport) {
            colorB = Color.parseColor("#48a6e6");
        } else
            colorB = Color.GREEN;
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(view, "backgroundColor", colorA, colorB);
        objectAnimator.setDuration(500);
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.start();
    }
}
