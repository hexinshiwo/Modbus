package com.example.xinhe002614.modbustest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinhe002614.modbustest.Unit.SocketUnit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static com.example.xinhe002614.modbustest.Unit.CommonUnit.showToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static byte[] REQ_PRIMARY_COIL = {0x3A, 0x05, 0x04, 0x00, 0x00, 0x00, 0x10};//向原边寄存器发送的命令
    private static byte[] REQ_SECOND_COIL = {0x3A, 0x05, 0x04, 0x00, 0x10, 0x00, 0x18};//向副边寄存器发送的命令
    private SocketUnit socketUnit;
    private SQLiteDatabase modbus;
    private PagerAdapter pagerAdapter;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private DataInputStream dis;
    private DataOutputStream dos;
    private byte readBuffer[] = new byte[80];
    private TextView input_rate, inpute_elect, coil_elect_1, input_voltage, output_rate, coil_elect_2, BUCK_voltage, BUCK_elect, temp;
    private TextView track_1, track_2, track_3, track_4, track_5, track_6, track_7, track_8, track_9, track_10;
    private Socket socket_1, socket_2, socket_3, socket_4, socket_5, socket_6, socket_7, socket_8, socket_9, socket_10, socket_11;
    private String ip_1, ip_2, ip_3, ip_4, ip_5, ip_6, ip_7, ip_8, ip_9, ip_10, ip_11;
    private int port_1, port_2, port_3, port_4, port_5, port_6, port_7, port_8, port_9, port_10, port_11;
    private ViewPager viewPager;
    private CheckBox power_switch;
    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private PointValue value;
    private PointValue last_value;
    private Timer timer, connect_timer;
    private TimerTask connect_timertask;
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
        try {
            modbus.execSQL("create table ip_table(" + "_id integer primary key autoincrement,"
                    + "ip_address varchar(20)," + "port varchar(20))");
            initDatabase();
        } catch (SQLiteException se) {
            Log.w("Exception", se.toString());
        }
        initview();
        //showChangeLineChart();
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
        timer.schedule(timerTask, 50, 50);
    }

    public void initview() {
        connect_timer = new Timer();
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
                    connect_timer.cancel();
                } else {
                    showToast(this, "连接中", Toast.LENGTH_LONG);
                    new Thread() {
                        @Override
                        public void run() {
                            socketUnit = new SocketUnit(1, MainActivity.this);
                            socketUnit.connect(socket_1);
                        }
                    }.start();
                    createTask();
                    connect_timer = new Timer();
                    connect_timer.schedule(connect_timertask, 50, 500);
                }
                break;
        }
    }

    private void createTask() {
        connect_timertask = new TimerTask() {
            @Override
            public void run() {
//                sendData(socket_1, 0);
//                sendData(socket_2, 0);
//                sendData(socket_3, 0);
//                sendData(socket_4, 0);
//                sendData(socket_5, 0);
//                sendData(socket_6, 0);
//                sendData(socket_7, 0);
//                sendData(socket_8, 0);
//                sendData(socket_9, 0);
//                sendData(socket_10, 0);
//                sendData(socket_11, 1);
            }
        };
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
        connect_timer.cancel();
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
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
        modbus.execSQL("insert into ip_table(ip_address,port) values(?,?)", new String[]{"0.0.0.0", "0"});
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

    public void connect() {
        if (socket_1 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_1 = new Socket(ip_1, port_1);
                        socket_1.setSoTimeout(2000);
                        socket_1.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_2 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_2 = new Socket(ip_2, port_2);
                        socket_2.setSoTimeout(2000);
                        socket_2.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_3 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_3 = new Socket(ip_3, port_3);
                        socket_3.setSoTimeout(2000);
                        socket_3.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_4 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_4 = new Socket(ip_4, port_4);
                        socket_4.setSoTimeout(2000);
                        socket_4.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_5 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_5 = new Socket(ip_5, port_5);
                        socket_5.setSoTimeout(2000);
                        socket_5.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_6 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_6 = new Socket(ip_6, port_6);
                        socket_6.setSoTimeout(2000);
                        socket_6.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_7 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_7 = new Socket(ip_7, port_7);
                        socket_7.setSoTimeout(2000);
                        socket_7.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_8 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_8 = new Socket(ip_8, port_8);
                        socket_8.setSoTimeout(2000);
                        socket_8.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_9 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_9 = new Socket(ip_9, port_9);
                        socket_9.setSoTimeout(2000);
                        socket_9.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_10 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_10 = new Socket(ip_10, port_10);
                        socket_10.setSoTimeout(2000);
                        socket_10.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        if (socket_11 == null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        socket_11 = new Socket(ip_11, port_11);
                        socket_11.setSoTimeout(2000);
                        socket_11.setTcpNoDelay(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void sendData(Socket socket, int tag) {
        if (socket != null) {
            int count = 0;
            try {
                //oos = new ObjectOutputStream(socket.getOutputStream());
                dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                if (tag == 0) dos.write(REQ_PRIMARY_COIL);// 发送消息给原边
                else dos.write(REQ_SECOND_COIL);// 发送消息给副边
                //oos.flush();
                dos.flush();
                //ois = new ObjectInputStream(socket.getInputStream());
                dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//                DataInput dataInput = null;
//                dataInput = (DataInput) ois.readObject();
                count = dis.read(readBuffer);
                if (count != 0) receiveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveData() {
        showToast(this, "接收到数据" + Arrays.toString(readBuffer), Toast.LENGTH_LONG);
        try {
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
