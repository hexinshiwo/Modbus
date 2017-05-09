package com.example.xinhe002614.modbustest.Unit;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.example.xinhe002614.modbustest.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static com.example.xinhe002614.modbustest.Unit.CommonUnit.showToast;

/**
 * Created by xinhe002614 on 2017/5/7.
 */

public class SocketUnit {
    private Vector<ConnectSocket> vector = new Vector();
    public static int TAG = 0;//标志位
    public Context context;
    public static final byte[] DATE_FROM_COIL = null;
    public static final byte[] REQ_PRIMARY_COIL = {0x3A, 0x05, 0x04, 0x00, 0x00, 0x00, 0x10};//向原边寄存器发送的命令
    public static final byte[] REQ_SECOND_COIL = {0x3A, 0x05, 0x04, 0x00, 0x10, 0x00, 0x18};//向副边寄存器发送的命令

    public SocketUnit(int tag, Context context) {
        this.TAG = tag;
        this.context = context;
    }

    public void connect(Socket socket) {
        try {
            ServerSocket e = new ServerSocket(3325);
            Looper.prepare();
            System.out.println("Waiting");

            socket = e.accept();

            System.out.println("Connecting");
            showToast(context, "连接成功", Toast.LENGTH_LONG);
            Looper.loop();
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    public void add(ConnectSocket csSocket) {
        this.vector.add(csSocket);
    }

    public byte[] getDataFromCoil() {
        return DATE_FROM_COIL;
    }
}
